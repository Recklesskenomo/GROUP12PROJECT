package com.courier.client.util;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

import java.util.Optional;

/**
 * Utility class for displaying standardized JavaFX alert dialogs.
 *
 * <p>Provides static convenience methods for the most common alert types:
 * error, information, and confirmation. All methods create modal dialogs
 * that block until the user dismisses them.</p>
 *
 * <p>This class keeps alert creation consistent and DRY across all controllers.
 * Instead of manually building {@link Alert} objects in every controller method,
 * call one of the static helpers:</p>
 *
 * <pre>{@code
 * AlertHelper.showError("Connection Failed", "Could not reach the server at " + host);
 * AlertHelper.showInfo("Success", "Parcel booked successfully!");
 * boolean confirmed = AlertHelper.showConfirmation("Delete", "Are you sure?");
 * }</pre>
 *
 * @author Group 12
 * @version 1.0
 */
public final class AlertHelper {

    /**
     * Private constructor to prevent instantiation.
     * This is a utility class — use the static methods.
     */
    private AlertHelper() {
    }

    /**
     * Displays an error alert dialog.
     *
     * <p>Used for RMI failures, validation errors, and other exceptional conditions
     * that prevent an operation from completing.</p>
     *
     * @param title   the dialog title (appears in the title bar)
     * @param message the detailed error message shown in the dialog body
     */
    public static void showError(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Displays an informational alert dialog.
     *
     * <p>Used for success messages, status updates, and general information
     * that the user should acknowledge.</p>
     *
     * @param title   the dialog title (appears in the title bar)
     * @param message the informational message shown in the dialog body
     */
    public static void showInfo(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Displays a confirmation dialog with OK and Cancel buttons.
     *
     * <p>Used before destructive actions such as deleting or deactivating records.
     * Returns {@code true} only if the user clicks OK.</p>
     *
     * @param title   the dialog title (appears in the title bar)
     * @param message the confirmation question shown in the dialog body
     * @return {@code true} if the user confirmed (clicked OK), {@code false} otherwise
     */
    public static boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}
