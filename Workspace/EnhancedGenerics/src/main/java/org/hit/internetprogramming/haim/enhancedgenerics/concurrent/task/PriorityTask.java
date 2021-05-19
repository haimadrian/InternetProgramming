package org.hit.internetprogramming.haim.enhancedgenerics.concurrent.task;

import java.util.concurrent.*;

/**
 * This class represents an Entity that can be either executed or submitted to a ThreadPool<br/>
 * This is a generic type<br/>
 * Uses strategy pattern (as {@link PriorityRunnable} did)<br/>
 * There will be 2 constructors: one for {@link Runnable} tasks and another for {@link Callable} tasks
 *
 * @param <V> The return type of a target that this class delegates functionality to, in case an instance
 * was constructed using Callable target, or the type of the result in case an instance was constructed using Runnable target.
 * @author Nathan Dillbary
 */
public class PriorityTask<V> implements RunnableFuture<V>, Comparable<PriorityTask<V>> {
    /**
     * This data member will wrap either {@link Runnable} or {@link Callable}
     */
    private final RunnableFuture<V> target;
    private final int priority;

    /**
     * Constructs a new {@link PriorityTask}
     *
     * @param target The target to delegate functionality to
     * @param priority Priority of this task
     */
    public PriorityTask(Callable<V> target, int priority) {
        this.target = new FutureTask<>(target);
        this.priority = priority;
    }

    /**
     * Constructs a new {@link PriorityTask}
     *
     * @param target The target to delegate functionality to
     * @param result The result of this task
     * @param priority Priority of this task
     */
    public PriorityTask(Runnable target, V result, int priority) {
        this.target = new FutureTask<>(target, result);
        this.priority = priority;
    }

    @Override
    public int compareTo(PriorityTask<V> another) {
        return Integer.compare(this.priority, another.priority);
    }

    @Override
    public void run() {
        target.run();
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return target.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
        return target.isCancelled();
    }

    @Override
    public boolean isDone() {
        return target.isDone();
    }

    @Override
    public V get() throws InterruptedException, ExecutionException {
        return target.get();
    }

    @Override
    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return target.get(timeout, unit);
    }
}
