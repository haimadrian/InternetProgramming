package org.hit.internetprogramming.haim.matrix.common.log;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.OutputStream;

/**
 * This output stream holds all streamed data in an internal buffer. On flush() it will send buffer data to the relevant logger.
 * @author Haim Adrian
 * @since 23-Apr-21
 */
public class LoggingStream extends OutputStream {
    /**
     * Internal stream buffer
     */
    private final StringBuilder sb;

    /**
     * The logger where we flush internal buffer to
     */
    private final Logger logger;

    /**
     * Constructs a new {@link LoggingStream}
     *
     * @param loggerName Name of the logger to log messages to
     */
    public LoggingStream(String loggerName) {
        sb = new StringBuilder(128);
        logger = LogManager.getLogger(loggerName);
    }

    /**
     * Writes the specified byte to this output stream. The general contract for <code>write</code> is
     * that one byte is written to the output stream. The byte to be written is the eight low-order bits of
     * the argument <code>b</code>. The 24 high-order bits of <code>b</code> are ignored.
     * <p>
     * Subclasses of <code>OutputStream</code> must provide an implementation for this method.
     *
     * @param b the <code>byte</code>.
     */
    @Override
    public void write(int b) {
        sb.append((char) b);
    }

    /**
     * Flushes this output stream and forces any buffered output bytes to be written out. The general
     * contract of <code>flush</code> is that calling it is an indication that, if any bytes previously
     * written have been buffered by the implementation of the output stream, such bytes should immediately
     * be written to their intended destination.
     * <p>
     * The <code>flush</code> method of <code>OutputStream</code> does nothing.
     */
    @Override
    public void flush() {
        if (sb.length() > 0) {
            String message = sb.toString();

            // When calling log.info, there is the print of the message which is flushed
            // and we print it with a new line as part of the logger implementation, and then there is additional
            // newLine() call of the println, which we would like to ignore cause the logger already prints a new line.
            if (!System.lineSeparator().equals(message)) {
                logger.info(message);
            }

            sb.setLength(0);
        }
    }

    /**
     * Closes this output stream and releases any system resources associated with this stream. The general
     * contract of <code>close</code> is that it closes the output stream. A closed stream cannot perform
     * output operations and cannot be reopened.
     * <p>
     * The <code>close</code> method of <code>OutputStream</code> does nothing.
     */
    @Override
    public void close() {
        flush();
    }
}

