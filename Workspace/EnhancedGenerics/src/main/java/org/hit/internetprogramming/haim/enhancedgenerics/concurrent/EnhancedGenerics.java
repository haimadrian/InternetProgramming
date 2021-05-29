package org.hit.internetprogramming.haim.enhancedgenerics.concurrent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;

/**
 * This class enables the conversion of both {@link Runnable} and {@link Callable} tasks
 * to a {@link PriorityRunnable} object.<br/>
 * A PriorityRunnable task wraps a Runnable and assigns it with a priority.<br/>
 * The various apply methods convert Runnable or Callable tasks and submit them (using the
 * offer method) to a {@link PriorityBlockingQueue}.<br/>
 * An instance of the EnhancedGenerics class creates a separate thread and while it is not stopped
 * tries to take a task from the queue and run it.
 *
 * @param <T> A Runnable task or an instance of a type that implements the Runnable interface
 * @author Nathan Dillbary, Haim Adrian
 */
public class EnhancedGenerics<T extends Runnable> implements ExecutorService {
    /**
     * A default value to use as fixed capacity of {@link #workQueue}
     */
    private static final int DEFAULT_WORK_QUEUE_CAPACITY = 10;

    /**
     * Use this in order to count instances of EnhancedGenerics, because we use
     * custom consumer thread name, and we would like to add an identifier to that name,
     * to differ each thread.
     */
    private static final AtomicInteger INSTANCE_COUNTER = new AtomicInteger();

    /**
     * Use a logger for debug information, to shade some light over the darkness of parallel work.
     */
    private static final Logger log = LogManager.getLogger(EnhancedGenerics.class);

    /**
     * Work queue to contain tasks for execution.<br/>
     * Work queue can be defined by user when using {@link #EnhancedGenerics(BlockingQueue, Function)}, or a
     * linked blocking queue with default fixed capacity of {@value #DEFAULT_WORK_QUEUE_CAPACITY}
     */
    private final BlockingQueue<T> workQueue;

    /**
     * A single thread which is being used in order to pull tasks out of {@link #workQueue} and execute them.
     */
    private final Thread consumerThread;

    /**
     * A function to use for conversion of {@link Runnable} to generic type {@code <T>}
     */
    private final Function<Runnable, T> defaultRunnableConverterFunction;

