package org.hit.internetprogramming.eoh.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hit.internetprogramming.eoh.common.log.LoggingStream;
import org.hit.internetprogramming.eoh.server.action.ActionThreadService;
import org.hit.internetprogramming.eoh.server.common.TCPServer;
import org.hit.internetprogramming.eoh.server.impl.MatrixClientHandler;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Main class of the server.<br/>
 * The server is using {@link TCPServer} for listening to clients and server their requests.<br/>
 * As the {@link TCPServer} is generic, we use {@link MatrixClientHandler} in order to handle operational sockets
 * as input stream and output stream, letting the TCPServer class manage most of the work for us.<br/>
 * The server also uses {@link TrayIcon}, in case we are running in a Desktop environment, so we can
 * find the server icon at the tray icon list, right click it and stop the server by selecting "Exit" option.
 * @author Haim Adrian
 * @since 23-Apr-21
 */
public class ServerMain {
    // Used for redirecting System.out and System.in to Log4j2.
    private static final String STDOUT_LOGGER_NAME = "stdout";
    private static final String STDERR_LOGGER_NAME = "stderr";
    public static final String MATRIX_SERVER = "Graph Server";
    private static final int PORT = 8005;

    private Logger log;
    private TrayIcon trayIcon;
    private TCPServer server;

    /**
     * A flag that we use in order to mark that the server shutdown method has been invoked.<br/>
     * The flag is atomic as we call {@link #shutdown()} from a JVM shutdown handler, but also from
     * the tray icon's popup menu, when we instruct the server to shutdown ordinary. As a result, we would like
     * to avoid a situation where the shutdown method runs twice.
     */
    private final AtomicBoolean wasShutDown = new AtomicBoolean(false);

    public static void main(String[] args) {
        configureLog4j2();
        redirectStreamsToLog4j();

        new ServerMain().run();
    }

    /**
     * Configure Log4j2 to use async loggers by default for all loggers, in order to avoid of making
     * log calls affecting performance. (Instead of waiting for IO, the code continues and the IO occurs in background)
     */
    private static void configureLog4j2() {
        // Use asynchronous loggers by default for better performance
        System.setProperty("log4j2.contextSelector", "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");

        // When the async queue is full, discard all DEBUG and TRACE messages that can not be ingested to the queue. INFO and more descriptive will block the caller
        // thread until there is a space for the log event to be kept.
        System.setProperty("log4j2.AsyncQueueFullPolicy", "Discard");
        System.setProperty("log4j2.DiscardThreshold", "DEBUG");
    }

    /**
     * Redirect System.out and System.in to Log4j2.
     */
    private static void redirectStreamsToLog4j() {
        System.setOut(new PrintStream(new LoggingStream(STDOUT_LOGGER_NAME), true));
        System.setErr(new PrintStream(new LoggingStream(STDERR_LOGGER_NAME), true));

        System.out.println(getJavaVersionString());
    }

    private static String getJavaVersionString() {
        return "java version \"" + System.getProperty("java.version") + "\"" + System.lineSeparator() + System.getProperty("java.runtime.name") +
            " (build " + System.getProperty("java.runtime.version") + ")" + System.lineSeparator() + System.getProperty("java.vm.name") +
            " (build " + System.getProperty("java.vm.version") + ", " + System.getProperty("java.vm.info") + ")";
    }

    /**
     * Here we actually start the TCPServer.
     */
    private void run() {
        log = LogManager.getLogger(ServerMain.class);

        server = new TCPServer(PORT, 0, 10, new MatrixClientHandler());
        server.start();
        showTrayIcon();

        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown, "ServerShutdownThread"));
    }

    /**
     * Ordinary shutdown TCPServer and Log4j.
     */
    private void shutdown() {
        if (!wasShutDown.getAndSet(true)) {
            log.info("Shutting down InternetProgramming server");
            server.stop();

            // Stop all actions if there are, as we have been instructed to shutdown.
            ActionThreadService.getInstance().shutdownNow();

            if (trayIcon != null) {
                SystemTray.getSystemTray().remove(trayIcon);
            }

            LogManager.shutdown();
        }
    }

    /**
     * Display tray icon.
     */
    private void showTrayIcon() {
        try {
            tweakPLAF();

            PopupMenu popup = new PopupMenu();
            MenuItem exit = new MenuItem("Exit");
            exit.addActionListener(e -> shutdown());
            popup.add(exit);

            InputStream icon = getClass().getClassLoader().getResourceAsStream("icon.png");
            if (icon != null) {
                trayIcon = new TrayIcon(new ImageIcon(ImageIO.read(icon)).getImage(), MATRIX_SERVER);
                trayIcon.setImageAutoSize(true);
                trayIcon.setPopupMenu(popup);
                SystemTray.getSystemTray().add(trayIcon);

                trayIcon.displayMessage(MATRIX_SERVER, "Internet Programming server is running", java.awt.TrayIcon.MessageType.INFO);
                trayIcon.setToolTip(MATRIX_SERVER);
            }
        } catch (Exception e) {
            log.error("Failed to add system tray icon: " + e.getMessage(), e);
        }
    }

    /**
     * Some Look & Feel for Swing. (For tray icon's popup menu)
     */
    private void tweakPLAF() {
        // Set up Look & Feel to default for current OS

        /*
         * Other L&F options: UIManager.getSystemLookAndFeelClassName() - default for current OS
         * UIManager.getCrossPlatformLookAndFeelClassName() - metal L&F
         * "javax.swing.plaf.metal.MetalLookAndFeel" - metal L&F
         * "com.sun.java.swing.plaf.windows.WindowsLookAndFeel" - windows L&F
         * "com.sun.java.swing.plaf.gtk.GTKLookAndFeel" - GTK+ L&F "com.sun.java.swing.plaf.mac.MacLookAndFeel" -
         * Mac L&F "com.sun.java.swing.plaf.motif.MotifLookAndFeel" - Motif L&F
         */
        //String className = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
        String className = "javax.swing.plaf.nimbus.NimbusLookAndFeel";
        try {
            UIManager.setLookAndFeel(className);
        } catch (Exception e) {
            log.error("Failed setting NimbusLookAndFeel. Defaulting to system L&F", e);

            className = UIManager.getSystemLookAndFeelClassName();
            try {
                UIManager.setLookAndFeel(className);
            } catch (Exception classNotFoundException) {
                log.error("Failed setting SystemLookAndFeel.. FML", classNotFoundException);
            }
        }
    }
}

