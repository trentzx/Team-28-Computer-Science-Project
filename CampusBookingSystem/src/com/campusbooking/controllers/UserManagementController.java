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

public class UserManagementController implements Initializable {

    @FXML private TextField userIdField;
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private ComboBox<String> userTypeCombo;
    @FXML private TableView<User> userTableView;
    @FXML private TableColumn<User, String> colUserId;
    @FXML private TableColumn<User, String> colName;
    @FXML private TableColumn<User, String> colEmail;
    @FXML private TableColumn<User, String> colType;
    @FXML private Label statusLabel;

    private final ObservableList<User> userList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configureTableColumns();
        userTypeCombo.getItems().addAll("Student", "Staff", "Guest");
        loadInitialUsers();
        userTableView.setItems(userList);
    }

    private void configureTableColumns() {
        colUserId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colType.setCellValueFactory(new PropertyValueFactory<>("userType"));
    }

    private void loadInitialUsers() {
        userList.addAll(service.SystemData.getInstance().getUsers());
    }

    @FXML
    private void handleAddUser() {
        String userId = userIdField.getText().trim();
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String type = userTypeCombo.getValue();

        if (userId.isEmpty() || name.isEmpty() || email.isEmpty() || type == null) {
            statusLabel.setText("Please fill in all fields.");
            return;
        }

        for (User u : userList) {
            if (u.getUserId().equals(userId)) {
                statusLabel.setText("User ID already exists!");
                return;
            }
        }

        User newUser = switch (type) {
            case "Student" -> new Student(userId, name, email);
            case "Staff" -> new Staff(userId, name, email);
            case "Guest" -> new Guest(userId, name, email);
            default -> null;
        };

        if (newUser != null) {
            service.SystemData.getInstance().addUser(newUser);
            userList.add(newUser);
            statusLabel.setText("User " + userId + " added successfully!");
            userIdField.clear();
            nameField.clear();
            emailField.clear();
            userTypeCombo.getSelectionModel().clearSelection();
        }
    }
}