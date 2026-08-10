package com.courier.client.controller;

import com.courier.client.connection.ServerConnection;
import com.courier.client.util.AlertHelper;
import com.courier.common.dto.ParcelDTO;
import com.courier.common.dto.SenderDTO;
import com.courier.common.dto.ServiceTypeDTO;
import com.courier.common.dto.ShipmentDTO;
import com.courier.common.dto.ShipmentDTO.ShipmentStatus;
import com.courier.common.remote.CourierService;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.rmi.RemoteException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Controller for the Admin (Dispatcher) Dashboard ({@code admin_dashboard.fxml}).
 *
 * <p>This controller manages four tabs, each corresponding to a major entity
 * in the courier system. The methods are organized into sections matching the
 * RMI interface's operation groups:</p>
 *
 * <ul>
 *     <li><strong>Service Types</strong> — full CRUD (add, edit, deactivate, view)</li>
 *     <li><strong>Senders</strong> — read-only list of registered customers</li>
 *     <li><strong>Parcels</strong> — view all parcels, create shipments from them</li>
 *     <li><strong>Shipments</strong> — view all shipments, update status/location</li>
 * </ul>
 *
 * <p>All RMI calls are wrapped in try/catch blocks with user-friendly error
 * alerts via {@link AlertHelper}. Tables auto-refresh after mutations.</p>
 *
 * @author Group 12
 * @version 1.0
 * @see CourierService
 * @see AlertHelper
 */
public class AdminDashboardController {

    // =========================================================================
    // FXML INJECTED FIELDS — Service Types Tab
    // =========================================================================

    /** Table displaying all service types. */
    @FXML private TableView<ServiceTypeDTO> serviceTypeTable;
    /** Column for service type ID. */
    @FXML private TableColumn<ServiceTypeDTO, Number> stIdCol;
    /** Column for service type name. */
    @FXML private TableColumn<ServiceTypeDTO, String> stNameCol;
    /** Column for service type description. */
    @FXML private TableColumn<ServiceTypeDTO, String> stDescCol;
    /** Column for price per kilogram. */
    @FXML private TableColumn<ServiceTypeDTO, Number> stPriceCol;
    /** Column for estimated delivery days. */
    @FXML private TableColumn<ServiceTypeDTO, Number> stDaysCol;
    /** Column for active/inactive status. */
    @FXML private TableColumn<ServiceTypeDTO, String> stActiveCol;

    // =========================================================================
    // FXML INJECTED FIELDS — Senders Tab
    // =========================================================================

    /** Table displaying all registered senders. */
    @FXML private TableView<SenderDTO> senderTable;
    /** Column for sender ID. */
    @FXML private TableColumn<SenderDTO, Number> snIdCol;
    /** Column for sender full name. */
    @FXML private TableColumn<SenderDTO, String> snNameCol;
    /** Column for sender email. */
    @FXML private TableColumn<SenderDTO, String> snEmailCol;
    /** Column for sender phone number. */
    @FXML private TableColumn<SenderDTO, String> snPhoneCol;
    /** Column for sender address. */
    @FXML private TableColumn<SenderDTO, String> snAddressCol;
    /** Column for sender registration date. */
    @FXML private TableColumn<SenderDTO, String> snDateCol;

    // =========================================================================
    // FXML INJECTED FIELDS — Parcels Tab
    // =========================================================================

    /** Table displaying all parcels. */
    @FXML private TableView<ParcelDTO> parcelTable;
    /** Column for parcel ID. */
    @FXML private TableColumn<ParcelDTO, Number> prIdCol;
    /** Column for sender ID of the parcel. */
    @FXML private TableColumn<ParcelDTO, Number> prSenderCol;
    /** Column for service type ID. */
    @FXML private TableColumn<ParcelDTO, Number> prServiceCol;
    /** Column for parcel weight. */
    @FXML private TableColumn<ParcelDTO, Number> prWeightCol;
    /** Column for recipient name. */
    @FXML private TableColumn<ParcelDTO, String> prRecipientCol;
    /** Column for recipient address. */
    @FXML private TableColumn<ParcelDTO, String> prAddressCol;
    /** Column for total cost. */
    @FXML private TableColumn<ParcelDTO, Number> prCostCol;
    /** Column for parcel creation date. */
    @FXML private TableColumn<ParcelDTO, String> prDateCol;

    // =========================================================================
    // FXML INJECTED FIELDS — Shipments Tab
    // =========================================================================

