package com.campusbooking.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.bookings.Booking;
import model.events.Event;
import model.users.User;
import service.SystemData;

import java.net.URL;
import java.util.ResourceBundle;

/*
 Controller for the Booking Management panel.
 Handles creating new bookings and cancelling existing ones.
 Enforces all booking rules via BookingService.
 */
public class BookingManagementController implements Initializable {

    // Input fields for booking an event
    @FXML private TextField bookingUserIdField;  // User ID input
    @FXML private TextField bookingEventIdField; // Event ID input

    // Input field for cancelling a booking
    @FXML private TextField cancelBookingIdField;

    // Label to display success/error messages to the user
    @FXML private Label statusLabel;

    // Table to display all bookings and their statuses
    @FXML private TableView<Booking> bookingTable;
    @FXML private TableColumn<Booking, String> colBookingId;
    @FXML private TableColumn<Booking, String> colUserId;
    @FXML private TableColumn<Booking, String> colEventId;
    @FXML private TableColumn<Booking, String> colStatus;

    // Observable list that the TableView watches for changes
    private final ObservableList<Booking> bookingList = FXCollections.observableArrayList();

    /*
     Called automatically when the panel loads.
     Sets up the table columns and connects the booking list to the table.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configureTableColumns();
        bookingTable.setItems(bookingList);
    }

    /*
     Links each table column to the corresponding field in the Booking class
     using PropertyValueFactory which calls the getter methods automatically.
     */
    private void configureTableColumns() {
        colBookingId.setCellValueFactory(new PropertyValueFactory<>("bookingId"));
        colUserId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        colEventId.setCellValueFactory(new PropertyValueFactory<>("eventId"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("bookingStatus"));
    }

    /*
     Handles the Book Event button click.
     Validates inputs, looks up the user and event, then calls
     BookingService.bookEvent() which enforces all booking rules:
     - No duplicate bookings
     - Booking limits by user type (Student 3, Staff 5, Guest 1)
     - Confirmed if capacity available, Waitlisted if event is full
     */
    @FXML
    private void handleBookEvent() {
        String userId = bookingUserIdField.getText().trim();
        String eventId = bookingEventIdField.getText().trim();

        // Validate that both fields are filled in
        if (userId.isEmpty() || eventId.isEmpty()) {
            statusLabel.setText("Please enter both User ID and Event ID.");
            return;
        }

        // Look up the user and event in the system
        User user = SystemData.getInstance().findUserById(userId);
        Event event = SystemData.getInstance().findEventById(eventId);

        // Show error if user or event doesn't exist
        if (user == null) {
            statusLabel.setText("User not found: " + userId);
            return;
        }
        if (event == null) {
            statusLabel.setText("Event not found: " + eventId);
            return;
        }

        // Attempt to book the event - BookingService handles all the rules
        String result = SystemData.getInstance().getBookingService().bookEvent(user, event);
        statusLabel.setText(result);

        // Refresh the table to show the new booking
        bookingList.setAll(SystemData.getInstance().getBookingService().getAllBookings());
        bookingUserIdField.clear();
        bookingEventIdField.clear();
    }

    /*
     Handles the Cancel Booking button click.
     Calls BookingService.cancelBooking() which:
     - Marks the booking as Cancelled
     - If it was Confirmed, automatically promotes the first waitlisted user
     */
    @FXML
    private void handleCancelBooking() {
        String bookingId = cancelBookingIdField.getText().trim();

        // Validate that a booking ID was entered
        if (bookingId.isEmpty()) {
            statusLabel.setText("Please enter a Booking ID to cancel.");
            return;
        }

        // Cancel the booking - BookingService handles waitlist promotion
        String result = SystemData.getInstance().getBookingService().cancelBooking(bookingId);
        statusLabel.setText(result);

        // Refresh the table to reflect the cancellation
        bookingList.setAll(SystemData.getInstance().getBookingService().getAllBookings());
        cancelBookingIdField.clear();
    }
}