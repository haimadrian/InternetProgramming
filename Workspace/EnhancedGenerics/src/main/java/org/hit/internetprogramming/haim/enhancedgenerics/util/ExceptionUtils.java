package org.hit.internetprogramming.haim.enhancedgenerics.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Haim Adrian
 * @see #getStackTrace(Throwable)
 */
public class ExceptionUtils {
    /**
     * Use this method to get a stack trace of some throwable as string.<br/>
     * This way we can print stack trace to console in multi threaded environment, without having the
     * stack trace messing up with other prints
     *
     * @param thrown A throwable to get stack trace of
     * @return The stack trace of a specified throwable, as string
     */
    public static String getStackTrace(Throwable thrown) {
        String result;

        try (ByteArrayOutputStream bytes = new ByteArrayOutputStream();
             PrintWriter writer = new PrintWriter(bytes)) {
            thrown.printStackTrace(writer);
            result = bytes.toString();
        } catch (IOException e) {
            result = thrown.toString();
        }

        return result;
    }
}
