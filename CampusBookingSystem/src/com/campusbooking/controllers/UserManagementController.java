package com.campusbooking.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.users.*;

import java.net.URL;
import java.util.ResourceBundle;

/*
 Controller for the User Management panel.
 Handles displaying all users and adding new users to the system.
 */
public class UserManagementController implements Initializable {

    // Input fields for adding a new user
    @FXML private TextField userIdField;   // User ID input
    @FXML private TextField nameField;     // Name input
    @FXML private TextField emailField;    // Email input
    @FXML private ComboBox<String> userTypeCombo; // Dropdown for Student/Staff/Guest

    // Table to display all registered users
    @FXML private TableView<User> userTableView;
    @FXML private TableColumn<User, String> colUserId;
    @FXML private TableColumn<User, String> colName;
    @FXML private TableColumn<User, String> colEmail;
    @FXML private TableColumn<User, String> colType;

    // Label to display success/error messages
    @FXML private Label statusLabel;

    // Observable list that the TableView watches for changes
    private final ObservableList<User> userList = FXCollections.observableArrayList();

    /*
     Called automatically when the panel loads.
     Sets up table columns, populates the type dropdown,
     loads existing users from CSV, and connects the list to the table.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configureTableColumns();
        userTypeCombo.getItems().addAll("Student", "Staff", "Guest");
        loadInitialUsers();
        userTableView.setItems(userList);
    }

    /*
     Links each table column to the corresponding field in the User class
     using PropertyValueFactory which calls the getter methods automatically.
     For example colType uses getUserType() from the User class.
     */
    private void configureTableColumns() {
        colUserId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colType.setCellValueFactory(new PropertyValueFactory<>("userType"));
    }

    /*
     Loads the initial users from SystemData which reads from users.csv at startup.
     This ensures the table is pre-populated when the panel first opens.
     */
    private void loadInitialUsers() {
        userList.addAll(service.SystemData.getInstance().getUsers());
    }

    /*
     Handles the Add User button click.
     Validates all input fields, checks for duplicate user IDs,
     creates the correct user subclass, and adds to the system.
     Booking limits are enforced automatically by user type:
     - Student: max 3 confirmed bookings
     - Staff: max 5 confirmed bookings
     - Guest: max 1 confirmed booking
     */
    @FXML
    private void handleAddUser() {
        String userId = userIdField.getText().trim();
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String type = userTypeCombo.getValue();

        // Validate that all fields are filled in
        if (userId.isEmpty() || name.isEmpty() || email.isEmpty() || type == null) {
            statusLabel.setText("Please fill in all fields.");
            return;
        }

        // Check for duplicate user ID
        for (User u : userList) {
            if (u.getUserId().equals(userId)) {
                statusLabel.setText("User ID already exists!");
                return;
            }
        }

        // Create the correct subclass based on selected type
        User newUser = switch (type) {
            case "Student" -> new Student(userId, name, email);
            case "Staff" -> new Staff(userId, name, email);
            case "Guest" -> new Guest(userId, name, email);
            default -> null;
        };

        if (newUser != null) {
            // Add to SystemData so other panels can access the new user
            service.SystemData.getInstance().addUser(newUser);
            // Add to the observable list so the table updates immediately
            userList.add(newUser);
            statusLabel.setText("User " + userId + " added successfully!");

            // Clear all input fields after successful add
            userIdField.clear();
            nameField.clear();
            emailField.clear();
            userTypeCombo.getSelectionModel().clearSelection();
        }
    }
}