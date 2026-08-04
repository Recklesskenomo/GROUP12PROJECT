package com.courier.client.controller;

import com.courier.client.connection.ServerConnection;
import com.courier.client.util.AlertHelper;
import com.courier.common.dto.ParcelDTO;
import com.courier.common.dto.ServiceTypeDTO;
import com.courier.common.dto.ShipmentDTO;
import com.courier.common.dto.SenderDTO;
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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Controller for the Sender (User) Dashboard ({@code sender_dashboard.fxml}).
 *
 * <p>This controller manages three tabs for the logged-in sender:</p>
 * <ul>
 *     <li><strong>Book Parcel</strong> — select a service type, enter recipient
 *         details and weight, then create a parcel via RMI</li>
 *     <li><strong>My Parcels</strong> — view all parcels booked by this sender</li>
 *     <li><strong>Track Shipment</strong> — enter a tracking number to view status,
 *         or browse the full shipment history</li>
 * </ul>
 *
 * <p>The sender context is passed from the {@link LoginController} via
 * {@link #initSender(SenderDTO)} after FXML loading.</p>
 *
 * @author Group 12
 * @version 1.0
 * @see CourierService
 * @see LoginController
 */
public class SenderDashboardController {

    // =========================================================================
    // FXML INJECTED FIELDS — Top Bar
    // =========================================================================

    /** Welcome label showing the sender's name. */
    @FXML private Label welcomeLabel;

    // =========================================================================
    // FXML INJECTED FIELDS — Book Parcel Tab
    // =========================================================================

    /** Combo box for selecting a service type. */
    @FXML private ComboBox<ServiceTypeDTO> serviceTypeCombo;
    /** Text field for recipient's full name. */
    @FXML private TextField recipientNameField;
    /** Text field for recipient's delivery address. */
    @FXML private TextField recipientAddressField;
    /** Text field for recipient's phone number. */
    @FXML private TextField recipientPhoneField;
    /** Text field for parcel weight in kilograms. */
    @FXML private TextField weightField;
    /** Text field for a brief description of parcel contents. */
    @FXML private TextField descriptionField;
    /** Label showing the estimated cost preview. */
    @FXML private Label costPreviewLabel;

    // =========================================================================
    // FXML INJECTED FIELDS — My Parcels Tab
    // =========================================================================

    /** Table displaying the sender's parcels. */
    @FXML private TableView<ParcelDTO> myParcelTable;
    /** Column for parcel ID. */
    @FXML private TableColumn<ParcelDTO, Number> mpIdCol;
    /** Column for service type ID. */
    @FXML private TableColumn<ParcelDTO, Number> mpServiceCol;
    /** Column for parcel weight. */
    @FXML private TableColumn<ParcelDTO, Number> mpWeightCol;
    /** Column for recipient name. */
    @FXML private TableColumn<ParcelDTO, String> mpRecipientCol;
    /** Column for recipient address. */
    @FXML private TableColumn<ParcelDTO, String> mpAddressCol;
    /** Column for total cost. */
    @FXML private TableColumn<ParcelDTO, Number> mpCostCol;
    /** Column for booking date. */
    @FXML private TableColumn<ParcelDTO, String> mpDateCol;

    // =========================================================================
    // FXML INJECTED FIELDS — Track Shipment Tab
    // =========================================================================

    /** Text field for entering a tracking number. */
    @FXML private TextField trackingField;
    /** Container for the tracking result display. */
    @FXML private VBox trackingResultBox;
    /** Label showing the shipment status. */
    @FXML private Label trackingStatusLabel;
    /** Label showing the current location. */
    @FXML private Label trackingLocationLabel;
    /** Label showing estimated delivery date. */
    @FXML private Label trackingEstLabel;
    /** Label showing last updated timestamp. */
    @FXML private Label trackingUpdatedLabel;

    /** Table displaying the sender's shipment history. */
    @FXML private TableView<ShipmentDTO> historyTable;
    /** Column for tracking number. */
    @FXML private TableColumn<ShipmentDTO, String> hsTrackingCol;
    /** Column for associated parcel ID. */
    @FXML private TableColumn<ShipmentDTO, Number> hsParcelCol;
    /** Column for shipment status. */
    @FXML private TableColumn<ShipmentDTO, String> hsStatusCol;
    /** Column for current location. */
    @FXML private TableColumn<ShipmentDTO, String> hsLocationCol;
    /** Column for estimated delivery. */
    @FXML private TableColumn<ShipmentDTO, String> hsEstCol;
    /** Column for last updated timestamp. */
    @FXML private TableColumn<ShipmentDTO, String> hsUpdatedCol;

    /** Reference to the remote service. */
    private CourierService service;

    /** The currently logged-in sender. */
    private SenderDTO currentSender;

    /**
     * Called by JavaFX after FXML loading completes.
     * Sets up table columns and configures the service type combo box.
     */
    @FXML
    public void initialize() {
        service = ServerConnection.getInstance().getService();
        setupParcelColumns();
        setupHistoryColumns();
        setupServiceTypeCombo();
    }

    /**
     * Initializes the controller with the logged-in sender's data.
     *
     * <p>This method is called by the {@link LoginController} after FXML
     * loading to pass the authenticated sender context. It updates the
     * welcome label and loads the sender's data into all tabs.</p>
     *
     * @param sender the authenticated sender DTO
     */
    public void initSender(SenderDTO sender) {
        this.currentSender = sender;
        welcomeLabel.setText("Welcome, " + sender.getFullName());
        loadServiceTypes();
        loadMyParcels();
        loadShipmentHistory();
    }

    // =========================================================================
    // BOOK PARCEL OPERATIONS
    // =========================================================================

    /**
     * Configures the service type combo box with a custom string converter
     * that displays the service name, price, and estimated days.
     */
    private void setupServiceTypeCombo() {
        serviceTypeCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(ServiceTypeDTO st) {
                if (st == null) return "";
                return st.getName() + " — R" + String.format("%.2f", st.getPricePerKg()) +
                       "/kg (" + st.getEstimatedDays() + " days)";
            }

            @Override
            public ServiceTypeDTO fromString(String string) {
                return null; // Not needed for non-editable combo box
            }
        });

        // Update cost preview when service type or weight changes
        serviceTypeCombo.setOnAction(e -> updateCostPreview());
        weightField.textProperty().addListener((obs, oldVal, newVal) -> updateCostPreview());
    }

    /**
     * Loads active service types into the combo box.
     */
    private void loadServiceTypes() {
        try {
            List<ServiceTypeDTO> types = service.getAllServiceTypes();
            // Filter to only active service types
            List<ServiceTypeDTO> activeTypes = types.stream()
                    .filter(ServiceTypeDTO::isActive)
                    .toList();
            serviceTypeCombo.setItems(FXCollections.observableArrayList(activeTypes));
        } catch (RemoteException e) {
            AlertHelper.showError("Load Error", "Could not load service types.\n" + e.getMessage());
        }
    }

    /**
     * Updates the cost preview label based on the selected service type and weight.
     */
    private void updateCostPreview() {
        ServiceTypeDTO selected = serviceTypeCombo.getValue();
        String weightText = weightField.getText().trim();

        if (selected != null && !weightText.isEmpty()) {
            try {
                double weight = Double.parseDouble(weightText);
                double cost = weight * selected.getPricePerKg();
                costPreviewLabel.setText(String.format("Estimated Cost: R%.2f", cost));
            } catch (NumberFormatException e) {
                costPreviewLabel.setText("Enter a valid weight");
            }
        } else {
            costPreviewLabel.setText("");
        }
    }

    /**
     * Handles the "Book Parcel" button click.
     *
     * <p>Validates all form fields, creates a {@link ParcelDTO}, and calls
     * {@link CourierService#createParcel(ParcelDTO)}. The server calculates
     * the total cost. On success, shows a confirmation and clears the form.</p>
     */
    @FXML
    private void onBookParcel() {
        // Validate inputs
        ServiceTypeDTO selectedType = serviceTypeCombo.getValue();
        if (selectedType == null) {
            AlertHelper.showError("Validation Error", "Please select a service type.");
            return;
        }
        String recipientName = recipientNameField.getText().trim();
        if (recipientName.isEmpty()) {
            AlertHelper.showError("Validation Error", "Please enter the recipient's name.");
            return;
        }
        String recipientAddress = recipientAddressField.getText().trim();
        if (recipientAddress.isEmpty()) {
            AlertHelper.showError("Validation Error", "Please enter the delivery address.");
            return;
        }
        String recipientPhone = recipientPhoneField.getText().trim();
        String weightText = weightField.getText().trim();
        if (weightText.isEmpty()) {
            AlertHelper.showError("Validation Error", "Please enter the parcel weight.");
            return;
        }

        double weight;
        try {
            weight = Double.parseDouble(weightText);
            if (weight <= 0) {
                AlertHelper.showError("Validation Error", "Weight must be a positive number.");
                return;
            }
        } catch (NumberFormatException e) {
            AlertHelper.showError("Validation Error", "Please enter a valid weight (e.g., 2.5).");
            return;
        }

        String description = descriptionField.getText().trim();

        // Create the parcel DTO
        ParcelDTO parcel = new ParcelDTO();
        parcel.setSenderId(currentSender.getId());
        parcel.setServiceTypeId(selectedType.getId());
        parcel.setWeight(weight);
        parcel.setRecipientName(recipientName);
        parcel.setRecipientAddress(recipientAddress);
        parcel.setRecipientPhone(recipientPhone);
        parcel.setDescription(description);

        try {
            ParcelDTO created = service.createParcel(parcel);
            AlertHelper.showInfo("Parcel Booked",
                    "Your parcel has been booked successfully!\n\n" +
                    "Parcel ID: " + created.getId() + "\n" +
                    "Service: " + selectedType.getName() + "\n" +
                    "Weight: " + weight + " kg\n" +
                    "Total Cost: R" + String.format("%.2f", created.getTotalCost()) + "\n\n" +
                    "The dispatcher will create a shipment for your parcel.");

            // Clear the form
            serviceTypeCombo.setValue(null);
            recipientNameField.clear();
            recipientAddressField.clear();
            recipientPhoneField.clear();
            weightField.clear();
            descriptionField.clear();
            costPreviewLabel.setText("");

            // Refresh the parcels tab
            loadMyParcels();
        } catch (RemoteException e) {
            AlertHelper.showError("Booking Failed", "Could not book parcel.\n" + e.getMessage());
        }
    }

    // =========================================================================
    // MY PARCELS OPERATIONS
    // =========================================================================

    /**
     * Configures cell value factories for the My Parcels table columns.
     */
    private void setupParcelColumns() {
        mpIdCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getId()));
        mpServiceCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getServiceTypeId()));
        mpWeightCol.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getWeight()));
        mpRecipientCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRecipientName()));
        mpAddressCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRecipientAddress()));
        mpCostCol.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getTotalCost()));
        mpDateCol.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getCreatedDate() != null
                        ? c.getValue().getCreatedDate().toString() : ""));
    }

    /**
     * Loads the sender's parcels from the server into the table.
     */
    private void loadMyParcels() {
        if (currentSender == null) return;
        try {
            List<ParcelDTO> parcels = service.getParcelsBySender(currentSender.getId());
            myParcelTable.setItems(FXCollections.observableArrayList(parcels));
        } catch (RemoteException e) {
            AlertHelper.showError("Load Error", "Could not load your parcels.\n" + e.getMessage());
        }
    }

    /**
     * Refreshes the My Parcels table.
     */
    @FXML
    private void onRefreshMyParcels() {
        loadMyParcels();
    }

    // =========================================================================
    // TRACK SHIPMENT OPERATIONS
    // =========================================================================

    /**
     * Configures cell value factories for the shipment history table columns.
     */
    private void setupHistoryColumns() {
        hsTrackingCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTrackingNumber()));
        hsParcelCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getParcelId()));
        hsStatusCol.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getStatus() != null ? c.getValue().getStatus().name() : ""));
        hsLocationCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCurrentLocation()));
        hsEstCol.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getEstimatedDelivery() != null
                        ? c.getValue().getEstimatedDelivery().toString() : ""));
        hsUpdatedCol.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getLastUpdated() != null
                        ? c.getValue().getLastUpdated().toString() : ""));
    }

    /**
     * Handles the "Track" button click.
     *
     * <p>Looks up the shipment by tracking number via
     * {@link CourierService#getShipmentByTrackingNumber(String)} and displays
     * the status, location, and timestamps in the tracking result area.</p>
     */
    @FXML
    private void onTrack() {
        String trackingNumber = trackingField.getText().trim();
        if (trackingNumber.isEmpty()) {
            AlertHelper.showError("Validation Error", "Please enter a tracking number.");
            return;
        }

        try {
            ShipmentDTO shipment = service.getShipmentByTrackingNumber(trackingNumber);
            if (shipment == null) {
                trackingResultBox.setVisible(false);
                trackingResultBox.setManaged(false);
                AlertHelper.showError("Not Found",
                        "No shipment found with tracking number: " + trackingNumber);
                return;
            }

            // Display the tracking result
            trackingStatusLabel.setText("Status: " + shipment.getStatus().name());
            trackingLocationLabel.setText("Location: " +
                    (shipment.getCurrentLocation() != null ? shipment.getCurrentLocation() : "Unknown"));
            trackingEstLabel.setText("Estimated Delivery: " +
                    (shipment.getEstimatedDelivery() != null
                            ? shipment.getEstimatedDelivery().toString() : "TBD"));
            trackingUpdatedLabel.setText("Last Updated: " +
                    (shipment.getLastUpdated() != null
                            ? shipment.getLastUpdated().toString() : "N/A"));

            trackingResultBox.setVisible(true);
            trackingResultBox.setManaged(true);
        } catch (RemoteException e) {
            AlertHelper.showError("Tracking Error", "Could not track shipment.\n" + e.getMessage());
        }
    }

    /**
     * Loads the sender's full shipment history into the history table.
     */
    private void loadShipmentHistory() {
        if (currentSender == null) return;
        try {
            List<ShipmentDTO> history = service.getShipmentHistory(currentSender.getId());
            historyTable.setItems(FXCollections.observableArrayList(history));
        } catch (RemoteException e) {
            AlertHelper.showError("Load Error", "Could not load shipment history.\n" + e.getMessage());
        }
    }

    /**
     * Refreshes the shipment history table.
     */
    @FXML
    private void onRefreshHistory() {
        loadShipmentHistory();
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
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            stage.setTitle("Courier & Logistics System");
            stage.setScene(scene);
        } catch (IOException e) {
            AlertHelper.showError("Navigation Error", "Could not return to login screen.");
        }
    }
}
