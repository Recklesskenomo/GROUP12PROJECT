package com.courier.client.controller;

import com.courier.client.ClientApp;
import com.courier.client.connection.ServerConnection;
import com.courier.client.util.AlertHelper;
import com.courier.common.dto.SenderDTO;
import com.courier.common.remote.CourierService;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Optional;

/**
 * Controller for the login / role selection screen ({@code login.fxml}).
 *
 * <p>This controller handles three user actions:</p>
 * <ol>
 *     <li><strong>Connect as Admin</strong> — validates the RMI connection and
 *         navigates to the Admin Dashboard</li>
 *     <li><strong>Sender Login</strong> — looks up the sender by email via RMI
 *         and navigates to the Sender Dashboard with the sender context</li>
 *     <li><strong>Register</strong> — collects new sender details via dialogs,
 *         registers them through RMI, and logs in automatically</li>
 * </ol>
 *
 * <p>The server IP field is pre-populated from command-line arguments via
 * {@link ClientApp#getServerHost()}.</p>
 *
 * @author Group 12
 * @version 1.0
 * @see ServerConnection
 * @see AdminDashboardController
 * @see SenderDashboardController
 */
public class LoginController {

    /** Text field for the server IP address. */
    @FXML
    private TextField serverIpField;

    /** Text field for the sender's email address. */
    @FXML
    private TextField emailField;

    /** Status label for displaying connection feedback. */
    @FXML
    private Label statusLabel;

    /**
     * Called by JavaFX after FXML loading completes.
     * Pre-fills the server IP field from the command-line argument.
     */
    @FXML
    public void initialize() {
        serverIpField.setText(ClientApp.getServerHost());
    }

    /**
     * Handles the "Connect as Admin" button click.
     *
     * <p>Attempts to connect to the RMI server using the IP in the text field.
     * On success, navigates to the Admin Dashboard. On failure, displays an
     * error alert with the connection details.</p>
     */
    @FXML
    private void onConnectAdmin() {
        String host = serverIpField.getText().trim();
        if (host.isEmpty()) {
            AlertHelper.showError("Validation Error", "Please enter the server IP address.");
            return;
        }

        statusLabel.setText("Connecting to " + host + "...");
        try {
            ServerConnection.getInstance().connect(host);
            statusLabel.setText("Connected! Loading Admin Dashboard...");
            navigateTo("/fxml/admin_dashboard.fxml", "Courier System — Admin Dashboard");
        } catch (Exception e) {
            statusLabel.setText("Connection failed.");
            AlertHelper.showError("Connection Failed",
                    "Could not connect to the server at " + host + ".\n\n" +
                    "Error: " + e.getMessage() + "\n\n" +
                    "Make sure the RMI server is running and the IP is correct.");
        }
    }

    /**
     * Handles the "Login" button click for senders.
     *
     * <p>Connects to the RMI server (if not already connected), looks up the
     * sender by email via {@link CourierService#getSenderByEmail(String)}, and
     * navigates to the Sender Dashboard with the sender context.</p>
     */
    @FXML
    private void onSenderLogin() {
        String host = serverIpField.getText().trim();
        String email = emailField.getText().trim();

        if (host.isEmpty()) {
            AlertHelper.showError("Validation Error", "Please enter the server IP address.");
            return;
        }
        if (email.isEmpty()) {
            AlertHelper.showError("Validation Error", "Please enter your email address.");
            return;
        }

        statusLabel.setText("Connecting...");
        try {
            // Connect if not already connected (or reconnect to a different host)
            ServerConnection.getInstance().connect(host);
            CourierService service = ServerConnection.getInstance().getService();

            // Look up sender by email
            SenderDTO sender = service.getSenderByEmail(email);
            if (sender == null) {
                statusLabel.setText("Email not found.");
                AlertHelper.showError("Login Failed",
                        "No account found for email: " + email + "\n\n" +
                        "If you are a new user, click \"Register\" to create an account.");
                return;
            }

            statusLabel.setText("Welcome, " + sender.getFullName() + "!");
            navigateToSenderDashboard(sender);
        } catch (Exception e) {
            statusLabel.setText("Login failed.");
            AlertHelper.showError("Login Failed",
                    "Could not log in.\n\nError: " + e.getMessage());
        }
    }

