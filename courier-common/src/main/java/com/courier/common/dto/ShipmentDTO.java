package com.courier.common.dto;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Data Transfer Object representing a shipment and its tracking lifecycle.
 *
 * <p>A shipment is the physical journey of a {@link ParcelDTO}. It contains a unique
 * tracking number, a status that progresses through defined stages, the current location,
 * and timestamp information. Each parcel has exactly one active shipment.</p>
 *
 * <p>This class implements {@link Serializable} so it can be transmitted over RMI
 * between client and server.</p>
 *
 * @author Group 12
 * @version 1.0
 * @see ParcelDTO
 * @see ShipmentStatus
 */
public class ShipmentDTO implements Serializable {

    /** Serial version UID for serialization compatibility. */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Enumeration of possible shipment statuses.
     *
     * <p>A shipment progresses through these statuses in order during a normal
     * delivery lifecycle. A shipment can also be cancelled at any stage
     * before delivery.</p>
     *
     * <pre>
     * PENDING → PICKED_UP → IN_TRANSIT → OUT_FOR_DELIVERY → DELIVERED
     *                                                      ↗
     *                        (any stage) → CANCELLED ------
     * </pre>
     */
    public enum ShipmentStatus {
        /** The shipment has been created but not yet picked up. */
        PENDING,

        /** The parcel has been picked up from the sender. */
        PICKED_UP,

        /** The parcel is in transit between facilities. */
        IN_TRANSIT,

        /** The parcel is out for delivery to the recipient. */
        OUT_FOR_DELIVERY,

        /** The parcel has been successfully delivered. */
        DELIVERED,

        /** The shipment has been cancelled. */
        CANCELLED
    }

    /** Unique identifier for the shipment. */
    private int id;

    /** The ID of the parcel associated with this shipment. */
    private int parcelId;

    /**
     * A unique, human-readable tracking number (e.g., "CRR-20260803-00042").
     * Used by senders to track their shipments.
     */
    private String trackingNumber;

    /** The current status of this shipment. */
    private ShipmentStatus status;

    /** A description of the shipment's current physical location. */
    private String currentLocation;

    /** The estimated date and time of delivery. */
    private LocalDateTime estimatedDelivery;

    /** The date and time when the shipment status was last updated. */
    private LocalDateTime lastUpdated;

    /** The date and time when this shipment record was created. */
    private LocalDateTime createdDate;

    /**
     * Default no-argument constructor.
     * Required for serialization and framework compatibility.
     */
    public ShipmentDTO() {
    }

    /**
     * Constructs a fully initialized ShipmentDTO.
     *
     * @param id                the unique identifier
     * @param parcelId          the ID of the associated parcel
     * @param trackingNumber    the human-readable tracking number
     * @param status            the current shipment status
     * @param currentLocation   the current physical location description
     * @param estimatedDelivery the estimated delivery date and time
     * @param lastUpdated       the date and time of the last status update
     * @param createdDate       the date and time when the shipment was created
     */
    public ShipmentDTO(int id, int parcelId, String trackingNumber, ShipmentStatus status,
                       String currentLocation, LocalDateTime estimatedDelivery,
                       LocalDateTime lastUpdated, LocalDateTime createdDate) {
        this.id = id;
        this.parcelId = parcelId;
        this.trackingNumber = trackingNumber;
        this.status = status;
        this.currentLocation = currentLocation;
        this.estimatedDelivery = estimatedDelivery;
        this.lastUpdated = lastUpdated;
        this.createdDate = createdDate;
    }

    /**
     * Returns the unique identifier of this shipment.
     *
     * @return the shipment ID
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the unique identifier of this shipment.
     *
     * @param id the shipment ID to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Returns the ID of the parcel associated with this shipment.
     *
     * @return the parcel ID
     */
    public int getParcelId() {
        return parcelId;
    }

    /**
     * Sets the ID of the parcel associated with this shipment.
     *
     * @param parcelId the parcel ID to set
     */
    public void setParcelId(int parcelId) {
        this.parcelId = parcelId;
    }

    /**
     * Returns the unique, human-readable tracking number.
     *
     * @return the tracking number
     */
    public String getTrackingNumber() {
        return trackingNumber;
    }

    /**
     * Sets the unique, human-readable tracking number.
     *
     * @param trackingNumber the tracking number to set
     */
    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    /**
     * Returns the current status of this shipment.
     *
     * @return the shipment status
     * @see ShipmentStatus
     */
    public ShipmentStatus getStatus() {
        return status;
    }

    /**
     * Sets the current status of this shipment.
     *
     * @param status the shipment status to set
     * @see ShipmentStatus
     */
    public void setStatus(ShipmentStatus status) {
        this.status = status;
    }

    /**
     * Returns the current physical location description of this shipment.
     *
     * @return the current location
     */
    public String getCurrentLocation() {
        return currentLocation;
    }

    /**
     * Sets the current physical location description of this shipment.
     *
     * @param currentLocation the current location to set
     */
    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }

    /**
     * Returns the estimated date and time of delivery.
     *
     * @return the estimated delivery date and time
     */
    public LocalDateTime getEstimatedDelivery() {
        return estimatedDelivery;
    }

    /**
     * Sets the estimated date and time of delivery.
     *
     * @param estimatedDelivery the estimated delivery date and time to set
     */
    public void setEstimatedDelivery(LocalDateTime estimatedDelivery) {
        this.estimatedDelivery = estimatedDelivery;
    }

    /**
     * Returns the date and time when the shipment status was last updated.
     *
     * @return the last update timestamp
     */
    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    /**
     * Sets the date and time when the shipment status was last updated.
     *
     * @param lastUpdated the last update timestamp to set
     */
    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    /**
     * Returns the date and time when this shipment record was created.
     *
     * @return the creation date and time
     */
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    /**
     * Sets the date and time when this shipment record was created.
     *
     * @param createdDate the creation date and time to set
     */
    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    /**
     * Returns a string representation of this shipment for debugging purposes.
     *
     * @return a string containing the shipment's key fields
     */
    @Override
    public String toString() {
        return "ShipmentDTO{" +
                "id=" + id +
                ", parcelId=" + parcelId +
                ", trackingNumber='" + trackingNumber + '\'' +
                ", status=" + status +
                ", currentLocation='" + currentLocation + '\'' +
                ", lastUpdated=" + lastUpdated +
                '}';
    }
}
