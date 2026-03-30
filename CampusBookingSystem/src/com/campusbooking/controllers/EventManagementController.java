package com.campusbooking.controllers;

import model.events.*;

import com.campusbooking.models.Event;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Predicate;

/*
 Controller for the Event Management panel.
 Handles displaying all events, searching by title,
 filtering by type, and cancelling events.
 */
public class EventManagementController implements Initializable {

    // Search bar for filtering events by title
    @FXML private TextField searchTitleField;

    // Dropdown for filtering events by type (Workshop/Seminar/Concert)
    @FXML private ComboBox<String> filterTypeCombo;

    // Table to display all events
    @FXML private TableView<Event> eventTableView;
    @FXML private TableColumn<Event, String> colId;
    @FXML private TableColumn<Event, String> colTitle;
    @FXML private TableColumn<Event, String> colDate;
    @FXML private TableColumn<Event, String> colLocation;
    @FXML private TableColumn<Event, Integer> colCapacity;
    @FXML private TableColumn<Event, String> colStatus;
    @FXML private TableColumn<Event, String> colType;

    // Master list of all events - filtering is applied on top of this
    private final ObservableList<Event> masterEventRegistry = FXCollections.observableArrayList();

    /*
     Called automatically when the panel loads.
     Sets up columns, loads events from CSV, and initializes search/filter.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configureTableColumns();
        injectInitialDataState();
        initializeSearchAndFilterMechanics();
    }

    /*
     Links each table column to the corresponding field in the Event class
     using PropertyValueFactory which calls the getter methods automatically.
     */
    private void configureTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("eventId"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("dateTime"));
        colLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        colCapacity.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colType.setCellValueFactory(new PropertyValueFactory<>("eventType"));
    }

    /*
     Loads events from SystemData which reads from events.csv at startup.
     Converts model.events.Event objects to com.campusbooking.models.Event
     objects for display in the TableView.
     Also populates the filter dropdown with event types.
     */
    private void injectInitialDataState() {
        filterTypeCombo.getItems().addAll("All Types", "Workshop", "Seminar", "Concert");
        filterTypeCombo.getSelectionModel().selectFirst();

        // Load events from CSV via SystemData and add to the master registry
        for (model.events.Event e : service.SystemData.getInstance().getEvents()) {
            masterEventRegistry.add(new Event(
                    e.getEventId(),
                    e.getTitle(),
                    e.getDateTime(),
                    e.getLocation(),
                    e.getCapacity(),
                    e.getStatus(),
                    e.getEventType()
            ));
        }
    }

    /*
     Sets up live search and filter functionality.
     Uses JavaFX FilteredList which automatically updates the table
     whenever the search text or filter type changes.
     SortedList is wrapped around FilteredList to allow column sorting.
     */
    private void initializeSearchAndFilterMechanics() {
        // FilteredList wraps the master list and applies a predicate to show/hide rows
        FilteredList<Event> filteredData = new FilteredList<>(masterEventRegistry, event -> true);

        // Update filter whenever the search text changes
        searchTitleField.textProperty().addListener((observable, oldValue, newValue) ->
                filteredData.setPredicate(generateCompoundPredicate(newValue, filterTypeCombo.getValue()))
        );

        // Update filter whenever the type dropdown changes
        filterTypeCombo.valueProperty().addListener((observable, oldValue, newValue) ->
                filteredData.setPredicate(generateCompoundPredicate(searchTitleField.getText(), newValue))
        );

        // SortedList allows the table columns to be clicked to sort
        SortedList<Event> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(eventTableView.comparatorProperty());

        eventTableView.setItems(sortedData);
    }

    /*
     Generates a combined predicate that filters events by both
     title (partial, case-insensitive match) and event type.
     Both conditions must be true for an event to be shown.

     @param textualQuery  the search text entered by the user
     @param categoryQuery the selected event type filter
     */
    private Predicate<Event> generateCompoundPredicate(String textualQuery, String categoryQuery) {
        return event -> {
            boolean textualMatch = true;
            boolean categoryMatch = true;

            // Check if title contains the search text (case-insensitive)
            if (textualQuery != null && !textualQuery.trim().isEmpty()) {
                if (!event.getTitle().toLowerCase().contains(textualQuery.toLowerCase())) {
                    textualMatch = false;
                }
            }

            // Check if event type matches the selected filter
            if (categoryQuery != null && !categoryQuery.equals("All Types")) {
                if (!event.getEventType().equalsIgnoreCase(categoryQuery)) {
                    categoryMatch = false;
                }
            }

            return textualMatch && categoryMatch;
        };
    }

    /*
     Handles the Cancel Event button click.
     Gets the selected event from the table and sets its status to Cancelled.
     The table refreshes automatically to reflect the change.
     Per the spec, cancelled events remain visible but cannot be booked.
     */
    @FXML
    private void handleCancelEventAction() {
        Event selectedEvent = eventTableView.getSelectionModel().getSelectedItem();
        if (selectedEvent != null) {
            selectedEvent.setStatus("Cancelled");
            eventTableView.refresh();
        }
    }
}