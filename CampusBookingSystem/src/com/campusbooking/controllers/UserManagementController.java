package com.campusbooking.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.bookings.Booking;
import model.users.*;
import service.SystemData;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller for the User Management panel.
 * Supports: Add User, List All Users, View User Details (with booking summary).
 */
public class UserManagementController implements Initializable {

    // ── Add-User form ─────────────────────────────────────────────────────────
    @FXML private TextField       userIdField;
    @FXML private TextField       nameField;
    @FXML private TextField       emailField;
    @FXML private ComboBox<String> userTypeCombo;

    // ── User table ────────────────────────────────────────────────────────────
    @FXML private TableView<User>          userTableView;
    @FXML private TableColumn<User,String> colUserId;
    @FXML private TableColumn<User,String> colName;
    @FXML private TableColumn<User,String> colEmail;
    @FXML private TableColumn<User,String> colType;

    // ── View-User-Details area ────────────────────────────────────────────────
    @FXML private TextField viewUserIdField;   // user types an ID to look up
    @FXML private Label     detailsLabel;      // shows the user info + booking summary

    // ── Status message ────────────────────────────────────────────────────────
    @FXML private Label statusLabel;

    private final ObservableList<User> userList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configureTableColumns();
        userTypeCombo.getItems().addAll("Student", "Staff", "Guest");
        userList.addAll(SystemData.getInstance().getUsers());
        userTableView.setItems(userList);

        // Clicking a row in the table auto-fills the View Details field
        userTableView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null && viewUserIdField != null) {
                        viewUserIdField.setText(newVal.getUserId());
                    }
                }
        );
    }

    private void configureTableColumns() {
        colUserId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        colName  .setCellValueFactory(new PropertyValueFactory<>("name"));
        colEmail .setCellValueFactory(new PropertyValueFactory<>("email"));
        colType  .setCellValueFactory(new PropertyValueFactory<>("userType"));
    }

    // ── Add User ──────────────────────────────────────────────────────────────

    @FXML
    private void handleAddUser() {
        String userId = userIdField.getText().trim();
        String name   = nameField.getText().trim();
        String email  = emailField.getText().trim();
        String type   = userTypeCombo.getValue();

        if (userId.isEmpty() || name.isEmpty() || email.isEmpty() || type == null) {
            statusLabel.setText("Please fill in all fields.");
            return;
        }

        if (SystemData.getInstance().userIdExists(userId)) {
            statusLabel.setText("User ID already exists!");
            return;
        }

        User newUser = switch (type) {
            case "Student" -> new Student(userId, name, email);
            case "Staff"   -> new Staff(userId, name, email);
            case "Guest"   -> new Guest(userId, name, email);
            default        -> null;
        };

        if (newUser != null) {
            SystemData.getInstance().addUser(newUser);
            userList.add(newUser);
            statusLabel.setText("User " + userId + " added successfully!");
            userIdField.clear();
            nameField.clear();
            emailField.clear();
            userTypeCombo.getSelectionModel().clearSelection();
        }
    }

    // ── View User Details ─────────────────────────────────────────────────────

    @FXML
    private void handleViewUserDetails() {
        String lookupId = viewUserIdField.getText().trim();
        if (lookupId.isEmpty()) {
            detailsLabel.setText("Enter a User ID or select a user from the table.");
            return;
        }

        User user = SystemData.getInstance().findUserById(lookupId);
        if (user == null) {
            detailsLabel.setText("No user found with ID: " + lookupId);
            return;
        }

        // Build booking summary
        List<Booking> userBookings =
                SystemData.getInstance().getBookingService().getBookingsForUser(lookupId);

        long confirmed  = userBookings.stream().filter(b -> "Confirmed" .equals(b.getBookingStatus())).count();
        long waitlisted = userBookings.stream().filter(b -> "Waitlisted".equals(b.getBookingStatus())).count();
        long cancelled  = userBookings.stream().filter(b -> "Cancelled" .equals(b.getBookingStatus())).count();

        StringBuilder sb = new StringBuilder();
        sb.append("User ID : ").append(user.getUserId()).append("\n");
        sb.append("Name    : ").append(user.getName()).append("\n");
        sb.append("Email   : ").append(user.getEmail()).append("\n");
        sb.append("Type    : ").append(user.getUserType())
                .append("  (max ").append(user.getMaxBookings()).append(" confirmed bookings)\n\n");
        sb.append("── Booking Summary ──────────────────\n");
        sb.append("Confirmed  : ").append(confirmed).append("\n");
        sb.append("Waitlisted : ").append(waitlisted).append("\n");
        sb.append("Cancelled  : ").append(cancelled).append("\n\n");

        if (!userBookings.isEmpty()) {
            sb.append("── Booking List ─────────────────────\n");
            for (Booking b : userBookings) {
                sb.append(b.getBookingId())
                        .append("  Event: ").append(b.getEventId())
                        .append("  Status: ").append(b.getBookingStatus())
                        .append("\n");
            }
        }

        detailsLabel.setText(sb.toString());
    }
}
