package com.courier.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Main entry point for the Courier JavaFX Client application.
 *
 * <p>This class extends {@link Application} and serves as the launcher for the
 * graphical user interface. It loads the initial login screen from FXML and
 * applies the application-wide stylesheet.</p>
 *
 * <h3>Usage</h3>
 * <pre>
 * # Pass the server's hotspot IP as the first argument:
 * mvn javafx:run -Dexec.args="192.168.43.1"
 *
 * # Or for local testing (defaults to localhost):
 * mvn javafx:run
 * </pre>
 *
 * <p>The server IP is passed to the login screen where the user can also
 * edit it before connecting.</p>
 *
 * @author Group 12
 * @version 1.0
 */
public class ClientApp extends Application {

    /** Minimum width for the application window. */
    private static final double MIN_WIDTH = 900;

    /** Minimum height for the application window. */
    private static final double MIN_HEIGHT = 600;

    /** Default server IP when no command-line argument is provided. */
    private static final String DEFAULT_HOST = "localhost";

    /**
     * The server IP extracted from command-line arguments.
     * Made accessible to the LoginController via a static getter.
     */
    private static String serverHost = DEFAULT_HOST;

    /**
     * Starts the JavaFX application.
     *
     * <p>Loads the login screen, applies the stylesheet, and displays
     * the primary stage with configured dimensions.</p>
     *
     * @param primaryStage the primary stage provided by the JavaFX runtime
     * @throws IOException if the FXML file cannot be loaded
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        // Extract server IP from command-line args
        var params = getParameters().getRaw();
        if (!params.isEmpty()) {
            serverHost = params.get(0);
        }

        // Load the login screen
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        Parent root = loader.load();

        // Create the scene and apply the stylesheet
        Scene scene = new Scene(root, MIN_WIDTH, MIN_HEIGHT);
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

        // Configure and show the stage
        primaryStage.setTitle("Courier & Logistics System");
        primaryStage.setMinWidth(MIN_WIDTH);
        primaryStage.setMinHeight(MIN_HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.show();

        System.out.println("[Client] Application started. Default server: " + serverHost);
    }

    /**
     * Returns the server hostname/IP extracted from command-line arguments.
     *
     * <p>Used by the {@code LoginController} to pre-fill the server IP field.</p>
     *
     * @return the server IP address, or "localhost" if none was provided
     */
    public static String getServerHost() {
        return serverHost;
    }

    /**
     * Application entry point.
     *
     * @param args command-line arguments; first argument is the server IP
     */
    public static void main(String[] args) {
        launch(args);
    }
}
