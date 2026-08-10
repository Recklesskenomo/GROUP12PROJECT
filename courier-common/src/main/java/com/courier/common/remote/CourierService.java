package com.courier.common.remote;

import com.courier.common.dto.ParcelDTO;
import com.courier.common.dto.SenderDTO;
import com.courier.common.dto.ServiceTypeDTO;
import com.courier.common.dto.ShipmentDTO;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Remote interface for the Courier and Logistics Service.
 *
 * <p>This is the central contract of the distributed application. All client-server
 * communication passes through this interface via Java RMI. Clients (both Admin/Dispatcher
 * and Sender/User) invoke these methods remotely; the server implementation handles
 * all database access through JDBC.</p>
 *
 * <p><strong>Architecture constraint:</strong> Clients MUST NOT access the database
 * directly. This interface is the sole communication channel between clients and
 * the data layer.</p>
 *
 * <p>The interface is organized into four operation groups:</p>
 * <ul>
 *     <li><strong>Service Type operations</strong> — CRUD for delivery service tiers</li>
 *     <li><strong>Sender operations</strong> — registration and management of customers</li>
 *     <li><strong>Parcel operations</strong> — booking and management of parcels</li>
 *     <li><strong>Shipment operations</strong> — tracking and status updates for shipments</li>
 * </ul>
 *
 * @author Group 12
 * @version 1.0
 * @see ServiceTypeDTO
 * @see SenderDTO
 * @see ParcelDTO
 * @see ShipmentDTO
 */
public interface CourierService extends Remote {

    // =========================================================================
    // SERVICE TYPE OPERATIONS
    // =========================================================================

    /**
     * Adds a new service type to the system.
     *
     * <p><strong>Actor:</strong> Admin (Dispatcher)</p>
     *
     * @param serviceType the service type data to add; the {@code id} field is
     *                    ignored as the database auto-generates it
     * @return the created {@link ServiceTypeDTO} with the generated ID populated
     * @throws RemoteException if a communication error occurs during the remote call
     */
    ServiceTypeDTO addServiceType(ServiceTypeDTO serviceType) throws RemoteException;

    /**
     * Updates an existing service type.
     *
     * <p><strong>Actor:</strong> Admin (Dispatcher)</p>
     *
     * @param serviceType the service type data with updated fields; the {@code id}
     *                    field identifies which record to update
     * @return {@code true} if the update was successful, {@code false} if the
     *         service type was not found
     * @throws RemoteException if a communication error occurs during the remote call
     */
    boolean updateServiceType(ServiceTypeDTO serviceType) throws RemoteException;

    /**
     * Deletes a service type by its ID.
     *
     * <p><strong>Actor:</strong> Admin (Dispatcher)</p>
     * <p><strong>Note:</strong> A service type that is referenced by existing parcels
     * may be soft-deleted (deactivated) rather than physically removed, depending
     * on the server implementation.</p>
     *
     * @param serviceTypeId the ID of the service type to delete
     * @return {@code true} if the deletion was successful, {@code false} if the
     *         service type was not found
     * @throws RemoteException if a communication error occurs during the remote call
     */
    boolean deleteServiceType(int serviceTypeId) throws RemoteException;

    /**
     * Retrieves a single service type by its ID.
     *
     * <p><strong>Actor:</strong> Admin (Dispatcher), Sender (User)</p>
     *
     * @param serviceTypeId the ID of the service type to retrieve
     * @return the matching {@link ServiceTypeDTO}, or {@code null} if not found
     * @throws RemoteException if a communication error occurs during the remote call
     */
    ServiceTypeDTO getServiceType(int serviceTypeId) throws RemoteException;

    /**
     * Retrieves all service types in the system.
     *
     * <p><strong>Actor:</strong> Admin (Dispatcher), Sender (User)</p>
     * <p>Senders use this to browse available delivery rates before booking a parcel.</p>
     *
     * @return a list of all {@link ServiceTypeDTO} objects; may be empty but never {@code null}
     * @throws RemoteException if a communication error occurs during the remote call
     */
    List<ServiceTypeDTO> getAllServiceTypes() throws RemoteException;

    // =========================================================================
    // SENDER OPERATIONS
    // =========================================================================

    /**
     * Registers a new sender (customer) in the system.
     *
     * <p><strong>Actor:</strong> Sender (User), Admin (Dispatcher)</p>
     *
     * @param sender the sender data to register; the {@code id} and
     *               {@code registeredDate} fields are ignored as they are
     *               auto-generated
     * @return the created {@link SenderDTO} with the generated ID and registration
     *         date populated
     * @throws RemoteException if a communication error occurs during the remote call
     */
    SenderDTO registerSender(SenderDTO sender) throws RemoteException;