    /** Table displaying all shipments. */
    @FXML private TableView<ShipmentDTO> shipmentTable;
    /** Column for shipment ID. */
    @FXML private TableColumn<ShipmentDTO, Number> shIdCol;
    /** Column for associated parcel ID. */
    @FXML private TableColumn<ShipmentDTO, Number> shParcelCol;
    /** Column for tracking number. */
    @FXML private TableColumn<ShipmentDTO, String> shTrackingCol;
    /** Column for shipment status. */
    @FXML private TableColumn<ShipmentDTO, String> shStatusCol;
    /** Column for current location. */
    @FXML private TableColumn<ShipmentDTO, String> shLocationCol;
    /** Column for estimated delivery date. */
    @FXML private TableColumn<ShipmentDTO, String> shEstCol;
    /** Column for last update timestamp. */
    @FXML private TableColumn<ShipmentDTO, String> shUpdatedCol;

    /** Reference to the remote service. */
    private CourierService service;

    /**
     * Called by JavaFX after FXML loading completes.
     * Configures all table column cell value factories and loads initial data.
     */
    @FXML
    public void initialize() {
        service = ServerConnection.getInstance().getService();
        setupServiceTypeColumns();
        setupSenderColumns();
        setupParcelColumns();
        setupShipmentColumns();
        loadAllData();
    }

    // =========================================================================
    // SERVICE TYPE OPERATIONS
    // =========================================================================

