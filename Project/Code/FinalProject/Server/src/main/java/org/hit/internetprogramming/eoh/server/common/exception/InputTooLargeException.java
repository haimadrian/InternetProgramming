package org.hit.internetprogramming.eoh.server.common.exception;

/**
 * This exception is thrown when the input to {@link org.hit.internetprogramming.eoh.server.graph.algorithm.FindPaths#findShortestPaths(Object)}
 * is over than 50x50.
 * @author Haim Adrian
 * @since 19-Jul-21
 */
public class InputTooLargeException extends IllegalArgumentException {
    public InputTooLargeException() {
    }

    public InputTooLargeException(String message) {
        super(message);
    }

    public InputTooLargeException(String message, Throwable cause) {
        super(message, cause);
    }

    public InputTooLargeException(Throwable cause) {
        super(cause);
    }
}
