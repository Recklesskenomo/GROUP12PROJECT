package com.courier.common.dto;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Data Transfer Object representing a registered sender (customer).
 *
 * <p>A sender is a user who can book parcels for delivery through the courier system.
 * Each sender has personal contact information and an account registration date.</p>
 *
 * <p>This class implements {@link Serializable} so it can be transmitted over RMI
 * between client and server. Note that {@link LocalDateTime} is serializable in Java 8+.</p>
 *
 * @author Group 12
 * @version 1.0
 */
public class SenderDTO implements Serializable {

    /** Serial version UID for serialization compatibility. */
    @Serial
    private static final long serialVersionUID = 1L;

    /** Unique identifier for the sender. */
    private int id;

    /** The sender's first name. */
    private String firstName;

    /** The sender's last name. */
    private String lastName;

    /** The sender's email address, used as a unique login identifier. */
    private String email;

    /** The sender's phone number. */
    private String phone;

    /** The sender's physical or mailing address. */
    private String address;

    /** The date and time when the sender registered in the system. */
    private LocalDateTime registeredDate;

    /**
     * Default no-argument constructor.
     * Required for serialization and framework compatibility.
     */
    public SenderDTO() {
    }

    /**
     * Constructs a fully initialized SenderDTO.
     *
     * @param id             the unique identifier
     * @param firstName      the sender's first name
     * @param lastName       the sender's last name
     * @param email          the sender's email address
     * @param phone          the sender's phone number
     * @param address        the sender's physical address
     * @param registeredDate the date and time of registration
     */
    public SenderDTO(int id, String firstName, String lastName, String email,
                     String phone, String address, LocalDateTime registeredDate) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.registeredDate = registeredDate;
    }

    /**
     * Returns the unique identifier of this sender.
     *
     * @return the sender ID
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the unique identifier of this sender.
     *
     * @param id the sender ID to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Returns the sender's first name.
     *
     * @return the first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the sender's first name.
     *
     * @param firstName the first name to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Returns the sender's last name.
     *
     * @return the last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the sender's last name.
     *
     * @param lastName the last name to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Returns the sender's email address.
     *
     * @return the email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the sender's email address.
     *
     * @param email the email address to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns the sender's phone number.
     *
     * @return the phone number
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets the sender's phone number.
     *
     * @param phone the phone number to set
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Returns the sender's physical address.
     *
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the sender's physical address.
     *
     * @param address the address to set
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Returns the date and time when the sender registered.
     *
     * @return the registration date and time
     */
    public LocalDateTime getRegisteredDate() {
        return registeredDate;
    }

    /**
     * Sets the date and time when the sender registered.
     *
     * @param registeredDate the registration date and time to set
     */
    public void setRegisteredDate(LocalDateTime registeredDate) {
        this.registeredDate = registeredDate;
    }

    /**
     * Returns the sender's full name by concatenating first and last name.
     *
     * @return the full name in "firstName lastName" format
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * Returns a string representation of this sender for debugging purposes.
     *
     * @return a string containing the sender's fields
     */
    @Override
    public String toString() {
        return "SenderDTO{" +
                "id=" + id +
                ", name='" + firstName + " " + lastName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", registeredDate=" + registeredDate +
                '}';
    }
}
