package org.hit.internetprogramming.haim.enhancedgenerics;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hit.internetprogramming.haim.enhancedgenerics.concurrent.EnhancedGenerics;
import org.hit.internetprogramming.haim.enhancedgenerics.concurrent.task.PriorityRunnable;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * @author Nathan Dillbary, Haim Adrian
 */
public class EnhancedGenericsMain {
    /**
     * Use a flag to control whether we add a lot of runnables, to test priority reordering, or not.
     */
    private static final boolean IS_TESTING_PRIORITY_REORDERING = false;

    private static final Logger log;
    static {
        System.setProperty("log4j.configurationFile", "log4j2.xml");
        log = LogManager.getLogger(EnhancedGenericsMain.class);
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        EnhancedGenerics<PriorityRunnable> service =
            new EnhancedGenerics<>(new PriorityBlockingQueue<>(),
                aRunnableTask -> new PriorityRunnable(aRunnableTask, 1));

        /*
         submit Runnable tasks to to the queue (as PriorityRunnable objects) using
         the apply methods above
         */
        service.apply(() -> log.info("There are more than 2 design patterns in this class"));
        service.apply(() -> log.info("a runnable"));

        service.apply(() -> log.info("Fun"), runnable -> new PriorityRunnable(runnable, 5));

        Callable<String> stringCallable = () -> {
            try {
                Thread.sleep(5000); // wait until interrupt
            } catch (InterruptedException e) {
                log.info("Job interrupted");
            }
            return "callable string";
        };
        Future<String> futureString = service.apply(stringCallable);
        Future<String> anotherFutureString = service.apply(stringCallable);

        if (IS_TESTING_PRIORITY_REORDERING) {
            // Submit some tasks with priority of 0, so they will be executed before priority 1.
            for (int i = 0; i < 5; i++) {
                service.apply(() -> {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        log.info("Job interrupted2");
                    }
                    log.info("callable string2");
                }, runnable -> new PriorityRunnable(runnable, 0));
            }
        }

        // Wait until we get the result of first submitted task (5 seconds)
        log.info(futureString.get());

        if (IS_TESTING_PRIORITY_REORDERING) {
            // Submit some tasks with priority of -1, so they will be executed before priority 0 and 1.
            for (int i = 0; i < 5; i++) {
                service.apply(() -> {
                    try {
                        Thread.sleep(1000); // wait until interrupt
                    } catch (InterruptedException e) {
                        log.info("Job interrupted3");
                    }
                    log.info("callable string3");
                }, runnable -> new PriorityRunnable(runnable, -1));
            }
        }

        log.info(anotherFutureString.get());

        service.stop(true);
        log.info("done");
    }
}
