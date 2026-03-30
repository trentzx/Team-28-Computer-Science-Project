package com.campusbooking.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.events.*;
import service.SystemData;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Predicate;

/**
 * Controller for the Event Management panel.
 * Handles: List Events, Create Event, Update Event, Cancel Event (with booking cascade),
 * Search/Filter, and View Event Roster.
 */
public class EventManagementController implements Initializable {

    // ── Search / Filter ───────────────────────────────────────────────────────
    @FXML private TextField        searchTitleField;
    @FXML private ComboBox<String> filterTypeCombo;

    // ── Event table ───────────────────────────────────────────────────────────
    @FXML private TableView<Event>          eventTableView;
    @FXML private TableColumn<Event,String>  colId;
    @FXML private TableColumn<Event,String>  colTitle;
    @FXML private TableColumn<Event,String>  colDate;
    @FXML private TableColumn<Event,String>  colLocation;
    @FXML private TableColumn<Event,Integer> colCapacity;
    @FXML private TableColumn<Event,String>  colStatus;
    @FXML private TableColumn<Event,String>  colType;

    // ── Create / Update form ──────────────────────────────────────────────────
    @FXML private TextField        formEventId;
    @FXML private TextField        formTitle;
    @FXML private TextField        formDateTime;
    @FXML private TextField        formLocation;
    @FXML private TextField        formCapacity;
    @FXML private ComboBox<String> formEventType;
    @FXML private TextField        formTypeSpecific;   // topic / speakerName / ageRestriction
    @FXML private Label            formTypeSpecificLabel;

    // ── Roster view ───────────────────────────────────────────────────────────
    @FXML private TextArea rosterArea;

    // ── Status message ────────────────────────────────────────────────────────
    @FXML private Label statusLabel;

    // The real model.events.Event list from SystemData, wrapped for the TableView
    private final ObservableList<Event> masterEventList = FXCollections.observableArrayList();

    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configureTableColumns();
        loadEventsFromSystemData();
        initSearchAndFilter();

        // Populate form type dropdown
        formEventType.getItems().addAll("Workshop", "Seminar", "Concert");

