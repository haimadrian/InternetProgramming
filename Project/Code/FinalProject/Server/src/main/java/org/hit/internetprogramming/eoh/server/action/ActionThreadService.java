package org.hit.internetprogramming.eoh.server.action;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Thread service to be used by graph algorithms that we run in the server.<br/>
 * This class is a singleton created for serving parallel algorithms. This is not the thread pool
 * of our TCP server. Instead, this is a thread pool that serves parallel actions.<br/>
 * We need to share a thread pool among all actions, to make sure we do not maintain a thread pool
 * for every action (user request) with no management over the amount of threads that the server created.
 * Thus we can set a limit here which is shared for all actions.
 * <p>
 * Note that this class implements {@link ExecutorService} to ease the use of it, though it hides
 * two inner thread pools. One for Dijkstra, which is a Fork-Join thread pool, and the other one is
 * a regular thread pool, for other algorithms.<br/>
 * When a task is submitted, we check if it is a {@link ForkJoinTask}, and if so, it will be forwarded to
 * the Fork-Join pool. Otherwise, it will be forwarded to the regular thread pool.
 * </p>
 * @author Haim Adrian
 * @since 22-Jul-21
 */
@Log4j2
public class ActionThreadService implements ExecutorService {
    private static final int THREADS_PER_PROCESSOR = 4;
    private static final int FORK_JOIN_THREADS_PER_PROCESSOR = 1;

    /**
     * A thread pool with as many threads as the available processors on the current machine.<br/>
     * This thread pool is being used by the various algorithms of Graph server, except Dijkstra, which uses
     * the Fork-Join thread pool.<br/>
     * We share the same thread pool to limit the amount of threads created by Graph server, and control
     * them in one location, instead of managing a pool per algorithm.
     */
    private final ExecutorService threadPool;

    /**
     * A Fork-Join thread pool with as many threads as the available processors on the current machine.<br/>
     * This thread pool is being used by {@link org.hit.internetprogramming.eoh.server.graph.algorithm.DijkstraWithNegCycleSupport} algorithm,
     * where we splits an action into sub actions recursively, to find shortest paths in weighted graph, using parallel search.
     */
    private final ForkJoinPool forkJoinThreadPool;

    /**
     * How many threads were allocated by the regular thread pool, to let algorithms to split their tasks
     * equally among all of our threads.
     */
    @Getter
    private final int amountOfWorkers;

    /**
     * A flag we use in order to have a shutdown now state, to let tasks to test this flag and stop
     * when it is set to true.
     */
    private final AtomicBoolean isShutdownNow;

    /**
     * Use an atomic counter so we can count threads (action workers) and give them meaningful name.
     */
    private final AtomicInteger workerThreadIdCounter;

    /**
     * Use an atomic counter so we can count threads (action workers) and give them meaningful name.
     */
    private final AtomicInteger forkJoinWorkerThreadIdCounter;

    private ActionThreadService() {
        amountOfWorkers = Runtime.getRuntime().availableProcessors() * THREADS_PER_PROCESSOR;
        workerThreadIdCounter = new AtomicInteger();
        forkJoinWorkerThreadIdCounter = new AtomicInteger();
        isShutdownNow = new AtomicBoolean(false);

        // Create a new cached thread pool, but use bounded max pool size, so we will not create too many threads.
        threadPool = new ThreadPoolExecutor(amountOfWorkers, amountOfWorkers, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), this::workerThreadFactory);
        forkJoinThreadPool = new ForkJoinPool(FORK_JOIN_THREADS_PER_PROCESSOR, this::forkJoinWorkerThreadFactory, (thread, e) -> log.error("Uncaught error in ForkJoin thread: " + thread.getName() + ". Error: " + e, e), true);
    }

    private Thread workerThreadFactory(Runnable r) {
        Thread t = Executors.defaultThreadFactory().newThread(r);
        t.setName("ActionWorker-" + workerThreadIdCounter.incrementAndGet());
        return t;
    }

    private ForkJoinWorkerThread forkJoinWorkerThreadFactory(ForkJoinPool pool) {
        ForkJoinWorkerThread t = ForkJoinPool.defaultForkJoinWorkerThreadFactory.newThread(pool);
        t.setName("ActionForkJoinWorker-" + forkJoinWorkerThreadIdCounter.incrementAndGet());
        return t;
    }

    /**
     * @return The unique instance of {@link ActionThreadService}.
     */
    public static ActionThreadService getInstance() {
        return ActionThreadServiceHolder.INSTANCE;
    }

    @Override
    public void shutdown() {
        threadPool.shutdown();
        forkJoinThreadPool.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        isShutdownNow.set(true);
        List<Runnable> allTasks = new ArrayList<>(threadPool.shutdownNow());
        allTasks.addAll(forkJoinThreadPool.shutdownNow());
        return allTasks;
    }

    @Override
    public boolean isShutdown() {
        return threadPool.isShutdown();
    }

    /**
     * Useful functionality to stop action tasks when server is shutting down.
     * @return Whether action thread service instructed to stop now, or not.
     */
    public boolean isShutdownNow() {
        return isShutdownNow.get();
    }

    @Override
    public boolean isTerminated() {
        return threadPool.isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return threadPool.awaitTermination(timeout, unit);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        if (task instanceof ForkJoinTask) {
            return forkJoinThreadPool.submit(task);
        }

        return threadPool.submit(task);
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        if (task instanceof ForkJoinTask) {
            return forkJoinThreadPool.submit(task, result);
        }

        return threadPool.submit(task, result);
    }

    @Override
    public Future<?> submit(Runnable task) {
        if (task instanceof ForkJoinTask) {
            return forkJoinThreadPool.submit(task);
        }

        return threadPool.submit(task);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        if (tasks.iterator().next() instanceof ForkJoinTask) {
            return forkJoinThreadPool.invokeAll(tasks);
        }

        return threadPool.invokeAll(tasks);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        if (tasks.iterator().next() instanceof ForkJoinTask) {
            return forkJoinThreadPool.invokeAll(tasks, timeout, unit);
        }

        return threadPool.invokeAll(tasks, timeout, unit);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        if (tasks.iterator().next() instanceof ForkJoinTask) {
            return forkJoinThreadPool.invokeAny(tasks);
        }

        return threadPool.invokeAny(tasks);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        if (tasks.iterator().next() instanceof ForkJoinTask) {
            return forkJoinThreadPool.invokeAny(tasks, timeout, unit);
        }

        return threadPool.invokeAny(tasks, timeout, unit);
    }

    @Override
    public void execute(Runnable command) {
        if (command instanceof ForkJoinTask) {
            forkJoinThreadPool.execute(command);
        } else {
            threadPool.execute(command);
        }
    }

    /**
     * @see ForkJoinPool#invoke(ForkJoinTask)
     */
    public <T> T invoke(ForkJoinTask<T> task) {
        return forkJoinThreadPool.invoke(task);
    }

    private static class ActionThreadServiceHolder {
        static final ActionThreadService INSTANCE = new ActionThreadService();
    }
}
