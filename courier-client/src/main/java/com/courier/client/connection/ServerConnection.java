package com.courier.client.connection;

import com.courier.common.remote.CourierService;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Singleton manager for the RMI connection to the Courier server.
 *
 * <p>This class handles looking up the {@link CourierService} remote object from
 * the RMI registry. All controllers obtain the service proxy through
 * {@link #getService()} rather than performing their own RMI lookups.</p>
 *
 * <p><strong>Usage:</strong></p>
 * <pre>{@code
 * // At application startup (once):
 * ServerConnection.getInstance().connect("192.168.43.1");
 *
 * // In any controller:
 * CourierService service = ServerConnection.getInstance().getService();
 * List<ServiceTypeDTO> types = service.getAllServiceTypes();
 * }</pre>
 *
 * @author Group 12
 * @version 1.0
 * @see CourierService
 */
public class ServerConnection {

    /** The name under which the service is registered in the RMI registry. */
    private static final String SERVICE_NAME = "CourierService";

    /** The RMI registry port. */
    private static final int RMI_PORT = 1099;

    /** The singleton instance. */
    private static ServerConnection instance;

    /** The remote service proxy obtained from the RMI registry. */
    private CourierService service;

    /** The hostname/IP of the server we are connected to. */
    private String host;

    /**
     * Private constructor to enforce the singleton pattern.
     */
    private ServerConnection() {
    }

    /**
     * Returns the singleton instance of ServerConnection.
     *
     * @return the single {@link ServerConnection} instance
     */
    public static synchronized ServerConnection getInstance() {
        if (instance == null) {
            instance = new ServerConnection();
        }
        return instance;
    }

    /**
     * Connects to the RMI server at the specified host.
     *
     * <p>Locates the RMI registry on the given host and port, then looks up
     * the {@code CourierService} remote object. If the connection succeeds,
     * subsequent calls to {@link #getService()} will return the service proxy.</p>
     *
     * @param host the server's IP address or hostname (e.g., "192.168.43.1")
     * @throws RemoteException   if a communication error occurs
     * @throws NotBoundException if the service name is not found in the registry
     */
    public void connect(String host) throws RemoteException, NotBoundException {
        this.host = host;
        Registry registry = LocateRegistry.getRegistry(host, RMI_PORT);
        this.service = (CourierService) registry.lookup(SERVICE_NAME);
        System.out.println("[Client] Connected to server at: rmi://" + host + ":" +
                RMI_PORT + "/" + SERVICE_NAME);
    }

    /**
     * Returns the {@link CourierService} remote proxy.
     *
     * <p>This method must be called after a successful {@link #connect(String)}.
     * If called before connecting, it returns {@code null}.</p>
     *
     * @return the remote service proxy, or {@code null} if not yet connected
     */
    public CourierService getService() {
        return service;
    }

    /**
     * Returns the hostname/IP that this connection is using.
     *
     * @return the server host, or {@code null} if not yet connected
     */
    public String getHost() {
        return host;
    }

    /**
     * Checks whether the client is currently connected to the server.
     *
     * @return {@code true} if {@link #connect(String)} has been called successfully
     */
    public boolean isConnected() {
        return service != null;
    }
}
