package org.hit.internetprogramming.haim.enhancedgenerics.concurrent;

import org.hit.internetprogramming.haim.enhancedgenerics.concurrent.task.PriorityRunnable;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Test class for some simulations over {@link EnhancedGenerics} class.<br/>
 * <b>Note</b> that this test class depends on the libs below:
 * <ol>
 *     <li>org.junit.jupiter:junit-jupiter-api:5.7.0</li>
 *     <li>org.junit.jupiter:junit-jupiter-engine:5.7.0</li>
 * </ol>
 * We define time outs in order to make sure tests can run, without getting blocked due to deadlock or whatever else.
 *
 * @author Haim Adrian
 */
public class EnhancedGenericsTest {
    private EnhancedGenerics<PriorityRunnable> service;

    /**
     * We use a flag so we can fail a test when consumer thread is interrupted when it was not supposed to.<br/>
     * We cannot fail a test within the consumer thread (by calling {@link Assertions#fail()}) because this is a different
     * thread, and the {@link AssertionError} will get dismissed.
     */
    private boolean isInterrupted;

    private static int testIndex = 1;

    static {
        System.setProperty("log4j.configurationFile", "log4j2.xml");
    }

    @Timeout(2)
    @BeforeEach
    public void setup() {
        service = new EnhancedGenerics<>(new PriorityBlockingQueue<>(), task -> new PriorityRunnable(task, 1));
        isInterrupted = false;
        println("\t\t\t\tTest #" + testIndex++);
        println("\t\t\t---------------");
    }

    @Timeout(2)
    @AfterEach
    public void tearDown() {
        service.shutdown();
        println(System.lineSeparator() + "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~" + System.lineSeparator());
    }

    @Timeout(1)
    @Test
    public void testService_submitNoTasks_shutdownSuccessfullyWithoutInfiniteWait() throws InterruptedException {
        println("Calling stop and wait, to make sure we do not wait as no task was submitted.");

        // Just call shutdown, without submitting tasks, to make sure we can stop the service
        // without getting blocked forever due to anything that might wait for tasks to arrive.
        service.stop(true);
    }

    @Timeout(1)
    @Test
    public void testService_submitNoTasks_shutdownNowSuccessfullyWithoutInfiniteWait() throws InterruptedException {
        println("Calling stop without wait, to make sure we do not wait as no task was submitted.");

        // Just call shutdown, without submitting tasks, to make sure we can stop the service
        // without getting blocked forever due to anything that might wait for tasks to arrive.
        service.stop(true);
    }

    @Timeout(3)
    @Test
    public void testService_submit1RunnableThatRunsFor2Secs_responseShouldBeArrivedWithin2Secs() throws InterruptedException {
        println("Make sure stop(true) waits for a submitted runnable to complete.");

        AtomicBoolean someCheck = new AtomicBoolean(false);
        service.apply(() -> {
            try {
                Thread.sleep(2000);

                // Some sign for completion of runnable
                someCheck.set(true);
            } catch (InterruptedException ignore) {
                isInterrupted = true;
                println("Thread was interrupted when it was not supposed to.");
            }
        });

        service.stop(true);
        Assertions.assertFalse(isInterrupted, "Thread was interrupted when it was not supposed to.");
        Assertions.assertTrue(someCheck.get(), "Runnable has not ran successfully.");
    }

    @Timeout(3)
    @Test
    public void testService_submit1CallableThatRunsFor2Secs_responseShouldBeArrivedWithin2Secs() throws ExecutionException, InterruptedException {
        println("Make sure stop(true) waits for a submitted callable to complete.");

        Future<Boolean> future = service.apply(() -> {
            try {
                Thread.sleep(2000);
                return Boolean.TRUE;
            } catch (InterruptedException ignore) {
                isInterrupted = true;
                println("Thread was interrupted when it was not supposed to.");
            }

            return Boolean.FALSE;
        });

        service.stop(true);
        Assertions.assertFalse(isInterrupted, "Thread was interrupted when it was not supposed to.");
        Assertions.assertTrue(future.get(), "Callable has not ran successfully.");
    }

    @Timeout(4)
    @Test
    public void testStopNow_submit1RunnableThatRunsFor2Secs_useStopNowInOrderToInterrupt() throws InterruptedException {
        println("Make sure stop(false) does not wait for a submitted callable to complete.");

        AtomicBoolean someCheck = new AtomicBoolean(false);
        service.apply(() -> {
            try {
                // In case the thread is not interrupted, we will sleep for 2 seconds
                Thread.sleep(2000);
                println("End incorrectly waiting, as we supposed to get interrupted.");
            } catch (InterruptedException ignore) {
                println("Wubba Lubba Dub Dub!!! Thread was interrupted, as expected.");
                someCheck.set(true);
            }
        });

        // Let consumer thread to consume the task
        Thread.sleep(50);
        service.stop(false);

        // Wait a little bit, so the interruption will finish before we test someCheck value
        Thread.sleep(250);
        Assertions.assertTrue(someCheck.get(), "Runnable supposed to get interrupted due to stopNow.");
    }

