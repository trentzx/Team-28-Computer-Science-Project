package com.campusbooking.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.bookings.Booking;
import model.events.Event;
import service.SystemData;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller for the Waitlist Management panel.
 * Provides dedicated visibility into waitlists and supports
 * cancelling waitlisted bookings while preserving order.
 */
public class WaitlistManagementController implements Initializable {

    // ── View Waitlist form ────────────────────────────────────────────────────
    @FXML private TextField waitlistEventIdField;

    // ── Cancel waitlisted booking form ────────────────────────────────────────
    @FXML private TextField cancelWaitlistBookingIdField;

    // ── Waitlist table ────────────────────────────────────────────────────────
    @FXML private TableView<WaitlistRow>          waitlistTable;
    @FXML private TableColumn<WaitlistRow,Integer> colPosition;
    @FXML private TableColumn<WaitlistRow,String>  colUserId;
    @FXML private TableColumn<WaitlistRow,String>  colBookingId;
    @FXML private TableColumn<WaitlistRow,String>  colTimestamp;

    // ── Status / promotion notice ─────────────────────────────────────────────
    @FXML private Label statusLabel;

    private final ObservableList<WaitlistRow> waitlistRows = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colPosition .setCellValueFactory(new PropertyValueFactory<>("position"));
        colUserId   .setCellValueFactory(new PropertyValueFactory<>("userId"));
        colBookingId.setCellValueFactory(new PropertyValueFactory<>("bookingId"));
        colTimestamp.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        waitlistTable.setItems(waitlistRows);
    }

    // ── View Waitlist ─────────────────────────────────────────────────────────

    @FXML
    private void handleViewWaitlist() {
        String eventId = waitlistEventIdField.getText().trim();
        if (eventId.isEmpty()) {
            statusLabel.setText("Enter an Event ID.");
            return;
        }

        Event event = SystemData.getInstance().findEventById(eventId);
        if (event == null) {
            statusLabel.setText("No event found with ID: " + eventId);
            return;
        }

        List<String> waitedUserIds =
                SystemData.getInstance().getBookingService().getWaitlist(eventId);
        List<Booking> allBookings  =
                SystemData.getInstance().getBookingService().getAllBookings();

        waitlistRows.clear();
        int position = 1;
        for (String userId : waitedUserIds) {
            // Find the corresponding booking for timestamp
            for (Booking b : allBookings) {
                if (b.getUserId().equals(userId) && b.getEventId().equals(eventId)
                        && "Waitlisted".equals(b.getBookingStatus())) {
                    waitlistRows.add(new WaitlistRow(
                            position++,
                            userId,
                            b.getBookingId(),
                            b.getCreatedAt().toString()
                    ));
                    break;
                }
            }
        }

        if (waitlistRows.isEmpty()) {
            statusLabel.setText("No waitlisted users for event: " + eventId);
        } else {
            statusLabel.setText("Waitlist for " + event.getTitle()
                    + " (" + waitlistRows.size() + " user(s))");
        }
    }

    // ── Remove (cancel) a waitlisted booking ──────────────────────────────────

    @FXML
    private void handleCancelWaitlistedBooking() {
        String bookingId = cancelWaitlistBookingIdField.getText().trim();
        if (bookingId.isEmpty()) {
            statusLabel.setText("Enter a Booking ID to cancel.");
            return;
        }

        // Verify it is actually Waitlisted before cancelling
        boolean found = false;
        for (Booking b : SystemData.getInstance().getBookingService().getAllBookings()) {
            if (b.getBookingId().equals(bookingId)) {
                if (!"Waitlisted".equals(b.getBookingStatus())) {
                    statusLabel.setText("Booking " + bookingId + " is not Waitlisted (status: "
                            + b.getBookingStatus() + ").");
                    return;
                }
                found = true;
                break;
            }
        }
        if (!found) {
            statusLabel.setText("Booking not found: " + bookingId);
            return;
        }

        String result = SystemData.getInstance().getBookingService().cancelBooking(bookingId);
        statusLabel.setText(result);
        cancelWaitlistBookingIdField.clear();

        // Refresh the displayed waitlist if one is showing
        String eventId = waitlistEventIdField.getText().trim();
        if (!eventId.isEmpty()) handleViewWaitlist();
    }

    // ── Inner model class for the TableView ───────────────────────────────────

    /**
     * Simple data container for one row in the waitlist table.
     * JavaFX PropertyValueFactory needs public getters.
     */
    public static class WaitlistRow {
        private final int    position;
        private final String userId;
        private final String bookingId;
        private final String timestamp;

        public WaitlistRow(int position, String userId, String bookingId, String timestamp) {
            this.position  = position;
            this.userId    = userId;
            this.bookingId = bookingId;
            this.timestamp = timestamp;
        }

        public int    getPosition()  { return position; }
        public String getUserId()    { return userId; }
        public String getBookingId() { return bookingId; }
        public String getTimestamp() { return timestamp; }
    }
}