    /**
     * Handles the "Register" button click.
     *
     * <p>Collects new sender details through a series of input dialogs, then
     * registers the sender via {@link CourierService#registerSender(SenderDTO)}.
     * On success, the new sender is automatically logged in.</p>
     */
    @FXML
    private void onRegister() {
        String host = serverIpField.getText().trim();
        if (host.isEmpty()) {
            AlertHelper.showError("Validation Error", "Please enter the server IP address.");
            return;
        }

        try {
            // Connect first
            ServerConnection.getInstance().connect(host);
            CourierService service = ServerConnection.getInstance().getService();

            // Collect sender details via input dialogs
            String firstName = promptInput("Registration", "Enter your first name:");
            if (firstName == null) return;

            String lastName = promptInput("Registration", "Enter your last name:");
            if (lastName == null) return;

            String email = promptInput("Registration", "Enter your email address:");
            if (email == null) return;

            String phone = promptInput("Registration", "Enter your phone number:");
            if (phone == null) return;

            String address = promptInput("Registration", "Enter your address:");
            if (address == null) return;

            // Create and register the sender
            SenderDTO newSender = new SenderDTO();
            newSender.setFirstName(firstName);
            newSender.setLastName(lastName);
            newSender.setEmail(email);
            newSender.setPhone(phone);
            newSender.setAddress(address);

            SenderDTO registered = service.registerSender(newSender);
            AlertHelper.showInfo("Registration Successful",
                    "Welcome, " + registered.getFullName() + "!\n" +
                    "Your account has been created. Logging you in...");

            navigateToSenderDashboard(registered);
        } catch (RemoteException e) {
            AlertHelper.showError("Registration Failed",
                    "Could not register your account.\n\nError: " + e.getMessage());
        } catch (Exception e) {
            AlertHelper.showError("Connection Failed",
                    "Could not connect to the server.\n\nError: " + e.getMessage());
        }
    }

    // =========================================================================
    // PRIVATE HELPER METHODS
    // =========================================================================

    /**
     * Displays a text input dialog and returns the user's input.
     *
     * @param title  the dialog title
     * @param prompt the prompt text describing what to enter
     * @return the trimmed input string, or {@code null} if the user cancelled
     */
    private String promptInput(String title, String prompt) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(title);
        dialog.setHeaderText(null);
        dialog.setContentText(prompt);
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && !result.get().trim().isEmpty()) {
            return result.get().trim();
        }
        return null;
    }

    /**
     * Navigates to a new FXML scene, replacing the current scene content.
     *
     * <p>The existing stage is reused and the stylesheet is preserved.</p>
     *
     * @param fxmlPath the classpath resource path to the FXML file
     * @param title    the new title for the stage
     * @throws IOException if the FXML file cannot be loaded
     */
    private void navigateTo(String fxmlPath, String title) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();

        Stage stage = (Stage) serverIpField.getScene().getWindow();
        Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        stage.setTitle(title);
        stage.setScene(scene);
    }

    /**
     * Navigates to the Sender Dashboard, passing the logged-in sender context.
     *
     * <p>The {@link SenderDashboardController} is obtained from the FXMLLoader
     * and initialized with the sender's data before the scene is displayed.</p>
     *
     * @param sender the authenticated sender DTO
     * @throws IOException if the FXML file cannot be loaded
     */
    private void navigateToSenderDashboard(SenderDTO sender) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/sender_dashboard.fxml"));
        Parent root = loader.load();

        // Pass the sender context to the controller
        SenderDashboardController controller = loader.getController();
        controller.initSender(sender);

        Stage stage = (Stage) serverIpField.getScene().getWindow();
        Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        stage.setTitle("Courier System — " + sender.getFullName());
        stage.setScene(scene);
    }
}