        // Auto-fill the form when a row is selected
        eventTableView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, selected) -> {
                    if (selected != null) fillFormFromEvent(selected);
                }
        );

        // Update the type-specific label when the type combo changes
        formEventType.valueProperty().addListener((obs, o, type) -> updateTypeLabel(type));
    }

    private void configureTableColumns() {
        colId      .setCellValueFactory(new PropertyValueFactory<>("eventId"));
        colTitle   .setCellValueFactory(new PropertyValueFactory<>("title"));
        colDate    .setCellValueFactory(new PropertyValueFactory<>("dateTime"));
        colLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        colCapacity.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        colStatus  .setCellValueFactory(new PropertyValueFactory<>("status"));
        colType    .setCellValueFactory(new PropertyValueFactory<>("eventType"));
    }

    private void loadEventsFromSystemData() {
        masterEventList.setAll(SystemData.getInstance().getEvents());
        filterTypeCombo.getItems().addAll("All Types", "Workshop", "Seminar", "Concert");
        filterTypeCombo.getSelectionModel().selectFirst();
    }

    private void initSearchAndFilter() {
        FilteredList<Event> filtered = new FilteredList<>(masterEventList, e -> true);

        searchTitleField.textProperty().addListener((obs, o, n) ->
                filtered.setPredicate(buildPredicate(n, filterTypeCombo.getValue())));

        filterTypeCombo.valueProperty().addListener((obs, o, n) ->
                filtered.setPredicate(buildPredicate(searchTitleField.getText(), n)));

        SortedList<Event> sorted = new SortedList<>(filtered);
        sorted.comparatorProperty().bind(eventTableView.comparatorProperty());
        eventTableView.setItems(sorted);
    }

    private Predicate<Event> buildPredicate(String text, String type) {
        return event -> {
            boolean titleMatch = (text == null || text.trim().isEmpty())
                    || event.getTitle().toLowerCase().contains(text.toLowerCase());
            boolean typeMatch  = (type == null || "All Types".equals(type))
                    || event.getEventType().equalsIgnoreCase(type);
            return titleMatch && typeMatch;
        };
    }

    // ── Create Event ──────────────────────────────────────────────────────────

    @FXML
    private void handleCreateEvent() {
        String eventId    = formEventId.getText().trim();
        String title      = formTitle.getText().trim();
        String dateTime   = formDateTime.getText().trim();
        String location   = formLocation.getText().trim();
        String capacityTx = formCapacity.getText().trim();
        String eventType  = formEventType.getValue();
        String specific   = formTypeSpecific.getText().trim();

        if (eventId.isEmpty() || title.isEmpty() || dateTime.isEmpty()
                || location.isEmpty() || capacityTx.isEmpty()
                || eventType == null || specific.isEmpty()) {
            statusLabel.setText("Please fill in all fields including the type-specific field.");
            return;
        }

        int capacity;
        try {
            capacity = Integer.parseInt(capacityTx);
            if (capacity <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            statusLabel.setText("Capacity must be a positive integer.");
            return;
        }

        if (SystemData.getInstance().eventIdExists(eventId)) {
            statusLabel.setText("Event ID already exists!");
            return;
        }

        Event newEvent = switch (eventType) {
            case "Workshop" -> new Workshop(eventId, title, dateTime, location, capacity, "Active", specific);
            case "Seminar"  -> new Seminar (eventId, title, dateTime, location, capacity, "Active", specific);
            case "Concert"  -> new Concert (eventId, title, dateTime, location, capacity, "Active", specific);
            default         -> null;
        };

        if (newEvent != null) {
            SystemData.getInstance().addEvent(newEvent);
            masterEventList.add(newEvent);
            statusLabel.setText("Event " + eventId + " created successfully!");
            clearForm();
        }
    }

    // ── Update Event ──────────────────────────────────────────────────────────

    @FXML
    private void handleUpdateEvent() {
        Event selected = eventTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Select an event from the table to update.");
            return;
        }

        String title      = formTitle.getText().trim();
        String dateTime   = formDateTime.getText().trim();
        String location   = formLocation.getText().trim();
        String capacityTx = formCapacity.getText().trim();
        String specific   = formTypeSpecific.getText().trim();

        if (title.isEmpty() || dateTime.isEmpty() || location.isEmpty()
                || capacityTx.isEmpty() || specific.isEmpty()) {
            statusLabel.setText("Please fill in all fields before updating.");
            return;
        }

        int capacity;
        try {
            capacity = Integer.parseInt(capacityTx);
            if (capacity <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            statusLabel.setText("Capacity must be a positive integer.");
            return;
        }

        // Apply changes to the real model object
        selected.setTitle(title);
        selected.setDateTime(dateTime);
        selected.setLocation(location);
        selected.setCapacity(capacity);

        // Update type-specific field via the concrete subclass
        if (selected instanceof Workshop w) w.setTopic(specific);
        else if (selected instanceof Seminar s) s.setSpeakerName(specific);
        else if (selected instanceof Concert c) c.setAgeRestriction(specific);

        eventTableView.refresh();
        statusLabel.setText("Event " + selected.getEventId() + " updated successfully!");
    }

    // ── Cancel Event (with booking cascade) ───────────────────────────────────

    @FXML
    private void handleCancelEvent() {
        Event selected = eventTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Select an event from the table to cancel.");
            return;
        }
        if ("Cancelled".equals(selected.getStatus())) {
            statusLabel.setText("Event is already cancelled.");
            return;
        }

        // Mark the real event object as Cancelled
        selected.setStatus("Cancelled");

        // Cancel all Confirmed and Waitlisted bookings and clear the waitlist
        SystemData.getInstance().getBookingService()
                .cancelAllBookingsForEvent(selected.getEventId());

        eventTableView.refresh();
        statusLabel.setText("Event " + selected.getEventId()
                + " cancelled. All bookings have been cancelled and the waitlist cleared.");
    }

    // ── View Event Roster ─────────────────────────────────────────────────────

    @FXML
    private void handleViewRoster() {
        Event selected = eventTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            rosterArea.setText("Select an event from the table to view its roster.");
            return;
        }

        String eventId = selected.getEventId();
        List<String> confirmed  = SystemData.getInstance().getBookingService().getConfirmedRoster(eventId);
        List<String> waitlisted = SystemData.getInstance().getBookingService().getWaitlist(eventId);

        StringBuilder sb = new StringBuilder();
        sb.append("Roster for: ").append(selected.getTitle())
                .append(" (").append(eventId).append(")\n");
        sb.append("Status: ").append(selected.getStatus())
                .append("  |  Capacity: ").append(selected.getCapacity()).append("\n\n");

        sb.append("── Confirmed (").append(confirmed.size()).append(") ──────────────\n");
        if (confirmed.isEmpty()) {
            sb.append("  (none)\n");
        } else {
            for (int i = 0; i < confirmed.size(); i++) {
                sb.append("  ").append(i + 1).append(". ").append(confirmed.get(i)).append("\n");
            }
        }

        sb.append("\n── Waitlist (").append(waitlisted.size()).append(") ─────────────\n");
        if (waitlisted.isEmpty()) {
            sb.append("  (none)\n");
        } else {
            for (int i = 0; i < waitlisted.size(); i++) {
                sb.append("  ").append(i + 1).append(". ").append(waitlisted.get(i)).append("\n");
            }
        }

        rosterArea.setText(sb.toString());
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void fillFormFromEvent(Event e) {
        formEventId.setText(e.getEventId());
        formTitle.setText(e.getTitle());
        formDateTime.setText(e.getDateTime());
        formLocation.setText(e.getLocation());
        formCapacity.setText(String.valueOf(e.getCapacity()));
        formEventType.setValue(e.getEventType());
        formTypeSpecific.setText(e.getTypeSpecificField());
        updateTypeLabel(e.getEventType());
    }

    private void updateTypeLabel(String type) {
        if (formTypeSpecificLabel == null || type == null) return;
        formTypeSpecificLabel.setText(switch (type) {
            case "Workshop" -> "Topic:";
            case "Seminar"  -> "Speaker Name:";
            case "Concert"  -> "Age Restriction:";
            default         -> "Type-Specific Field:";
        });
    }

    private void clearForm() {
        formEventId.clear();
        formTitle.clear();
        formDateTime.clear();
        formLocation.clear();
        formCapacity.clear();
        formTypeSpecific.clear();
        formEventType.getSelectionModel().clearSelection();
    }
}