    @Timeout(4)
    @Test
    public void testBoundedService_submitTooManyTasks_getRejectedExecutionException() throws InterruptedException, ExecutionException {
        println("Creating a bounded queue and flood it with tasks, so last tasks will be rejected.");

        // First, stop the service created as part of setup.
        service.stop(false);

        // Limit the queue to 5 tasks only
        service = new EnhancedGenerics<>(new ArrayBlockingQueue<>(5), task -> new PriorityRunnable(task, 1));

        // Submit 7 tasks, so last task will be rejected.
        List<Future<Boolean>> futures = new ArrayList<>();

        println("Submit one long task, so it will get consumed by the thread");
        Future<Boolean> future = service.apply(() -> {
            try {
                // Wait one second, so the queue will exceed its limit
                Thread.sleep(1000);
                return Boolean.TRUE;
            } catch (InterruptedException ignore) {
                isInterrupted = true;
                println("Thread was interrupted when it was not supposed to.");
            }

            return Boolean.FALSE;
        });
        futures.add(future);

        int taskIndex = 1;
        try {
            println("Now that the queue is empty and the thread is running our long task, submit 6 other tasks to exceed queue's capacity.");
            for (; taskIndex < 7; taskIndex++) {
                future = service.apply(() -> Boolean.TRUE);
                futures.add(future);
            }
        } catch (RejectedExecutionException | IllegalArgumentException ignore) {
            println("Pikaaa!!! Task #" + (taskIndex + 1) + " was rejected.");
        }

        println("There are " + futures.size() + " futures.");

        // If we've got here, at least see the results.
        for (int i = 0; i < futures.size(); i++) {
            // In case last task was rejected, without informing caller (by using exception), the call to get() will
            // be blocked forever and the test will fail on time-out.
            println("Result of task #" + (i+1) + " is: " + futures.get(i).get());
        }

        Assertions.assertFalse(isInterrupted, "Thread was interrupted when it was not supposed to.");
        Assertions.assertEquals(6, taskIndex, "Last task supposed to be rejected, as the queue was bounded to 5 tasks only.");
    }

    @Timeout(2)
    @Test
    public void testServiceState_submitTaskToStoppedService_taskMustBeRejected() throws ExecutionException, InterruptedException {
        println("Make sure we cannot submit tasks after stopping the service.");

        // First, stop the service
        println("Stopping service.");
        service.stop(false);

        boolean response = false;
        try {
            println("Submitting task.");
            Future<Boolean> future = service.apply(() -> Boolean.TRUE);

            // If not exception, at least expect null response?
            if (future != null) {
                println("Strange... There was a response.. Try to extract it");

                // If response differs from null, make sure it is getting blocked, or at least the response is not TRUE...
                // Cause the task was not supposed to run
                response = future.get();
            }
        } catch (RejectedExecutionException | IllegalArgumentException | IllegalStateException ignore) {
            println("Charrr!!! Task was rejected, as expected of a Deadpool.");
        }

        Assertions.assertFalse(response, "Task supposed to be rejected, as the thread pool is stopped!");
    }

    @Timeout(3)
    @Test
    public void testDrain_submitTasksAndStopNow_makeSureDrainReturnedCorrectAmountOfTasks() throws InterruptedException {
        println("A test to verify drain() works as expected.");

        println("Submit one long task, so it will get consumed by the thread");
        service.apply(() -> {
            try {
                // Wait one second, so we will receive all other tasks from drain()
                Thread.sleep(1000);
            } catch (InterruptedException ignore) {
            }
        });

        println("Now that the queue is empty and the thread is running our long task, submit some tasks so we will get them from drain().");
        int taskIndex = 0;
        for (; taskIndex < 5; taskIndex++) {
            service.apply(() -> Boolean.TRUE);
        }

        println("Stopping service now.");
        service.stop(false);

        println("Draining service");
        List<Runnable> tasks = service.drain();

        println("Received " + (tasks == null ? 0 : tasks.size()) + " tasks from drain().");

        // taskIndex is the amount of submitted task. (As per the loop above)
        Assertions.assertEquals(taskIndex, tasks.size(), "We supposed to receive all tasks from drain() as they were not consumed and we called stopNow.");
    }

    private void println(String message) {
        System.out.print(message + System.lineSeparator());
    }
}

