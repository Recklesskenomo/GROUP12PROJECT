/**
 * Module descriptor for the Courier RMI Server application.
 *
 * <p>This module implements the {@link com.courier.common.remote.CourierService}
 * remote interface using JDBC to access the MySQL database. It depends on the
 * shared {@code courier.common} module for DTOs and the remote interface.</p>
 */
module courier.server {

    // ── Shared DTOs and RMI interface ───────────────────────────────────
    requires courier.common;

    // ── Java RMI for exporting the remote object ────────────────────────
    requires java.rmi;

    // ── JDBC for database access ────────────────────────────────────────
    requires java.sql;

    // ── MySQL JDBC driver (automatic module) ────────────────────────────
    requires mysql.connector.j;

    // ── Export packages ─────────────────────────────────────────────────
    exports com.courier.server;
    exports com.courier.server.dao;
    exports com.courier.server.impl;
}
