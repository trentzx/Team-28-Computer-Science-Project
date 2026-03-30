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

/**
 * Controller for the Booking Management panel.
 * Handles creating and cancelling bookings, enforcing all booking rules,
 * and displaying the full booking list (including bookings loaded from CSV).
 */
public class BookingManagementController implements Initializable {

    // ── Book-Event form ───────────────────────────────────────────────────────
    @FXML private TextField bookingUserIdField;
    @FXML private TextField bookingEventIdField;

    // ── Cancel-Booking form ───────────────────────────────────────────────────
    @FXML private TextField cancelBookingIdField;

    // ── Status message ────────────────────────────────────────────────────────
    @FXML private Label statusLabel;

    // ── Booking table ─────────────────────────────────────────────────────────
    @FXML private TableView<Booking>          bookingTable;
    @FXML private TableColumn<Booking,String> colBookingId;
    @FXML private TableColumn<Booking,String> colUserId;
    @FXML private TableColumn<Booking,String> colEventId;
    @FXML private TableColumn<Booking,String> colStatus;

    private final ObservableList<Booking> bookingList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configureTableColumns();
        bookingTable.setItems(bookingList);
        // Pre-populate with any bookings already in the system (loaded from CSV)
        refreshTable();
    }

    private void configureTableColumns() {
        colBookingId.setCellValueFactory(new PropertyValueFactory<>("bookingId"));
        colUserId   .setCellValueFactory(new PropertyValueFactory<>("userId"));
        colEventId  .setCellValueFactory(new PropertyValueFactory<>("eventId"));
        colStatus   .setCellValueFactory(new PropertyValueFactory<>("bookingStatus"));
    }

    // ── Book Event ────────────────────────────────────────────────────────────

    @FXML
    private void handleBookEvent() {
        String userId  = bookingUserIdField.getText().trim();
        String eventId = bookingEventIdField.getText().trim();

        if (userId.isEmpty() || eventId.isEmpty()) {
            statusLabel.setText("Please enter both User ID and Event ID.");
            return;
        }

        User  user  = SystemData.getInstance().findUserById(userId);
        Event event = SystemData.getInstance().findEventById(eventId);

        if (user  == null) { statusLabel.setText("User not found: "  + userId);  return; }
        if (event == null) { statusLabel.setText("Event not found: " + eventId); return; }

        String result = SystemData.getInstance().getBookingService().bookEvent(user, event);
        statusLabel.setText(result);
        refreshTable();
        bookingUserIdField.clear();
        bookingEventIdField.clear();
    }

    // ── Cancel Booking ────────────────────────────────────────────────────────

    @FXML
    private void handleCancelBooking() {
        String bookingId = cancelBookingIdField.getText().trim();

        if (bookingId.isEmpty()) {
            statusLabel.setText("Please enter a Booking ID to cancel.");
            return;
        }

        String result = SystemData.getInstance().getBookingService().cancelBooking(bookingId);
        statusLabel.setText(result);
        refreshTable();
        cancelBookingIdField.clear();
    }

    // ── View a user's bookings ────────────────────────────────────────────────

    @FXML
    private void handleViewUserBookings() {
        String userId = bookingUserIdField.getText().trim();
        if (userId.isEmpty()) {
            statusLabel.setText("Enter a User ID and press View Bookings.");
            return;
        }
        bookingList.setAll(
                SystemData.getInstance().getBookingService().getBookingsForUser(userId)
        );
        statusLabel.setText("Showing bookings for user: " + userId);
    }

    // ── Show all bookings ─────────────────────────────────────────────────────

    @FXML
    private void handleShowAllBookings() {
        refreshTable();
        statusLabel.setText("Showing all bookings.");
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void refreshTable() {
        bookingList.setAll(SystemData.getInstance().getBookingService().getAllBookings());
    }
}
