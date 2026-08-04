/**
 * Module descriptor for the Courier JavaFX Client application.
 *
 * <p>This module provides the graphical user interface for both Admin (Dispatcher)
 * and Sender (User) roles. It communicates exclusively through the RMI remote
 * interface defined in {@code courier.common} — no direct database access.</p>
 *
 * <p>The {@code opens} directives allow JavaFX's FXML loader to reflectively
 * access controller classes and the main application class at runtime.</p>
 */
module courier.client {

    // ── JavaFX modules ──────────────────────────────────────────────────
    requires javafx.controls;
    requires javafx.fxml;

    // ── Shared DTOs and RMI interface ───────────────────────────────────
    requires courier.common;

    // ── Java RMI for remote method invocation ───────────────────────────
    requires java.rmi;

    // ── Open packages to JavaFX for FXML reflection ─────────────────────
    opens com.courier.client to javafx.fxml;
    opens com.courier.client.controller to javafx.fxml;

    // ── Export the main package so JavaFX can launch the Application ────
    exports com.courier.client;
}