    /**
     * Updates an existing sender's information.
     *
     * <p><strong>Actor:</strong> Sender (User), Admin (Dispatcher)</p>
     *
     * @param sender the sender data with updated fields; the {@code id} field
     *               identifies which record to update
     * @return {@code true} if the update was successful, {@code false} if the
     *         sender was not found
     * @throws RemoteException if a communication error occurs during the remote call
     */
    boolean updateSender(SenderDTO sender) throws RemoteException;

    /**
     * Retrieves a single sender by their ID.
     *
     * <p><strong>Actor:</strong> Admin (Dispatcher)</p>
     *
     * @param senderId the ID of the sender to retrieve
     * @return the matching {@link SenderDTO}, or {@code null} if not found
     * @throws RemoteException if a communication error occurs during the remote call
     */
    SenderDTO getSender(int senderId) throws RemoteException;

    /**
     * Retrieves a sender by their email address.
     *
     * <p><strong>Actor:</strong> Sender (User) — used for login/identification</p>
     *
     * @param email the email address to search for
     * @return the matching {@link SenderDTO}, or {@code null} if not found
     * @throws RemoteException if a communication error occurs during the remote call
     */
    SenderDTO getSenderByEmail(String email) throws RemoteException;

    /**
     * Retrieves all registered senders.
     *
     * <p><strong>Actor:</strong> Admin (Dispatcher)</p>
     *
     * @return a list of all {@link SenderDTO} objects; may be empty but never {@code null}
     * @throws RemoteException if a communication error occurs during the remote call
     */
    List<SenderDTO> getAllSenders() throws RemoteException;

    /**
     * Deletes a sender by their ID.
     *
     * <p><strong>Actor:</strong> Admin (Dispatcher)</p>
     * <p><strong>Note:</strong> This operation will fail if the sender has
     * existing parcels in the system. Remove the sender's parcels first.</p>
     *
     * @param senderId the ID of the sender to delete
     * @return {@code true} if the deletion was successful, {@code false} if the
     *         sender was not found
     * @throws RemoteException if a communication error occurs or if the sender
     *                         has existing parcels (referential integrity violation)
     */
    boolean deleteSender(int senderId) throws RemoteException;

    // =========================================================================
    // PARCEL OPERATIONS
    // =========================================================================

    /**
     * Creates a new parcel booking.
     *
     * <p><strong>Actor:</strong> Sender (User)</p>
     * <p>The server implementation should calculate the {@code totalCost} based on
     * the parcel's weight and the selected service type's price per kilogram.</p>
     *
     * @param parcel the parcel data to create; the {@code id}, {@code totalCost},
     *               and {@code createdDate} fields are auto-generated by the server
     * @return the created {@link ParcelDTO} with the generated ID, calculated cost,
     *         and creation date populated
     * @throws RemoteException if a communication error occurs during the remote call
     */
    ParcelDTO createParcel(ParcelDTO parcel) throws RemoteException;

    /**
     * Updates an existing parcel's information.
     *
     * <p><strong>Actor:</strong> Admin (Dispatcher)</p>
     *
     * @param parcel the parcel data with updated fields; the {@code id} field
     *               identifies which record to update
     * @return {@code true} if the update was successful, {@code false} if the
     *         parcel was not found
     * @throws RemoteException if a communication error occurs during the remote call
     */
    boolean updateParcel(ParcelDTO parcel) throws RemoteException;

    /**
     * Retrieves a single parcel by its ID.
     *
     * <p><strong>Actor:</strong> Admin (Dispatcher), Sender (User)</p>
     *
     * @param parcelId the ID of the parcel to retrieve
     * @return the matching {@link ParcelDTO}, or {@code null} if not found
     * @throws RemoteException if a communication error occurs during the remote call
     */
    ParcelDTO getParcel(int parcelId) throws RemoteException;

    /**
     * Retrieves all parcels booked by a specific sender.
     *
     * <p><strong>Actor:</strong> Sender (User) — used to view booking history</p>
     *
     * @param senderId the ID of the sender whose parcels to retrieve
     * @return a list of {@link ParcelDTO} objects for the given sender;
     *         may be empty but never {@code null}
     * @throws RemoteException if a communication error occurs during the remote call
     */
    List<ParcelDTO> getParcelsBySender(int senderId) throws RemoteException;

    /**
     * Retrieves all parcels in the system.
     *
     * <p><strong>Actor:</strong> Admin (Dispatcher)</p>
     *
     * @return a list of all {@link ParcelDTO} objects; may be empty but never {@code null}
     * @throws RemoteException if a communication error occurs during the remote call
     */
    List<ParcelDTO> getAllParcels() throws RemoteException;

