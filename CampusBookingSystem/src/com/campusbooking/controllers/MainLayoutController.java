package com.campusbooking.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.HashMap;
import java.util.Map;

public class MainLayoutController implements Initializable {

    @FXML
    private BorderPane mainBorderPane;

    // We save loaded screens here so we don't have to load them twice
    private Map<String, Node> viewCache = new HashMap<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Load the Event screen first when the app opens
        loadDynamicSubView("/fxml/EventManagement.fxml");
    }

    @FXML
    private void handleUserManagementNavigation(ActionEvent event) {
        loadDynamicSubView("/fxml/UserManagement.fxml");
    }

    @FXML
    private void handleEventManagementNavigation(ActionEvent event) {
        loadDynamicSubView("/fxml/EventManagement.fxml");
    }

    @FXML
    private void handleBookingManagementNavigation(ActionEvent event) {
        loadDynamicSubView("/fxml/BookingManagement.fxml");
    }

    @FXML
    private void handleWaitlistManagementNavigation(ActionEvent event) {
        loadDynamicSubView("/fxml/WaitlistManagement.fxml");
    }

    private void loadDynamicSubView(String fxmlResourcePath) {
        try {
            if (!viewCache.containsKey(fxmlResourcePath)) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlResourcePath));
                Node viewNode = loader.load();
                viewCache.put(fxmlResourcePath, viewNode);
            }
            // Swap the center part of the screen
            mainBorderPane.setCenter(viewCache.get(fxmlResourcePath));
        } catch (IOException e) {
            System.err.println("Error loading screen: " + fxmlResourcePath);
            e.printStackTrace();
        }
    }
}
