package org.hit.internetprogramming.haim.enhancedgenerics.concurrent.task;

import java.util.concurrent.PriorityBlockingQueue;

/**
 * This class defines a {@link Runnable} task (eventually will run in separate thread)
 * and gives it a priority.
 * <p>
 * <u>Strategy pattern</u>:
 * <ol>
 *  <li>Implement an interface</li>
 *  <li>Declare a data member of the same type of the interface</li>
 *  <li>Get the target in the constructor/Setter method</li>
 *  <li>Delegate functionality to the target in the method that we override</li>
 * </ol>
 * </p>
 * Your system support only Runnable tasks<br/>
 * It uses a method called start()<br/>
 * There is a {@link PriorityBlockingQueue} of Runnable objects
 * @author Nathan Dillbary
 */
public class PriorityRunnable implements Runnable, Comparable<PriorityRunnable> {
    private static final int DEFAULT_PRIORITY = 0;
    private final Runnable target;
    private final int priority;

    /**
     * Constructs a new {@link PriorityRunnable}
     *
     * @param target A runnable to delegate the functionality to
     * @param priority The priority to assign to this runnable
     */
    public PriorityRunnable(Runnable target, int priority) {
        this.target = target;
        this.priority = priority;
    }

    /**
     * Constructs a new {@link PriorityRunnable}, using default priority (0)
     *
     * @param target A runnable to delegate the functionality to
     */
    public PriorityRunnable(Runnable target) {
        this(target, DEFAULT_PRIORITY);
    }

    /**
     * This method compares the priority of 2 objects of the class using their priority data member
     *
     * @param another is an instance of another PriorityRunnable to compare to
     * @return the value 0 if {@code this.priority == another.priority}; a value less than 0 if {@code this.priority < another.priority};
     * and a value greater than 0 if {@code this.priority > another.priority}
     */
    @Override
    public int compareTo(PriorityRunnable another) {
        return Integer.compare(this.priority, another.priority);
    }

    @Override
    public void run() {
        if (target != null) {
            target.run();
        }
    }

    public int getPriority() {
        return this.priority;
    }
}

