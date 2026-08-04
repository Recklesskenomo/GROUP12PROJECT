-- ============================================================================
-- Courier & Logistics System — Database Schema
-- Engine: MySQL / MariaDB (managed via HeidiSQL)
-- Version: 1.0
-- Group: 12
-- ============================================================================

-- Create the database (run this once manually via HeidiSQL)
CREATE DATABASE IF NOT EXISTS courier_db
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE courier_db;

-- ============================================================================
-- TABLE: service_types
-- Represents delivery service tiers (e.g., Standard, Express, Overnight).
-- ============================================================================
CREATE TABLE IF NOT EXISTS service_types (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(100)    NOT NULL UNIQUE,
    description     VARCHAR(500),
    price_per_kg    DECIMAL(10, 2)  NOT NULL,
    estimated_days  INT             NOT NULL,
    active          BOOLEAN         NOT NULL DEFAULT TRUE
) ENGINE=InnoDB;

-- ============================================================================
-- TABLE: senders
-- Registered customers who can book parcels for delivery.
-- ============================================================================
CREATE TABLE IF NOT EXISTS senders (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    first_name      VARCHAR(100)    NOT NULL,
    last_name       VARCHAR(100)    NOT NULL,
    email           VARCHAR(255)    NOT NULL UNIQUE,
    phone           VARCHAR(20),
    address         VARCHAR(500),
    registered_date DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ============================================================================
-- TABLE: parcels
-- Parcels booked for delivery, linked to a sender and service type.
-- ============================================================================
CREATE TABLE IF NOT EXISTS parcels (
    id                INT AUTO_INCREMENT PRIMARY KEY,
    sender_id         INT             NOT NULL,
    service_type_id   INT             NOT NULL,
    weight            DECIMAL(10, 2)  NOT NULL,
    recipient_name    VARCHAR(200)    NOT NULL,
    recipient_address VARCHAR(500)    NOT NULL,
    recipient_phone   VARCHAR(20),
    description       VARCHAR(500),
    total_cost        DECIMAL(10, 2)  NOT NULL,
    created_date      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_parcel_sender
        FOREIGN KEY (sender_id) REFERENCES senders(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,

    CONSTRAINT fk_parcel_service_type
        FOREIGN KEY (service_type_id) REFERENCES service_types(id)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

-- ============================================================================
-- TABLE: shipments
-- Tracks the delivery lifecycle of a parcel.
-- ============================================================================
CREATE TABLE IF NOT EXISTS shipments (
    id                 INT AUTO_INCREMENT PRIMARY KEY,
    parcel_id          INT             NOT NULL,
    tracking_number    VARCHAR(50)     NOT NULL UNIQUE,
    status             ENUM('PENDING', 'PICKED_UP', 'IN_TRANSIT',
                            'OUT_FOR_DELIVERY', 'DELIVERED', 'CANCELLED')
                                       NOT NULL DEFAULT 'PENDING',
    current_location   VARCHAR(500),
    estimated_delivery DATETIME,
    last_updated       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP
                                       ON UPDATE CURRENT_TIMESTAMP,
    created_date       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_shipment_parcel
        FOREIGN KEY (parcel_id) REFERENCES parcels(id)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

-- ============================================================================
-- INDEXES for frequently queried columns
-- ============================================================================
CREATE INDEX idx_senders_email          ON senders(email);
CREATE INDEX idx_parcels_sender         ON parcels(sender_id);
CREATE INDEX idx_shipments_parcel       ON shipments(parcel_id);
CREATE INDEX idx_shipments_tracking     ON shipments(tracking_number);

-- ============================================================================
-- SEED DATA — Sample service types and senders for testing
-- ============================================================================
INSERT INTO service_types (name, description, price_per_kg, estimated_days, active) VALUES
    ('Standard',  'Regular delivery service with standard handling.',          150.00,  5, TRUE),
    ('Express',   'Fast delivery with priority handling and tracking.',        350.00,  2, TRUE),
    ('Overnight', 'Next-day delivery with premium handling and insurance.',    750.00,  1, TRUE),
    ('Economy',   'Budget-friendly delivery for non-urgent shipments.',         80.00, 10, TRUE);

INSERT INTO senders (first_name, last_name, email, phone, address) VALUES
    ('Super', 'User', 'admin@courier.com', '+254700000000', '123 HQ Tower, Nairobi'),
    ('John', 'Doe', 'john.doe@example.com', '+254711223344', '45 Parklands Rd, Nairobi');

