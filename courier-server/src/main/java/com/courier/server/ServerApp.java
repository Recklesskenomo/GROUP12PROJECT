package com.courier.server;

import com.courier.server.dao.DatabaseManager;
import com.courier.server.impl.CourierServiceImpl;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Main entry point for the Courier RMI Server application.
 *
 * <p>This application:</p>
 * <ol>
 *     <li>Sets the {@code java.rmi.server.hostname} system property for hotspot/LAN networking</li>
 *     <li>Tests the MySQL database connection</li>
 *     <li>Creates an RMI registry on port 1099</li>
 *     <li>Binds the {@link CourierServiceImpl} to the registry under the name "CourierService"</li>
 * </ol>
 *
 * <h3>Usage</h3>
 * <pre>
 * # Pass your hotspot IP as the first argument
 * java -cp ... com.courier.server.ServerApp 192.168.43.1
 *
 * # Or with Maven:
 * mvn exec:java -Dexec.mainClass="com.courier.server.ServerApp" -Dexec.args="192.168.43.1"
 * </pre>
 *
 * <p>If no IP argument is provided, the server defaults to {@code localhost},
 * which only works for local testing on the same machine.</p>
 *
 * @author Group 12
 * @version 1.0
 */
public class ServerApp {

    /** The name under which the service is registered in the RMI registry. */
    private static final String SERVICE_NAME = "CourierService";

    /** The port for the RMI registry. */
    private static final int RMI_PORT = 1099;

    /**
     * Starts the RMI server.
     *
     * @param args optional: first argument is the server's IP address for RMI
     *             hostname binding (e.g., the hotspot IP). Defaults to "localhost".
     */
    public static void main(String[] args) {
        try {
            // ── Step 1: Configure RMI hostname ──────────────────────────
            String host = (args.length > 0) ? args[0] : "localhost";
            System.setProperty("java.rmi.server.hostname", host);

            System.out.println("============================================");
            System.out.println("  Courier & Logistics RMI Server");
            System.out.println("============================================");
            System.out.println("[Server] RMI hostname set to: " + host);
            System.out.println("[Server] RMI port: " + RMI_PORT);

            // ── Step 2: Test database connection ────────────────────────
            System.out.println("[Server] Testing database connection...");
            DatabaseManager.testConnection();

            // ── Step 3: Create RMI registry ─────────────────────────────
            Registry registry = LocateRegistry.createRegistry(RMI_PORT);
            System.out.println("[Server] RMI registry created on port " + RMI_PORT);

            // ── Step 4: Bind the service implementation ─────────────────
            CourierServiceImpl service = new CourierServiceImpl();
            registry.rebind(SERVICE_NAME, service);

            System.out.println("[Server] '" + SERVICE_NAME + "' bound successfully.");
            System.out.println("============================================");
            System.out.println("[Server] Ready for client connections at:");
            System.out.println("         rmi://" + host + ":" + RMI_PORT + "/" + SERVICE_NAME);
            System.out.println("============================================");
            System.out.println("[Server] Press Ctrl+C to stop the server.");

            // Keep the main thread alive so the RMI server remains active
            Thread.currentThread().join();

        } catch (Exception e) {
            System.err.println("[Server] FATAL: Failed to start server.");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
