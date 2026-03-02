package com.campusbooking.controllers;

import com.campusbooking.models.Event; // This import will now work correctly
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

public class EventManagementController implements Initializable {

    @FXML private TextField searchTitleField;
    @FXML private ComboBox<String> filterTypeCombo;
    @FXML private TableView<Event> eventTableView;
    @FXML private TableColumn<Event, String> colId;
    @FXML private TableColumn<Event, String> colTitle;
    @FXML private TableColumn<Event, String> colDate;
    @FXML private TableColumn<Event, String> colLocation;
    @FXML private TableColumn<Event, Integer> colCapacity;
    @FXML private TableColumn<Event, String> colStatus;
    @FXML private TableColumn<Event, String> colType;

    private final ObservableList<Event> masterEventRegistry = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configureTableColumns();
        injectInitialDataState();
        initializeSearchAndFilterMechanics();
    }

    private void configureTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("eventId"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("dateTime"));
        colLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        colCapacity.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colType.setCellValueFactory(new PropertyValueFactory<>("eventType"));
    }

    private void injectInitialDataState() {
        filterTypeCombo.getItems().addAll("All Types", "Workshop", "Seminar", "Concert");
        filterTypeCombo.getSelectionModel().selectFirst();

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

    private void initializeSearchAndFilterMechanics() {
        FilteredList<Event> filteredData = new FilteredList<>(masterEventRegistry, event -> true);

        // Warning fixed: Changed statement lambda to expression lambda
        searchTitleField.textProperty().addListener((observable, oldValue, newValue) ->
                filteredData.setPredicate(generateCompoundPredicate(newValue, filterTypeCombo.getValue()))
        );

        // Warning fixed: Changed statement lambda to expression lambda
        filterTypeCombo.valueProperty().addListener((observable, oldValue, newValue) ->
                filteredData.setPredicate(generateCompoundPredicate(searchTitleField.getText(), newValue))
        );

        SortedList<Event> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(eventTableView.comparatorProperty());

        eventTableView.setItems(sortedData);
    }

    private Predicate<Event> generateCompoundPredicate(String textualQuery, String categoryQuery) {
        return event -> {
            boolean textualMatch = true;
            boolean categoryMatch = true;

            if (textualQuery!= null &&!textualQuery.trim().isEmpty()) {
                if (!event.getTitle().toLowerCase().contains(textualQuery.toLowerCase())) {
                    textualMatch = false;
                }
            }

            if (categoryQuery!= null &&!categoryQuery.equals("All Types")) {
                if (!event.getEventType().equalsIgnoreCase(categoryQuery)) {
                    categoryMatch = false;
                }
            }

            return textualMatch && categoryMatch;
        };
    }

    @FXML
    private void handleCancelEventAction() {
        Event selectedEvent = eventTableView.getSelectionModel().getSelectedItem();
        if (selectedEvent!= null) {
            selectedEvent.setStatus("Cancelled");
            eventTableView.refresh();
        }
    }
}