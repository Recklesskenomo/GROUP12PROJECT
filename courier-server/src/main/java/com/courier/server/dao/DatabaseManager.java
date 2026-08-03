package com.courier.server.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Database connection manager for the Courier system.
 *
 * <p>Loads MySQL connection parameters from a {@code db.properties} file on the
 * classpath and provides a method to obtain JDBC connections. Each call to
 * {@link #getConnection()} returns a new connection — callers are responsible
 * for closing connections after use (preferably via try-with-resources).</p>
 *
 * <p>The properties file must contain:</p>
 * <ul>
 *     <li>{@code db.url} — the JDBC connection URL</li>
 *     <li>{@code db.username} — the database username</li>
 *     <li>{@code db.password} — the database password</li>
 * </ul>
 *
 * @author Group 12
 * @version 1.0
 */
public class DatabaseManager {

    /** JDBC connection URL loaded from properties. */
    private static final String URL;

    /** Database username loaded from properties. */
    private static final String USERNAME;

    /** Database password loaded from properties. */
    private static final String PASSWORD;

    /*
     * Static initializer — loads db.properties from the classpath once when
     * the class is first loaded. Also loads the MySQL JDBC driver class.
     */
    static {
        Properties props = new Properties();
        try (InputStream input = DatabaseManager.class.getClassLoader()
                .getResourceAsStream("db.properties")) {
            if (input == null) {
                throw new RuntimeException(
                        "db.properties not found on classpath. " +
                        "Copy db.properties.template to db.properties and fill in your credentials.");
            }
            props.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load db.properties", e);
        }

        URL = props.getProperty("db.url");
        USERNAME = props.getProperty("db.username");
        PASSWORD = props.getProperty("db.password");

        // Load the MySQL JDBC driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC driver not found. " +
                    "Ensure mysql-connector-j is on the classpath.", e);
        }
    }

    /**
     * Private constructor to prevent instantiation.
     * This is a utility class — use the static {@link #getConnection()} method.
     */
    private DatabaseManager() {
    }

    /**
     * Obtains a new JDBC connection to the MySQL database.
     *
     * <p>The connection is configured with the URL, username, and password
     * from {@code db.properties}. The caller is responsible for closing the
     * connection after use.</p>
     *
     * <p><strong>Recommended usage:</strong></p>
     * <pre>{@code
     * try (Connection conn = DatabaseManager.getConnection()) {
     *     // use connection
     * }
     * }</pre>
     *
     * @return a new {@link Connection} to the database
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    /**
     * Tests the database connection by attempting to connect and immediately
     * closing the connection. Useful for startup validation.
     *
     * @throws SQLException if the connection cannot be established
     */
    public static void testConnection() throws SQLException {
        try (Connection conn = getConnection()) {
            System.out.println("[DatabaseManager] Connection successful: " +
                    conn.getMetaData().getDatabaseProductName() + " " +
                    conn.getMetaData().getDatabaseProductVersion());
        }
    }
}