    /**
     * A {@link ReentrantReadWriteLock} to protect critical code parts from concurrent modification exceptions
     */
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * We hold a reference to the {@link #consumerThread} as long as it is waiting for new task, by
     * {@link BlockingQueue#take()}. Once it receives a task, this reference will refer to {@code null}.<br/>
     * The reason for holding a reference to the thread while it is idle, is to get the ability to interrupt the
     * thread when we are being instructed to {@link #shutdown()}. This differs from interrupting the thread when
     * it is not idle, since we can interrupt a working thread only when we are being instructed to {@link #shutdownNow()}.<br/>
     * Note: This reference is volatile cause we test it from {@link #stop}, and assigning it at consumer thread's
     * work. We would like to ensure the value is up to date, though we do not have to synchronize it, as a second
     * try would be ignored.
     */
    private volatile Thread idleThread;

    /**
     * A flag to indicate when user asks to stop this single thread pool, such that no additional tasks
     * will be offered, though we will wait for existing tasks execution to be completed.
     */
    private volatile boolean stop = false;

    /**
     * Same as {@link #stop}, except of the fact that this flag will force stopping execution of running tasks,
     * without waiting for existing tasks to be executed.<br/>
     * We use this flag twice. The second use of it is an indication for "TERMINATED" state.
     */
    private volatile boolean stopNow = false;

    /**
     * Constructs a new {@link EnhancedGenerics}
     *
     * @param runnableConverterFunction A mapping function to map runnable into type {@code <T>}
     * @throws NullPointerException In case {@code runnableConverterFunction} refers to {@code null}.
     */
    public EnhancedGenerics(Function<Runnable, T> runnableConverterFunction) throws NullPointerException {
        this(new LinkedBlockingQueue<>(DEFAULT_WORK_QUEUE_CAPACITY), runnableConverterFunction);
    }

    /**
     * Constructs a new {@link EnhancedGenerics}
     *
     * @param workQueue Some custom queue to hold jobs requested for execution
     * @param runnableConverterFunction A mapping function to map runnable into type {@code <T>}
     * @throws NullPointerException In case {@code workQueue} or {@code runnableConverterFunction} refer to {@code null}.
     */
    public EnhancedGenerics(BlockingQueue<T> workQueue,
                            Function<Runnable, T> runnableConverterFunction) throws NullPointerException {
        throwIfNull(workQueue, runnableConverterFunction);

        this.workQueue = workQueue;
        this.defaultRunnableConverterFunction = runnableConverterFunction;

        this.consumerThread = new Thread(
            () -> {
                try {
                    debug("Consumer thread started");
                    while ((!isShutdown() || !this.workQueue.isEmpty()) && (!isTerminated())) {
                        try {
                            // Separate take action from run action, cause the consumer thread is considered
                            // an idle thread as long as it is waiting for task. (Blocked by the take() action)
                            // When the thread is idle, we will interrupt it even when calling shutdown, and not
                            // necessarily shutdownNow. Otherwise, the idle thread might be left idle forever.
                            idleThread = Thread.currentThread();
                            T task = this.workQueue.take();
                            idleThread = null;

                            // take() is blocking, hence make sure we have not been instructed to shutdown while waiting.
                            // We cannot lock here since it would not let the shutdownNow to interrupt our thread.
                            // Note that we check stopNow only, and not "stop", because stop should wait for the task to finish.
                            if (!isTerminated()) {
                                debug("Start executing task: " + task);
                                task.run();
                                debug("End executing task.");
                            }
                        } catch (InterruptedException ignore) {
                            debug("Consumer thread interrupted");
                        } catch (Exception e) {
                            error("Uncaught exception while executing a task: " + e.getMessage(), e);
                        }
                    }
                } finally {
                    // When we exit the consumer thread loop, make sure we mark the thread pool as terminated,
                    // which indicates nothing can be submitted to this thread pool.
                    // Wait here until stop() finishes.
                    lock.writeLock().lock();
                    try {
                        debug("Setting state to TERMINATED");
                        stop = true;
                        stopNow = true;
                    } finally {
                        lock.writeLock().unlock();
                    }
                }
            }, "EnhancedGenericsConsumerThread-" + INSTANCE_COUNTER.incrementAndGet());

        this.consumerThread.start();
    }

    /**
     * @param objects pass unknown number of arguments passed in run-time
     * @throws NullPointerException When any of the arguments refer to null
     */
    private static void throwIfNull(Object... objects) throws NullPointerException {
        for (Object argument : objects) {
            if (argument == null) {
                throw new NullPointerException("one of the arguments is null");
            }
        }
    }

    /**
     * Submit a task as {@link Runnable}, using the default runnable conversion mapping function
     *
     * @param runnable The task to submit
     * @throws RejectedExecutionException In case this thread pool is {@link #stop(boolean) stopped} or queue is full.
     */
    public void apply(final Runnable runnable) throws RejectedExecutionException {
        apply(runnable, defaultRunnableConverterFunction);
    }

    /**
     * Submit a task as {@link Callable}, using the default runnable conversion mapping function.
     *
     * @param callable The task to submit
     * @return A {@link Future} representing pending completion of the task
     * @throws RejectedExecutionException In case this thread pool is {@link #stop(boolean) stopped} or queue is full.
     */
    public <V> Future<V> apply(final Callable<V> callable) throws RejectedExecutionException {
        return apply(callable, defaultRunnableConverterFunction);
    }

    /**
     * Submit a task as {@link Runnable}, using the specified runnable conversion mapping function.
     *
     * @param runnable The task to submit
     * @param runnableConverterFunction can be Null, if Null will use the default converter
     * @throws RejectedExecutionException In case this thread pool is {@link #stop(boolean) stopped} or queue is full.
     */
    public void apply(final Runnable runnable, Function<Runnable, T> runnableConverterFunction) throws RejectedExecutionException {
        // Lock, to make sure apply() and stop() do not run in parallel. (Use read lock as we read "stop")
        // We would like to ignore any task that arrives after stop(), or in parallel to stop().
        // Note that several threads can submit tasks in parallel, cause BlockingQueue is thread-safe,
        // by design. Hence we use readLock, so all of the threads will have access.
        lock.readLock().lock();
        try {
            // After calling stop, we reject all tasks
            if (!stop) {
                Function<Runnable, T> runnableConverterFunctionToUse = runnableConverterFunction;
                if (runnableConverterFunctionToUse == null) {
                    runnableConverterFunctionToUse = this.defaultRunnableConverterFunction;
                }

                debug("Submitting task: " + runnable + ". Current work queue size (before submit) is " + workQueue.size());

                // I'd prefer to use put(T) here, which blocks calling thread until there is space available, and
                // also throws InterruptedException, to follow the assignment's method declaration. Though we've agreed on
                // using offer(T), and throw RejectedExecutionException in case there is no space available.
                if (!workQueue.offer(runnableConverterFunction.apply(runnable))) {
                    throw new RejectedExecutionException("Cannot submit task. Reason: Thread pool's queue is full");
                }
            } else {
                throw new RejectedExecutionException("Cannot submit task. Reason: Thread pool is stopped");
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Submit a task as {@link Callable}, using the specified runnable conversion mapping function.
     *
     * @param callable The task to submit
     * @param runnableConverterFunction can be Null, if Null will use the default converter
     * @return A {@link Future} representing pending completion of the task
     * @throws RejectedExecutionException In case this thread pool is {@link #stop(boolean) stopped} or queue is full.
     */
    public <V> Future<V> apply(final Callable<V> callable, Function<Runnable, T> runnableConverterFunction) throws RejectedExecutionException {
        FutureTask<V> futureTask = new FutureTask<>(callable);
        apply(futureTask, runnableConverterFunction);
        return futureTask;
    }

    /**
     * This method drains the task queue into a list of Runnable and returns it.
     *
     * @return Unhandled tasks
     */
    @SuppressWarnings("unchecked")
    public List<Runnable> drain() {
        debug("Draining work queue. There are " + workQueue.size() + " tasks.");
        List<Runnable> taskList = new ArrayList<>();
        workQueue.drainTo(taskList);

        // In case something went wrong and there are still elements in queue,
        // fallback to draining tasks using an array.
        if (!workQueue.isEmpty()) {
            debug("Work queue is not empty. Drained: " + taskList.size() + ", left: " + workQueue.size());

            // Elements in workQueue must be of type T, though
            // it is impossible to construct new T, hence we get them as Runnable and convert (safely).
            for (Runnable r : workQueue.toArray(new Runnable[0])) {
                T task = (T) r;
                if (workQueue.remove(task)) {
                    taskList.add(task);
                }
            }
        }

        return taskList;
    }

    /**
     * This method waits for current executing task then stops worker thread
     *
     * @param wait - if true, wait till execution of the current task is completed using the waitUntilDone method
     * @throws InterruptedException
     */
    public void stop(boolean wait) throws InterruptedException {
        // Make sure we ignore calls to stop when we have already stopped.
        if (!stop) {
            debug("Stopping thread pool");
            lock.writeLock().lock();
            try {
                // Exit in case two threads were arrived here simultaneously.
                // Only one of them will perform the stop operation.
                if (stop) {
                    debug("Stop rejected. Thread pool is already stopped.");
                    return;
                }

                stop = true;
                if (!wait) {
                    stopNow = true;
                }
            } finally {
                lock.writeLock().unlock();
            }

            if (wait) {
                // If the thread is idle, interrupt it as it means the queue is empty, and there is nothing to wait for.
                // Make sure we do not interrupt the thread in case there is a task in the queue that consumer thread has not consumed yet.
                if (workQueue.isEmpty()) {
                    Thread idleThread = this.idleThread;
                    if ((idleThread != null) && (!idleThread.isInterrupted())) {
                        debug("Interrupting consumer thread as it is idle.");
                        idleThread.interrupt();
                    }
                }

                waitUntilDone();
            }
            // Interrupt consumer thread only when we are required to stop now (without waiting)
            else if (!consumerThread.isInterrupted() && consumerThread.isAlive()) {
                debug("Interrupting consumer thread.");
                consumerThread.interrupt();
            }
        } else {
            debug("Stop rejected. Thread pool is already stopped.");
        }
    }

    /**
     * This method should be invoked if wait flag for the stop method is true
     *
     * @throws InterruptedException Upon thread interruption
     */
    public void waitUntilDone() throws InterruptedException {
        if (consumerThread.isAlive()) {
            do {
                debug("Waiting for consumer thread to stop");
                // Wait for submitted task to finish executing
                consumerThread.join();
                debug("End waiting. Consumer thread was stopped");
            } while (!workQueue.isEmpty());
        }
    }

    private void debug(String message) {
        log.debug(message);
    }

    private void error(String message, Throwable thrown) {
        log.error(message, thrown);
    }


    ///////////////// Delegate ExecutorService functionality to assignment's methods /////////////////
    @Override
    public void shutdown() {
        try {
            stop(true);
        } catch (InterruptedException ignore) {
        }
    }

    @Override
    public List<Runnable> shutdownNow() {
        try {
            stop(false);
        } catch (InterruptedException ignore) {
        }

        return drain();
    }

    /**
     * @return Safely get the {@code stop} flag
     */
    @Override
    public boolean isShutdown() {
        lock.readLock().lock();
        try {
            return stop;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * @return Safely get the {@code stopNow} flag
     */
    @Override
    public boolean isTerminated() {
        lock.readLock().lock();
        try {
            return stopNow;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * timeout is ignored. This method delegates the call to {@link #waitUntilDone()}.
     */
    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        waitUntilDone();
        return true;
    }

    /**
     * See {@link ExecutorService#submit(Callable)}<br/>
     * This implementation delegates the call to {@link #apply(Callable)}. We swallow the {@link InterruptedException}
     * that might be thrown by {@code submit}.
     *
     * @param task The task to submit
     * @param <V> The type of the task's result
     * @return A {@link Future} representing pending completion of the task
     */
    @Override
    public <V> Future<V> submit(Callable<V> task) {
        throwIfNull(task);
        return apply(task);
    }

    @Override
    public <V> Future<V> submit(Runnable task, V result) {
        throwIfNull(task);

        // Convert to callable and re-use the apply method.
        return apply(() -> {
            task.run();
            return result;
        });
    }

    @Override
    public Future<?> submit(Runnable task) {
        throwIfNull(task);
        FutureTask<?> future = new FutureTask<Void>(task, null);
        apply(future);
        return future;
    }

    @Override
    public <V> List<Future<V>> invokeAll(Collection<? extends Callable<V>> tasks) throws InterruptedException {
        throwIfNull(tasks);

        List<Future<V>> futures = new ArrayList<>(tasks.size());
        if (!tasks.isEmpty()) {
            for (Callable<V> task : tasks) {
                // Re-use the apply() method
                futures.add(apply(task));
            }
        }

        // According to ExecutorService interface, this method must return only after executing all
        // tasks and having their result ready. (Meaning the tasks were complete)
        for (Future<V> future : futures) {
            if (!future.isDone()) {
                try {
                    future.get();
                } catch (CancellationException | ExecutionException ignore) {
                }
            }
        }

        return futures;
    }

    /**
     * timeout is ignored. This method delegates the call to {@link #invokeAll(Collection)}.
     */
    @Override
    public <V> List<Future<V>> invokeAll(Collection<? extends Callable<V>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        return invokeAll(tasks);
    }

    @Override
    public <V> V invokeAny(Collection<? extends Callable<V>> tasks) throws InterruptedException, ExecutionException {
        throwIfNull(tasks);
        if (tasks.isEmpty()) {
            throw new IllegalArgumentException("No task to invoke. Input was empty");
        }

        return submit(tasks.iterator().next()).get();
    }

    @Override
    public <V> V invokeAny(Collection<? extends Callable<V>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException {
        throwIfNull(tasks);
        if (tasks.isEmpty()) {
            throw new IllegalArgumentException("No task to invoke. Input was empty");
        }

        return submit(tasks.iterator().next()).get();
    }

    /**
     * Execute a command on the calling thread. For executing a command at the future, use {@link #apply(Runnable)}.
     *
     * @param command The command to execute
     */
    @Override
    public void execute(Runnable command) {
        throwIfNull(command);
        command.run();
    }
}