    /**
     * Configures cell value factories for the Service Types table columns.
     */
    private void setupServiceTypeColumns() {
        stIdCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getId()));
        stNameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
        stDescCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDescription()));
        stPriceCol.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getPricePerKg()));
        stDaysCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getEstimatedDays()));
        stActiveCol.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().isActive() ? "Yes" : "No"));
    }

    /**
     * Loads all service types from the server into the table.
     */
    private void loadServiceTypes() {
        try {
            List<ServiceTypeDTO> types = service.getAllServiceTypes();
            serviceTypeTable.setItems(FXCollections.observableArrayList(types));
        } catch (RemoteException e) {
            AlertHelper.showError("Load Error", "Could not load service types.\n" + e.getMessage());
        }
    }

    /**
     * Handles the "Add" button for service types.
     *
     * <p>Opens a series of input dialogs to collect the service type details,
     * then calls {@link CourierService#addServiceType(ServiceTypeDTO)}.</p>
     */
    @FXML
    private void onAddServiceType() {
        try {
            String name = promptInput("Add Service Type", "Service type name:");
            if (name == null) return;

            String desc = promptInput("Add Service Type", "Description:");
            if (desc == null) return;

            String priceStr = promptInput("Add Service Type", "Price per kg:");
            if (priceStr == null) return;
            double price = Double.parseDouble(priceStr);

            String daysStr = promptInput("Add Service Type", "Estimated delivery days:");
            if (daysStr == null) return;
            int days = Integer.parseInt(daysStr);

            ServiceTypeDTO st = new ServiceTypeDTO();
            st.setName(name);
            st.setDescription(desc);
            st.setPricePerKg(price);
            st.setEstimatedDays(days);
            st.setActive(true);

            service.addServiceType(st);
            AlertHelper.showInfo("Success", "Service type \"" + name + "\" added successfully.");
            loadServiceTypes();
        } catch (NumberFormatException e) {
            AlertHelper.showError("Validation Error", "Invalid number format. Please enter valid numbers.");
        } catch (RemoteException e) {
            AlertHelper.showError("Error", "Could not add service type.\n" + e.getMessage());
        }
    }

    /**
     * Handles the "Edit" button for service types.
     *
     * <p>Requires a row to be selected. Opens pre-filled dialogs for each field
     * and calls {@link CourierService#updateServiceType(ServiceTypeDTO)}.</p>
     */
    @FXML
    private void onEditServiceType() {
        ServiceTypeDTO selected = serviceTypeTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showError("No Selection", "Please select a service type to edit.");
            return;
        }

        try {
            String name = promptInput("Edit Service Type", "Name:", selected.getName());
            if (name == null) return;

            String desc = promptInput("Edit Service Type", "Description:", selected.getDescription());
            if (desc == null) return;

            String priceStr = promptInput("Edit Service Type", "Price per kg:",
                    String.valueOf(selected.getPricePerKg()));
            if (priceStr == null) return;
            double price = Double.parseDouble(priceStr);

            String daysStr = promptInput("Edit Service Type", "Estimated days:",
                    String.valueOf(selected.getEstimatedDays()));
            if (daysStr == null) return;
            int days = Integer.parseInt(daysStr);

            selected.setName(name);
            selected.setDescription(desc);
            selected.setPricePerKg(price);
            selected.setEstimatedDays(days);

            boolean updated = service.updateServiceType(selected);
            if (updated) {
                AlertHelper.showInfo("Success", "Service type updated successfully.");
            } else {
                AlertHelper.showError("Error", "Service type not found on the server.");
            }
            loadServiceTypes();
        } catch (NumberFormatException e) {
            AlertHelper.showError("Validation Error", "Invalid number format.");
        } catch (RemoteException e) {
            AlertHelper.showError("Error", "Could not update service type.\n" + e.getMessage());
        }
    }

    /**
     * Handles the "Deactivate" button for service types.
     *
     * <p>Requires a row to be selected. Confirms the action, then calls
     * {@link CourierService#deleteServiceType(int)} which performs a soft delete.</p>
     */
    @FXML
    private void onDeleteServiceType() {
        ServiceTypeDTO selected = serviceTypeTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showError("No Selection", "Please select a service type to deactivate.");
            return;
        }

        boolean confirmed = AlertHelper.showConfirmation("Confirm Deactivation",
                "Deactivate service type \"" + selected.getName() + "\"?\n" +
                "Existing parcels using this type will not be affected.");
        if (!confirmed) return;

        try {
            service.deleteServiceType(selected.getId());
            AlertHelper.showInfo("Success", "Service type deactivated.");
            loadServiceTypes();
        } catch (RemoteException e) {
            AlertHelper.showError("Error", "Could not deactivate service type.\n" + e.getMessage());
        }
    }

    /**
     * Refreshes the service types table.
     */
    @FXML
    private void onRefreshServiceTypes() {
        loadServiceTypes();
    }

    // =========================================================================
    // SENDER OPERATIONS
    // =========================================================================

    /**
     * Configures cell value factories for the Senders table columns.
     */
    private void setupSenderColumns() {
        snIdCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getId()));
        snNameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFullName()));
        snEmailCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEmail()));
        snPhoneCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPhone()));
        snAddressCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAddress()));
        snDateCol.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getRegisteredDate() != null
                        ? c.getValue().getRegisteredDate().toString() : ""));
    }

    /**
     * Loads all senders from the server into the table.
     */
    private void loadSenders() {
        try {
            List<SenderDTO> senders = service.getAllSenders();
            senderTable.setItems(FXCollections.observableArrayList(senders));
        } catch (RemoteException e) {
            AlertHelper.showError("Load Error", "Could not load senders.\n" + e.getMessage());
        }
    }

    /**
     * Handles the "Add" button for senders.
     *
     * <p>Opens a series of input dialogs to collect the sender details,
     * then calls {@link CourierService#registerSender(SenderDTO)}.</p>
     */
    @FXML
    private void onAddSender() {
        try {
            String firstName = promptInput("Add Sender", "First name:");
            if (firstName == null) return;

            String lastName = promptInput("Add Sender", "Last name:");
            if (lastName == null) return;

            String email = promptInput("Add Sender", "Email address:");
            if (email == null) return;

            String phone = promptInput("Add Sender", "Phone number:");
            if (phone == null) return;

            String address = promptInput("Add Sender", "Address:");
            if (address == null) return;

            SenderDTO sender = new SenderDTO();
            sender.setFirstName(firstName);
            sender.setLastName(lastName);
            sender.setEmail(email);
            sender.setPhone(phone);
            sender.setAddress(address);

            service.registerSender(sender);
            AlertHelper.showInfo("Success", "Sender \"" + firstName + " " + lastName + "\" registered successfully.");
            loadSenders();
        } catch (RemoteException e) {
            AlertHelper.showError("Error", "Could not register sender.\n" + e.getMessage());
        }
    }

    /**
     * Handles the "Edit" button for senders.
     *
     * <p>Requires a row to be selected. Opens pre-filled dialogs for each field
     * and calls {@link CourierService#updateSender(SenderDTO)}.</p>
     */
    @FXML
    private void onEditSender() {
        SenderDTO selected = senderTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showError("No Selection", "Please select a sender to edit.");
            return;
        }

        try {
            String firstName = promptInput("Edit Sender", "First name:", selected.getFirstName());
            if (firstName == null) return;

            String lastName = promptInput("Edit Sender", "Last name:", selected.getLastName());
            if (lastName == null) return;

            String email = promptInput("Edit Sender", "Email:", selected.getEmail());
            if (email == null) return;

            String phone = promptInput("Edit Sender", "Phone:", selected.getPhone());
            if (phone == null) return;

            String address = promptInput("Edit Sender", "Address:", selected.getAddress());
            if (address == null) return;

            selected.setFirstName(firstName);
            selected.setLastName(lastName);
            selected.setEmail(email);
            selected.setPhone(phone);
            selected.setAddress(address);

            boolean updated = service.updateSender(selected);
            if (updated) {
                AlertHelper.showInfo("Success", "Sender updated successfully.");
            } else {
                AlertHelper.showError("Error", "Sender not found on the server.");
            }
            loadSenders();
        } catch (RemoteException e) {
            AlertHelper.showError("Error", "Could not update sender.\n" + e.getMessage());
        }
    }

    /**
     * Handles the "Delete" button for senders.
     *
     * <p>Requires a row to be selected. Confirms the action, then calls
     * {@link CourierService#deleteSender(int)}. Will fail if the sender
     * has existing parcels.</p>
     */
    @FXML
    private void onDeleteSender() {
        SenderDTO selected = senderTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showError("No Selection", "Please select a sender to delete.");
            return;
        }

        boolean confirmed = AlertHelper.showConfirmation("Confirm Deletion",
                "Delete sender \"" + selected.getFullName() + "\"?\n" +
                "This will fail if the sender has existing parcels.");
        if (!confirmed) return;

        try {
            boolean deleted = service.deleteSender(selected.getId());
            if (deleted) {
                AlertHelper.showInfo("Success", "Sender deleted.");
            } else {
                AlertHelper.showError("Error", "Sender not found on the server.");
            }
            loadSenders();
        } catch (RemoteException e) {
            AlertHelper.showError("Error", "Could not delete sender.\n" + e.getMessage());
        }
    }

    /**
     * Refreshes the senders table.
     */
    @FXML
    private void onRefreshSenders() {
        loadSenders();
    }

    // =========================================================================
    // PARCEL OPERATIONS
    // =========================================================================

    /**
     * Configures cell value factories for the Parcels table columns.
     */
    private void setupParcelColumns() {
        prIdCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getId()));
        prSenderCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getSenderId()));
        prServiceCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getServiceTypeId()));
        prWeightCol.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getWeight()));
        prRecipientCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRecipientName()));
        prAddressCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRecipientAddress()));
        prCostCol.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getTotalCost()));
        prDateCol.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getCreatedDate() != null
                        ? c.getValue().getCreatedDate().toString() : ""));
    }

    /**
     * Loads all parcels from the server into the table.
     */
    private void loadParcels() {
        try {
            List<ParcelDTO> parcels = service.getAllParcels();
            parcelTable.setItems(FXCollections.observableArrayList(parcels));
        } catch (RemoteException e) {
            AlertHelper.showError("Load Error", "Could not load parcels.\n" + e.getMessage());
        }
    }

    /**
     * Handles the "Create Shipment" button.
     *
     * <p>Requires a parcel to be selected. Prompts for initial location and
     * estimated delivery date, then calls {@link CourierService#createShipment(ShipmentDTO)}.</p>
     */
    @FXML
    private void onCreateShipment() {
        ParcelDTO selected = parcelTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showError("No Selection", "Please select a parcel to create a shipment for.");
            return;
        }

        try {
            String location = promptInput("Create Shipment",
                    "Initial location (e.g., \"Warehouse - Johannesburg\"):");
            if (location == null) return;

            ShipmentDTO shipment = new ShipmentDTO();
            shipment.setParcelId(selected.getId());
            shipment.setCurrentLocation(location);
            shipment.setEstimatedDelivery(LocalDateTime.now().plusDays(5));

            ShipmentDTO created = service.createShipment(shipment);
            AlertHelper.showInfo("Shipment Created",
                    "Shipment created successfully!\n\n" +
                    "Tracking Number: " + created.getTrackingNumber() + "\n" +
                    "Status: " + created.getStatus() + "\n" +
                    "Location: " + created.getCurrentLocation());
            loadShipments();
        } catch (RemoteException e) {
            AlertHelper.showError("Error", "Could not create shipment.\n" + e.getMessage());
        }
    }

    /**
     * Handles the "Edit" button for parcels.
     *
     * <p>Requires a row to be selected. Opens pre-filled dialogs for editable fields
     * and calls {@link CourierService#updateParcel(ParcelDTO)}.</p>
     */
    @FXML
    private void onEditParcel() {
        ParcelDTO selected = parcelTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showError("No Selection", "Please select a parcel to edit.");
            return;
        }

        try {
            String recipientName = promptInput("Edit Parcel", "Recipient name:", selected.getRecipientName());
            if (recipientName == null) return;

            String recipientAddress = promptInput("Edit Parcel", "Recipient address:", selected.getRecipientAddress());
            if (recipientAddress == null) return;

            String recipientPhone = promptInput("Edit Parcel", "Recipient phone:", selected.getRecipientPhone());
            if (recipientPhone == null) return;

            String weightStr = promptInput("Edit Parcel", "Weight (kg):",
                    String.valueOf(selected.getWeight()));
            if (weightStr == null) return;
            double weight = Double.parseDouble(weightStr);

            String description = promptInput("Edit Parcel", "Description:", selected.getDescription());
            if (description == null) return;

            selected.setRecipientName(recipientName);
            selected.setRecipientAddress(recipientAddress);
            selected.setRecipientPhone(recipientPhone);
            selected.setWeight(weight);
            selected.setDescription(description);

            // Recalculate cost based on updated weight
            ServiceTypeDTO serviceType = service.getServiceType(selected.getServiceTypeId());
            if (serviceType != null) {
                selected.setTotalCost(weight * serviceType.getPricePerKg());
            }

            boolean updated = service.updateParcel(selected);
            if (updated) {
                AlertHelper.showInfo("Success", "Parcel updated successfully.");
            } else {
                AlertHelper.showError("Error", "Parcel not found on the server.");
            }
            loadParcels();
        } catch (NumberFormatException e) {
            AlertHelper.showError("Validation Error", "Invalid number format for weight.");
        } catch (RemoteException e) {
            AlertHelper.showError("Error", "Could not update parcel.\n" + e.getMessage());
        }
    }

    /**
     * Handles the "Delete" button for parcels.
     *
     * <p>Requires a row to be selected. Confirms the action, then calls
     * {@link CourierService#deleteParcel(int)}. Will fail if the parcel
     * has existing shipments.</p>
     */
    @FXML
    private void onDeleteParcel() {
        ParcelDTO selected = parcelTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showError("No Selection", "Please select a parcel to delete.");
            return;
        }

        boolean confirmed = AlertHelper.showConfirmation("Confirm Deletion",
                "Delete parcel ID=" + selected.getId() + " (Recipient: " +
                selected.getRecipientName() + ")?\n" +
                "This will fail if the parcel has existing shipments.");
        if (!confirmed) return;

        try {
            boolean deleted = service.deleteParcel(selected.getId());
            if (deleted) {
                AlertHelper.showInfo("Success", "Parcel deleted.");
            } else {
                AlertHelper.showError("Error", "Parcel not found on the server.");
            }
            loadParcels();
        } catch (RemoteException e) {
            AlertHelper.showError("Error", "Could not delete parcel.\n" + e.getMessage());
        }
    }

    /**
     * Refreshes the parcels table.
     */
    @FXML
    private void onRefreshParcels() {
        loadParcels();
    }

    // =========================================================================
    // SHIPMENT OPERATIONS
    // =========================================================================

    /**
     * Configures cell value factories for the Shipments table columns.
     */
    private void setupShipmentColumns() {
        shIdCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getId()));
        shParcelCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getParcelId()));
        shTrackingCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTrackingNumber()));
        shStatusCol.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getStatus() != null ? c.getValue().getStatus().name() : ""));
        shLocationCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCurrentLocation()));
        shEstCol.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getEstimatedDelivery() != null
                        ? c.getValue().getEstimatedDelivery().toString() : ""));
        shUpdatedCol.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getLastUpdated() != null
                        ? c.getValue().getLastUpdated().toString() : ""));
    }

    /**
     * Loads all shipments from the server into the table.
     *
     * <p>Since the RMI interface provides shipments per-parcel, this method
     * iterates over all parcels and collects their shipments. This ensures
     * a complete view for the Admin.</p>
     */
    private void loadShipments() {
        try {
            List<ParcelDTO> parcels = service.getAllParcels();
            List<ShipmentDTO> allShipments = new ArrayList<>();
            for (ParcelDTO parcel : parcels) {
                allShipments.addAll(service.getShipmentsByParcel(parcel.getId()));
            }
            shipmentTable.setItems(FXCollections.observableArrayList(allShipments));
        } catch (RemoteException e) {
            AlertHelper.showError("Load Error", "Could not load shipments.\n" + e.getMessage());
        }
    }

    /**
     * Handles the "Update Status" button for shipments.
     *
     * <p>Requires a shipment to be selected. Opens a choice dialog for the new
     * status and a text input for the current location, then calls
     * {@link CourierService#updateShipmentStatus(int, ShipmentStatus, String)}.</p>
     */
    @FXML
    private void onUpdateShipmentStatus() {
        ShipmentDTO selected = shipmentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showError("No Selection", "Please select a shipment to update.");
            return;
        }

        // Status choice dialog
        ChoiceDialog<ShipmentStatus> statusDialog = new ChoiceDialog<>(
                selected.getStatus(), ShipmentStatus.values());
        statusDialog.setTitle("Update Shipment Status");
        statusDialog.setHeaderText("Shipment: " + selected.getTrackingNumber());
        statusDialog.setContentText("Select new status:");
        Optional<ShipmentStatus> statusResult = statusDialog.showAndWait();
        if (statusResult.isEmpty()) return;

        // Location input
        String location = promptInput("Update Location", "Current location:",
                selected.getCurrentLocation());
        if (location == null) return;

        try {
            boolean updated = service.updateShipmentStatus(
                    selected.getId(), statusResult.get(), location);
            if (updated) {
                AlertHelper.showInfo("Success", "Shipment status updated to " +
                        statusResult.get().name() + ".");
            } else {
                AlertHelper.showError("Error", "Shipment not found on the server.");
            }
            loadShipments();
        } catch (RemoteException e) {
            AlertHelper.showError("Error", "Could not update shipment.\n" + e.getMessage());
        }
    }

    /**
     * Handles the "Delete" button for shipments.
     *
     * <p>Requires a row to be selected. Confirms the action, then calls
     * {@link CourierService#deleteShipment(int)}.</p>
     */
    @FXML
    private void onDeleteShipment() {
        ShipmentDTO selected = shipmentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showError("No Selection", "Please select a shipment to delete.");
            return;
        }

        boolean confirmed = AlertHelper.showConfirmation("Confirm Deletion",
                "Delete shipment \"" + selected.getTrackingNumber() + "\"?\n" +
                "This action cannot be undone.");
        if (!confirmed) return;

        try {
            boolean deleted = service.deleteShipment(selected.getId());
            if (deleted) {
                AlertHelper.showInfo("Success", "Shipment deleted.");
            } else {
                AlertHelper.showError("Error", "Shipment not found on the server.");
            }
            loadShipments();
        } catch (RemoteException e) {
            AlertHelper.showError("Error", "Could not delete shipment.\n" + e.getMessage());
        }
    }

    /**
     * Refreshes the shipments table.
     */
    @FXML
    private void onRefreshShipments() {
        loadShipments();
    }

    // =========================================================================
    // NAVIGATION
    // =========================================================================

    /**
     * Handles the "Logout" button. Navigates back to the login screen.
     */
    @FXML
    private void onLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) serviceTypeTable.getScene().getWindow();
            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            stage.setTitle("Courier & Logistics System");
            stage.setScene(scene);
        } catch (IOException e) {
            AlertHelper.showError("Navigation Error", "Could not return to login screen.");
        }
    }

    // =========================================================================
    // PRIVATE HELPERS
    // =========================================================================

    /**
     * Loads data for all tabs on initial startup.
     */
    private void loadAllData() {
        loadServiceTypes();
        loadSenders();
        loadParcels();
        loadShipments();
    }

    /**
     * Displays a text input dialog and returns the user's input.
     *
     * @param title  the dialog title
     * @param prompt the prompt text
     * @return the trimmed input, or {@code null} if cancelled
     */
    private String promptInput(String title, String prompt) {
        return promptInput(title, prompt, "");
    }

    /**
     * Displays a text input dialog with a default value and returns the user's input.
     *
     * @param title        the dialog title
     * @param prompt       the prompt text
     * @param defaultValue the default value pre-filled in the text field
     * @return the trimmed input, or {@code null} if cancelled or empty
     */
    private String promptInput(String title, String prompt, String defaultValue) {
        TextInputDialog dialog = new TextInputDialog(defaultValue != null ? defaultValue : "");
        dialog.setTitle(title);
        dialog.setHeaderText(null);
        dialog.setContentText(prompt);
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && !result.get().trim().isEmpty()) {
            return result.get().trim();
        }
        return null;
    }
}
