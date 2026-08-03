package com.courier.common.dto;

import java.io.Serial;
import java.io.Serializable;

/**
 * Data Transfer Object representing a courier service type.
 *
 * <p>A service type defines a tier of delivery service offered by the courier system,
 * such as Standard, Express, or Overnight delivery. Each service type has an associated
 * price per kilogram and an estimated number of delivery days.</p>
 *
 * <p>This class implements {@link Serializable} so it can be transmitted over RMI
 * between client and server.</p>
 *
 * @author Group 12
 * @version 1.0
 */
public class ServiceTypeDTO implements Serializable {

    /** Serial version UID for serialization compatibility. */
    @Serial
    private static final long serialVersionUID = 1L;

    /** Unique identifier for the service type. */
    private int id;

    /** Display name of the service type (e.g., "Express", "Standard", "Overnight"). */
    private String name;

    /** Detailed description of what this service type offers. */
    private String description;

    /** Price charged per kilogram of parcel weight for this service type. */
    private double pricePerKg;

    /** Estimated number of days for delivery using this service type. */
    private int estimatedDays;

    /** Whether this service type is currently active and available for booking. */
    private boolean active;

    /**
     * Default no-argument constructor.
     * Required for serialization and framework compatibility.
     */
    public ServiceTypeDTO() {
    }

    /**
     * Constructs a fully initialized ServiceTypeDTO.
     *
     * @param id            the unique identifier
     * @param name          the display name of the service type
     * @param description   a detailed description of the service
     * @param pricePerKg    the price per kilogram
     * @param estimatedDays the estimated delivery days
     * @param active        whether this service type is currently active
     */
    public ServiceTypeDTO(int id, String name, String description,
                          double pricePerKg, int estimatedDays, boolean active) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.pricePerKg = pricePerKg;
        this.estimatedDays = estimatedDays;
        this.active = active;
    }

    /**
     * Returns the unique identifier of this service type.
     *
     * @return the service type ID
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the unique identifier of this service type.
     *
     * @param id the service type ID to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Returns the display name of this service type.
     *
     * @return the service type name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the display name of this service type.
     *
     * @param name the service type name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the detailed description of this service type.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the detailed description of this service type.
     *
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the price per kilogram for this service type.
     *
     * @return the price per kg
     */
    public double getPricePerKg() {
        return pricePerKg;
    }

    /**
     * Sets the price per kilogram for this service type.
     *
     * @param pricePerKg the price per kg to set
     */
    public void setPricePerKg(double pricePerKg) {
        this.pricePerKg = pricePerKg;
    }

    /**
     * Returns the estimated number of delivery days for this service type.
     *
     * @return the estimated delivery days
     */
    public int getEstimatedDays() {
        return estimatedDays;
    }

    /**
     * Sets the estimated number of delivery days for this service type.
     *
     * @param estimatedDays the estimated delivery days to set
     */
    public void setEstimatedDays(int estimatedDays) {
        this.estimatedDays = estimatedDays;
    }

    /**
     * Returns whether this service type is currently active and available for booking.
     *
     * @return {@code true} if active, {@code false} otherwise
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets whether this service type is currently active.
     *
     * @param active {@code true} to make active, {@code false} to deactivate
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Returns a string representation of this service type for debugging purposes.
     *
     * @return a string containing the service type's fields
     */
    @Override
    public String toString() {
        return "ServiceTypeDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", pricePerKg=" + pricePerKg +
                ", estimatedDays=" + estimatedDays +
                ", active=" + active +
                '}';
    }
}
