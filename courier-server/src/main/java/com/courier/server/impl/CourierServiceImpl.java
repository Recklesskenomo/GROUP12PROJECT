package com.courier.server.impl;

import com.courier.common.dto.ParcelDTO;
import com.courier.common.dto.SenderDTO;
import com.courier.common.dto.ServiceTypeDTO;
import com.courier.common.dto.ShipmentDTO;
import com.courier.common.dto.ShipmentDTO.ShipmentStatus;
import com.courier.common.remote.CourierService;
import com.courier.server.dao.DatabaseManager;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Implementation of the {@link CourierService} remote interface.
 *
 * <p>This class extends {@link UnicastRemoteObject} to make it available as an
 * RMI remote object and implements all 21 methods defined in the
 * {@code CourierService} interface using JDBC to access the MySQL database.</p>
 *
 * <p>All database access happens exclusively in this class — clients never
 * interact with the database directly. Each method obtains its own JDBC
 * connection via {@link DatabaseManager#getConnection()} and uses
 * try-with-resources for automatic cleanup.</p>
 *
 * @author Group 12
 * @version 1.0
 * @see CourierService
 * @see DatabaseManager
 */
public class CourierServiceImpl extends UnicastRemoteObject implements CourierService {

    /** Counter for generating unique tracking numbers within a single server session. */
    private static final AtomicInteger trackingCounter = new AtomicInteger(1);

    /**
     * Constructs a new CourierServiceImpl and exports it on the default RMI port.
     *
     * @throws RemoteException if the export fails
     */
    public CourierServiceImpl() throws RemoteException {
        super();
    }

    // =========================================================================
    // SERVICE TYPE OPERATIONS
    // =========================================================================

    /**
     * {@inheritDoc}
     *
     * <p>Inserts a new service type into the {@code service_types} table and
     * returns the created DTO with the auto-generated ID.</p>
     */
    @Override
    public ServiceTypeDTO addServiceType(ServiceTypeDTO serviceType) throws RemoteException {
        String sql = "INSERT INTO service_types (name, description, price_per_kg, estimated_days, active) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, serviceType.getName());
            stmt.setString(2, serviceType.getDescription());
            stmt.setDouble(3, serviceType.getPricePerKg());
            stmt.setInt(4, serviceType.getEstimatedDays());
            stmt.setBoolean(5, serviceType.isActive());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    serviceType.setId(keys.getInt(1));
                }
            }
            System.out.println("[Server] Added service type: " + serviceType.getName());
            return serviceType;
        } catch (SQLException e) {
            throw new RemoteException("Failed to add service type", e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * <p>Updates all fields of the service type identified by its ID.</p>
     */
    @Override
    public boolean updateServiceType(ServiceTypeDTO serviceType) throws RemoteException {
        String sql = "UPDATE service_types SET name = ?, description = ?, price_per_kg = ?, " +
                     "estimated_days = ?, active = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, serviceType.getName());
            stmt.setString(2, serviceType.getDescription());
            stmt.setDouble(3, serviceType.getPricePerKg());
            stmt.setInt(4, serviceType.getEstimatedDays());
            stmt.setBoolean(5, serviceType.isActive());
            stmt.setInt(6, serviceType.getId());

            int rows = stmt.executeUpdate();
            System.out.println("[Server] Updated service type ID=" + serviceType.getId() +
                    " (" + rows + " row(s) affected)");
            return rows > 0;
        } catch (SQLException e) {
            throw new RemoteException("Failed to update service type", e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * <p>Soft-deletes the service type by setting {@code active = FALSE}.
     * This preserves referential integrity with existing parcels.</p>
     */
    @Override
    public boolean deleteServiceType(int serviceTypeId) throws RemoteException {
        String sql = "UPDATE service_types SET active = FALSE WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, serviceTypeId);
            int rows = stmt.executeUpdate();
            System.out.println("[Server] Soft-deleted service type ID=" + serviceTypeId +
                    " (" + rows + " row(s) affected)");
            return rows > 0;
        } catch (SQLException e) {
            throw new RemoteException("Failed to delete service type", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ServiceTypeDTO getServiceType(int serviceTypeId) throws RemoteException {
        String sql = "SELECT * FROM service_types WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, serviceTypeId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapServiceType(rs);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RemoteException("Failed to get service type", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ServiceTypeDTO> getAllServiceTypes() throws RemoteException {
        String sql = "SELECT * FROM service_types ORDER BY name";
        List<ServiceTypeDTO> list = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapServiceType(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new RemoteException("Failed to get all service types", e);
        }
    }

    // =========================================================================
    // SENDER OPERATIONS
    // =========================================================================

    /**
     * {@inheritDoc}
     *
     * <p>Inserts a new sender into the {@code senders} table. The
     * {@code registered_date} is set by the database via {@code DEFAULT CURRENT_TIMESTAMP}.</p>
     */
    @Override
    public SenderDTO registerSender(SenderDTO sender) throws RemoteException {
        String sql = "INSERT INTO senders (first_name, last_name, email, phone, address) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, sender.getFirstName());
            stmt.setString(2, sender.getLastName());
            stmt.setString(3, sender.getEmail());
            stmt.setString(4, sender.getPhone());
            stmt.setString(5, sender.getAddress());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    sender.setId(keys.getInt(1));
                }
            }
            sender.setRegisteredDate(LocalDateTime.now());
            System.out.println("[Server] Registered sender: " + sender.getFullName());
            return sender;
        } catch (SQLException e) {
            throw new RemoteException("Failed to register sender", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean updateSender(SenderDTO sender) throws RemoteException {
        String sql = "UPDATE senders SET first_name = ?, last_name = ?, email = ?, " +
                     "phone = ?, address = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, sender.getFirstName());
            stmt.setString(2, sender.getLastName());
            stmt.setString(3, sender.getEmail());
            stmt.setString(4, sender.getPhone());
            stmt.setString(5, sender.getAddress());
            stmt.setInt(6, sender.getId());

            int rows = stmt.executeUpdate();
            System.out.println("[Server] Updated sender ID=" + sender.getId() +
                    " (" + rows + " row(s) affected)");
            return rows > 0;
        } catch (SQLException e) {
            throw new RemoteException("Failed to update sender", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SenderDTO getSender(int senderId) throws RemoteException {
        String sql = "SELECT * FROM senders WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, senderId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapSender(rs);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RemoteException("Failed to get sender", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SenderDTO getSenderByEmail(String email) throws RemoteException {
        String sql = "SELECT * FROM senders WHERE email = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapSender(rs);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RemoteException("Failed to get sender by email", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<SenderDTO> getAllSenders() throws RemoteException {
        String sql = "SELECT * FROM senders ORDER BY last_name, first_name";
        List<SenderDTO> list = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapSender(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new RemoteException("Failed to get all senders", e);
        }
    }

    // =========================================================================
    // PARCEL OPERATIONS
    // =========================================================================

    /**
     * {@inheritDoc}
     *
     * <p>Inserts a new parcel and calculates the total cost server-side by
     * looking up the service type's price per kg and multiplying by weight.
     * This ensures pricing integrity — the client cannot manipulate costs.</p>
     */
    @Override
    public ParcelDTO createParcel(ParcelDTO parcel) throws RemoteException {
        // First, look up the service type to calculate cost
        ServiceTypeDTO serviceType = getServiceType(parcel.getServiceTypeId());
        if (serviceType == null) {
            throw new RemoteException("Service type not found: ID=" + parcel.getServiceTypeId());
        }
        double totalCost = parcel.getWeight() * serviceType.getPricePerKg();
        parcel.setTotalCost(totalCost);

        String sql = "INSERT INTO parcels (sender_id, service_type_id, weight, recipient_name, " +
                     "recipient_address, recipient_phone, description, total_cost) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, parcel.getSenderId());
            stmt.setInt(2, parcel.getServiceTypeId());
            stmt.setDouble(3, parcel.getWeight());
            stmt.setString(4, parcel.getRecipientName());
            stmt.setString(5, parcel.getRecipientAddress());
            stmt.setString(6, parcel.getRecipientPhone());
            stmt.setString(7, parcel.getDescription());
            stmt.setDouble(8, totalCost);
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    parcel.setId(keys.getInt(1));
                }
            }
            parcel.setCreatedDate(LocalDateTime.now());
            System.out.println("[Server] Created parcel ID=" + parcel.getId() +
                    " (cost=" + totalCost + ")");
            return parcel;
        } catch (SQLException e) {
            throw new RemoteException("Failed to create parcel", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean updateParcel(ParcelDTO parcel) throws RemoteException {
        String sql = "UPDATE parcels SET sender_id = ?, service_type_id = ?, weight = ?, " +
                     "recipient_name = ?, recipient_address = ?, recipient_phone = ?, " +
                     "description = ?, total_cost = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, parcel.getSenderId());
            stmt.setInt(2, parcel.getServiceTypeId());
            stmt.setDouble(3, parcel.getWeight());
            stmt.setString(4, parcel.getRecipientName());
            stmt.setString(5, parcel.getRecipientAddress());
            stmt.setString(6, parcel.getRecipientPhone());
            stmt.setString(7, parcel.getDescription());
            stmt.setDouble(8, parcel.getTotalCost());
            stmt.setInt(9, parcel.getId());

            int rows = stmt.executeUpdate();
            System.out.println("[Server] Updated parcel ID=" + parcel.getId() +
                    " (" + rows + " row(s) affected)");
            return rows > 0;
        } catch (SQLException e) {
            throw new RemoteException("Failed to update parcel", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ParcelDTO getParcel(int parcelId) throws RemoteException {
        String sql = "SELECT * FROM parcels WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, parcelId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapParcel(rs);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RemoteException("Failed to get parcel", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ParcelDTO> getParcelsBySender(int senderId) throws RemoteException {
        String sql = "SELECT * FROM parcels WHERE sender_id = ? ORDER BY created_date DESC";
        List<ParcelDTO> list = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, senderId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapParcel(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new RemoteException("Failed to get parcels by sender", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ParcelDTO> getAllParcels() throws RemoteException {
        String sql = "SELECT * FROM parcels ORDER BY created_date DESC";
        List<ParcelDTO> list = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapParcel(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new RemoteException("Failed to get all parcels", e);
        }
    }

    // =========================================================================
    // SHIPMENT OPERATIONS
    // =========================================================================

    /**
     * {@inheritDoc}
     *
     * <p>Creates a shipment with an auto-generated tracking number in the format
     * {@code CRR-YYYYMMDD-NNNNN} and initial status of {@code PENDING}.</p>
     */
    @Override
    public ShipmentDTO createShipment(ShipmentDTO shipment) throws RemoteException {
        String trackingNumber = generateTrackingNumber();
        String sql = "INSERT INTO shipments (parcel_id, tracking_number, status, " +
                     "current_location, estimated_delivery) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, shipment.getParcelId());
            stmt.setString(2, trackingNumber);
            stmt.setString(3, ShipmentStatus.PENDING.name());
            stmt.setString(4, shipment.getCurrentLocation());
            stmt.setTimestamp(5, shipment.getEstimatedDelivery() != null
                    ? Timestamp.valueOf(shipment.getEstimatedDelivery()) : null);
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    shipment.setId(keys.getInt(1));
                }
            }
            shipment.setTrackingNumber(trackingNumber);
            shipment.setStatus(ShipmentStatus.PENDING);
            shipment.setCreatedDate(LocalDateTime.now());
            shipment.setLastUpdated(LocalDateTime.now());

            System.out.println("[Server] Created shipment: " + trackingNumber +
                    " for parcel ID=" + shipment.getParcelId());
            return shipment;
        } catch (SQLException e) {
            throw new RemoteException("Failed to create shipment", e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * <p>Updates the status and current location. The {@code last_updated}
     * timestamp is automatically set by the database via
     * {@code ON UPDATE CURRENT_TIMESTAMP}.</p>
     */
    @Override
    public boolean updateShipmentStatus(int shipmentId, ShipmentStatus status,
                                        String currentLocation) throws RemoteException {
        String sql = "UPDATE shipments SET status = ?, current_location = ?, " +
                     "last_updated = CURRENT_TIMESTAMP WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status.name());
            stmt.setString(2, currentLocation);
            stmt.setInt(3, shipmentId);

            int rows = stmt.executeUpdate();
            System.out.println("[Server] Updated shipment ID=" + shipmentId +
                    " status=" + status + " location=" + currentLocation +
                    " (" + rows + " row(s) affected)");
            return rows > 0;
        } catch (SQLException e) {
            throw new RemoteException("Failed to update shipment status", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ShipmentDTO getShipment(int shipmentId) throws RemoteException {
        String sql = "SELECT * FROM shipments WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, shipmentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapShipment(rs);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RemoteException("Failed to get shipment", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ShipmentDTO getShipmentByTrackingNumber(String trackingNumber) throws RemoteException {
        String sql = "SELECT * FROM shipments WHERE tracking_number = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, trackingNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapShipment(rs);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RemoteException("Failed to get shipment by tracking number", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ShipmentDTO> getShipmentsByParcel(int parcelId) throws RemoteException {
        String sql = "SELECT * FROM shipments WHERE parcel_id = ? ORDER BY created_date DESC";
        List<ShipmentDTO> list = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, parcelId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapShipment(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new RemoteException("Failed to get shipments by parcel", e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * <p>Retrieves all shipments across all parcels belonging to the given sender.
     * Uses a JOIN between the {@code shipments} and {@code parcels} tables.</p>
     */
    @Override
    public List<ShipmentDTO> getShipmentHistory(int senderId) throws RemoteException {
        String sql = "SELECT s.* FROM shipments s " +
                     "INNER JOIN parcels p ON s.parcel_id = p.id " +
                     "WHERE p.sender_id = ? " +
                     "ORDER BY s.last_updated DESC";
        List<ShipmentDTO> list = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, senderId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapShipment(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new RemoteException("Failed to get shipment history", e);
        }
    }

    // =========================================================================
    // PRIVATE HELPER METHODS — ResultSet → DTO Mapping
    // =========================================================================

    /**
     * Maps a database row from the {@code service_types} table to a {@link ServiceTypeDTO}.
     *
     * @param rs the result set positioned at the current row
     * @return a populated ServiceTypeDTO
     * @throws SQLException if a column cannot be read
     */
    private ServiceTypeDTO mapServiceType(ResultSet rs) throws SQLException {
        return new ServiceTypeDTO(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDouble("price_per_kg"),
                rs.getInt("estimated_days"),
                rs.getBoolean("active")
        );
    }

    /**
     * Maps a database row from the {@code senders} table to a {@link SenderDTO}.
     *
     * @param rs the result set positioned at the current row
     * @return a populated SenderDTO
     * @throws SQLException if a column cannot be read
     */
    private SenderDTO mapSender(ResultSet rs) throws SQLException {
        return new SenderDTO(
                rs.getInt("id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("email"),
                rs.getString("phone"),
                rs.getString("address"),
                toLocalDateTime(rs.getTimestamp("registered_date"))
        );
    }

    /**
     * Maps a database row from the {@code parcels} table to a {@link ParcelDTO}.
     *
     * @param rs the result set positioned at the current row
     * @return a populated ParcelDTO
     * @throws SQLException if a column cannot be read
     */
    private ParcelDTO mapParcel(ResultSet rs) throws SQLException {
        return new ParcelDTO(
                rs.getInt("id"),
                rs.getInt("sender_id"),
                rs.getInt("service_type_id"),
                rs.getDouble("weight"),
                rs.getString("recipient_name"),
                rs.getString("recipient_address"),
                rs.getString("recipient_phone"),
                rs.getString("description"),
                rs.getDouble("total_cost"),
                toLocalDateTime(rs.getTimestamp("created_date"))
        );
    }

    /**
     * Maps a database row from the {@code shipments} table to a {@link ShipmentDTO}.
     *
     * @param rs the result set positioned at the current row
     * @return a populated ShipmentDTO
     * @throws SQLException if a column cannot be read
     */
    private ShipmentDTO mapShipment(ResultSet rs) throws SQLException {
        return new ShipmentDTO(
                rs.getInt("id"),
                rs.getInt("parcel_id"),
                rs.getString("tracking_number"),
                ShipmentStatus.valueOf(rs.getString("status")),
                rs.getString("current_location"),
                toLocalDateTime(rs.getTimestamp("estimated_delivery")),
                toLocalDateTime(rs.getTimestamp("last_updated")),
                toLocalDateTime(rs.getTimestamp("created_date"))
        );
    }

    /**
     * Safely converts a {@link Timestamp} to a {@link LocalDateTime}.
     *
     * @param timestamp the SQL timestamp to convert (may be null)
     * @return the corresponding LocalDateTime, or {@code null} if the timestamp is null
     */
    private LocalDateTime toLocalDateTime(Timestamp timestamp) {
        return timestamp != null ? timestamp.toLocalDateTime() : null;
    }

    /**
     * Generates a unique tracking number in the format {@code CRR-YYYYMMDD-NNNNN}.
     *
     * <p>Example: {@code CRR-20260803-00042}</p>
     *
     * @return a unique tracking number string
     */
    private String generateTrackingNumber() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int seq = trackingCounter.getAndIncrement();
        return String.format("CRR-%s-%05d", date, seq);
    }
}