    /**
     * Deletes a parcel by its ID.
     *
     * <p><strong>Actor:</strong> Admin (Dispatcher)</p>
     * <p><strong>Note:</strong> This operation will fail if the parcel has
     * existing shipments. Remove the parcel's shipments first.</p>
     *
     * @param parcelId the ID of the parcel to delete
     * @return {@code true} if the deletion was successful, {@code false} if the
     *         parcel was not found
     * @throws RemoteException if a communication error occurs or if the parcel
     *                         has existing shipments (referential integrity violation)
     */
    boolean deleteParcel(int parcelId) throws RemoteException;

    // =========================================================================
    // SHIPMENT OPERATIONS
    // =========================================================================

    /**
     * Creates a new shipment record for a parcel.
     *
     * <p><strong>Actor:</strong> Admin (Dispatcher)</p>
     * <p>The server implementation should auto-generate a unique tracking number
     * (e.g., "CRR-20260803-00042") and set the initial status to
     * {@link ShipmentDTO.ShipmentStatus#PENDING}.</p>
     *
     * @param shipment the shipment data to create; the {@code id},
     *                 {@code trackingNumber}, {@code status}, and
     *                 {@code createdDate} fields are auto-generated
     * @return the created {@link ShipmentDTO} with all auto-generated fields populated
     * @throws RemoteException if a communication error occurs during the remote call
     */
    ShipmentDTO createShipment(ShipmentDTO shipment) throws RemoteException;

    /**
     * Updates the status and location of an existing shipment.
     *
     * <p><strong>Actor:</strong> Admin (Dispatcher)</p>
     * <p>This is the primary method for progressing a shipment through its lifecycle
     * (e.g., PENDING → PICKED_UP → IN_TRANSIT → DELIVERED). The {@code lastUpdated}
     * timestamp is automatically refreshed by the server.</p>
     *
     * @param shipmentId      the ID of the shipment to update
     * @param status          the new shipment status
     * @param currentLocation the updated location description
     * @return {@code true} if the update was successful, {@code false} if the
     *         shipment was not found
     * @throws RemoteException if a communication error occurs during the remote call
     */
    boolean updateShipmentStatus(int shipmentId, ShipmentDTO.ShipmentStatus status,
                                 String currentLocation) throws RemoteException;

    /**
     * Retrieves a single shipment by its ID.
     *
     * <p><strong>Actor:</strong> Admin (Dispatcher)</p>
     *
     * @param shipmentId the ID of the shipment to retrieve
     * @return the matching {@link ShipmentDTO}, or {@code null} if not found
     * @throws RemoteException if a communication error occurs during the remote call
     */
    ShipmentDTO getShipment(int shipmentId) throws RemoteException;

    /**
     * Retrieves a shipment by its unique tracking number.
     *
     * <p><strong>Actor:</strong> Sender (User) — the primary method for tracking
     * a shipment by entering a tracking number</p>
     *
     * @param trackingNumber the tracking number to search for
     *                       (e.g., "CRR-20260803-00042")
     * @return the matching {@link ShipmentDTO}, or {@code null} if not found
     * @throws RemoteException if a communication error occurs during the remote call
     */
    ShipmentDTO getShipmentByTrackingNumber(String trackingNumber) throws RemoteException;

    /**
     * Retrieves all shipments associated with a specific parcel.
     *
     * <p><strong>Actor:</strong> Admin (Dispatcher), Sender (User)</p>
     *
     * @param parcelId the ID of the parcel whose shipments to retrieve
     * @return a list of {@link ShipmentDTO} objects for the given parcel;
     *         may be empty but never {@code null}
     * @throws RemoteException if a communication error occurs during the remote call
     */
    List<ShipmentDTO> getShipmentsByParcel(int parcelId) throws RemoteException;

    /**
     * Retrieves the complete shipment history for a specific sender.
     *
     * <p><strong>Actor:</strong> Sender (User) — used to view all past and current
     * shipments across all of the sender's parcels</p>
     *
     * @param senderId the ID of the sender whose shipment history to retrieve
     * @return a list of {@link ShipmentDTO} objects across all parcels belonging
     *         to the given sender; may be empty but never {@code null}
     * @throws RemoteException if a communication error occurs during the remote call
     */
    List<ShipmentDTO> getShipmentHistory(int senderId) throws RemoteException;

    /**
     * Deletes a shipment by its ID.
     *
     * <p><strong>Actor:</strong> Admin (Dispatcher)</p>
     *
     * @param shipmentId the ID of the shipment to delete
     * @return {@code true} if the deletion was successful, {@code false} if the
     *         shipment was not found
     * @throws RemoteException if a communication error occurs during the remote call
     */
    boolean deleteShipment(int shipmentId) throws RemoteException;
}
