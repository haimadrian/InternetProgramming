package org.hit.internetprogramming.eoh.server.common.exception;

/**
 * This exception is thrown when a negative weight cycle is detected while traversing a graph
 * using {@link org.hit.internetprogramming.eoh.server.graph.algorithm.BellmanFord} algorithm.
 * @author Haim Adrian
 * @since 19-Jul-21
 */
public class NegativeWeightCycleException extends RuntimeException {
    public NegativeWeightCycleException() {
    }

    public NegativeWeightCycleException(String message) {
        super(message);
    }

    public NegativeWeightCycleException(String message, Throwable cause) {
        super(message, cause);
    }

    public NegativeWeightCycleException(Throwable cause) {
        super(cause);
    }

    public NegativeWeightCycleException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
