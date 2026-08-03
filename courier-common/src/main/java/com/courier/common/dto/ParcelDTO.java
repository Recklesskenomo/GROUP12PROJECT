package com.courier.common.dto;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Data Transfer Object representing a parcel booked for delivery.
 *
 * <p>A parcel captures all the details of a delivery request: who is sending it,
 * what service type is selected, the recipient's information, the weight,
 * and the calculated total cost. Each parcel may have one or more associated
 * {@link ShipmentDTO} records that track its journey.</p>
 *
 * <p>This class implements {@link Serializable} so it can be transmitted over RMI
 * between client and server.</p>
 *
 * @author Group 12
 * @version 1.0
 * @see SenderDTO
 * @see ServiceTypeDTO
 * @see ShipmentDTO
 */
public class ParcelDTO implements Serializable {

    /** Serial version UID for serialization compatibility. */
    @Serial
    private static final long serialVersionUID = 1L;

    /** Unique identifier for the parcel. */
    private int id;

    /** The ID of the sender who booked this parcel. */
    private int senderId;

    /** The ID of the selected service type for this parcel. */
    private int serviceTypeId;

    /** The weight of the parcel in kilograms. */
    private double weight;

    /** The full name of the intended recipient. */
    private String recipientName;

    /** The delivery address for the recipient. */
    private String recipientAddress;

    /** The phone number of the recipient. */
    private String recipientPhone;

    /** A brief description of the parcel contents. */
    private String description;

    /**
     * The total cost of delivering this parcel.
     * Typically calculated as {@code weight × serviceType.pricePerKg}.
     */
    private double totalCost;

    /** The date and time when this parcel was created/booked. */
    private LocalDateTime createdDate;

    /**
     * Default no-argument constructor.
     * Required for serialization and framework compatibility.
     */
    public ParcelDTO() {
    }

    /**
     * Constructs a fully initialized ParcelDTO.
     *
     * @param id               the unique identifier
     * @param senderId         the ID of the sender who booked this parcel
     * @param serviceTypeId    the ID of the selected service type
     * @param weight           the weight of the parcel in kilograms
     * @param recipientName    the full name of the recipient
     * @param recipientAddress the delivery address
     * @param recipientPhone   the recipient's phone number
     * @param description      a brief description of the parcel contents
     * @param totalCost        the calculated total delivery cost
     * @param createdDate      the date and time when the parcel was booked
     */
    public ParcelDTO(int id, int senderId, int serviceTypeId, double weight,
                     String recipientName, String recipientAddress, String recipientPhone,
                     String description, double totalCost, LocalDateTime createdDate) {
        this.id = id;
        this.senderId = senderId;
        this.serviceTypeId = serviceTypeId;
        this.weight = weight;
        this.recipientName = recipientName;
        this.recipientAddress = recipientAddress;
        this.recipientPhone = recipientPhone;
        this.description = description;
        this.totalCost = totalCost;
        this.createdDate = createdDate;
    }

    /**
     * Returns the unique identifier of this parcel.
     *
     * @return the parcel ID
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the unique identifier of this parcel.
     *
     * @param id the parcel ID to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Returns the ID of the sender who booked this parcel.
     *
     * @return the sender ID
     */
    public int getSenderId() {
        return senderId;
    }

    /**
     * Sets the ID of the sender who booked this parcel.
     *
     * @param senderId the sender ID to set
     */
    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    /**
     * Returns the ID of the service type selected for this parcel.
     *
     * @return the service type ID
     */
    public int getServiceTypeId() {
        return serviceTypeId;
    }

    /**
     * Sets the ID of the service type selected for this parcel.
     *
     * @param serviceTypeId the service type ID to set
     */
    public void setServiceTypeId(int serviceTypeId) {
        this.serviceTypeId = serviceTypeId;
    }

    /**
     * Returns the weight of this parcel in kilograms.
     *
     * @return the weight in kg
     */
    public double getWeight() {
        return weight;
    }

    /**
     * Sets the weight of this parcel in kilograms.
     *
     * @param weight the weight in kg to set
     */
    public void setWeight(double weight) {
        this.weight = weight;
    }

    /**
     * Returns the full name of the intended recipient.
     *
     * @return the recipient's name
     */
    public String getRecipientName() {
        return recipientName;
    }

    /**
     * Sets the full name of the intended recipient.
     *
     * @param recipientName the recipient's name to set
     */
    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    /**
     * Returns the delivery address for the recipient.
     *
     * @return the recipient's address
     */
    public String getRecipientAddress() {
        return recipientAddress;
    }

    /**
     * Sets the delivery address for the recipient.
     *
     * @param recipientAddress the recipient's address to set
     */
    public void setRecipientAddress(String recipientAddress) {
        this.recipientAddress = recipientAddress;
    }

    /**
     * Returns the phone number of the recipient.
     *
     * @return the recipient's phone number
     */
    public String getRecipientPhone() {
        return recipientPhone;
    }

    /**
     * Sets the phone number of the recipient.
     *
     * @param recipientPhone the recipient's phone number to set
     */
    public void setRecipientPhone(String recipientPhone) {
        this.recipientPhone = recipientPhone;
    }

    /**
     * Returns the brief description of the parcel contents.
     *
     * @return the parcel description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the brief description of the parcel contents.
     *
     * @param description the parcel description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the total delivery cost for this parcel.
     *
     * @return the total cost
     */
    public double getTotalCost() {
        return totalCost;
    }

    /**
     * Sets the total delivery cost for this parcel.
     *
     * @param totalCost the total cost to set
     */
    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    /**
     * Returns the date and time when this parcel was created/booked.
     *
     * @return the creation date and time
     */
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    /**
     * Sets the date and time when this parcel was created/booked.
     *
     * @param createdDate the creation date and time to set
     */
    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    /**
     * Returns a string representation of this parcel for debugging purposes.
     *
     * @return a string containing the parcel's key fields
     */
    @Override
    public String toString() {
        return "ParcelDTO{" +
                "id=" + id +
                ", senderId=" + senderId +
                ", serviceTypeId=" + serviceTypeId +
                ", weight=" + weight +
                ", recipientName='" + recipientName + '\'' +
                ", totalCost=" + totalCost +
                ", createdDate=" + createdDate +
                '}';
    }
}
