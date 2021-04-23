package org.hit.internetprogramming.haim.matrix.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hit.internetprogramming.haim.matrix.common.log.LoggingStream;
import org.hit.internetprogramming.haim.matrix.server.common.TCPServer;
import org.hit.internetprogramming.haim.matrix.server.impl.MatrixClientHandler;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.InputStream;
import java.io.PrintStream;

/**
 * @author Haim Adrian
 * @since 23-Apr-21
 */
public class MatrixServerMain {
    private static final String STDOUT_LOGGER_NAME = "stdout";
    private static final String STDERR_LOGGER_NAME = "stderr";
    public static final String MATRIX_SERVER = "Graph Server";

    private Logger log;
    private TrayIcon trayIcon;
    private TCPServer server;
    private boolean wasShutDown = false;

    public static void main(String[] args) {
        configureLog4j2();
        redirectStreamsToLog4j();

        new MatrixServerMain().run();
    }

    private static void configureLog4j2() {
        // Use asynchronous loggers by default for better performance
        System.setProperty("log4j2.contextSelector", "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");

        // When the async queue is full, discard all DEBUG and TRACE messages that can not be ingested to the queue. INFO and more descriptive will block the caller
        // thread until there is a space for the log event to be kept.
        System.setProperty("log4j2.AsyncQueueFullPolicy", "Discard");
        System.setProperty("log4j2.DiscardThreshold", "DEBUG");
    }

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

    private void run() {
        log = LogManager.getLogger(MatrixServerMain.class);

        server = new TCPServer(1234, 0, 10, new MatrixClientHandler());
        server.start();
        showTrayIcon();

        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown, "ServerShutdownThread"));
    }

    private void shutdown() {
        if (!wasShutDown) {
            wasShutDown = true;
            log.info("Shutting down InternetProgramming server");
            server.stop();

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

