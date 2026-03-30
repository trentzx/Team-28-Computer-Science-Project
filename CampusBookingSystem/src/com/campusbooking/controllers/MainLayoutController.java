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

/*
 Controller for the main application layout.
 Manages navigation between the four main modules:
 User Management, Event Management, Booking Management, and Waitlist Management.
 Uses a BorderPane layout where the center panel swaps based on which
 navigation button is clicked.
 */
public class MainLayoutController implements Initializable {

    // The main layout container - left panel has nav buttons, center swaps between modules
    @FXML private BorderPane mainBorderPane;

    /*
     Cache to store already-loaded panels so we don't reload them every time
     the user navigates back to a panel they already visited.
     Key = FXML file path, Value = the loaded panel node
     */
    private Map<String, Node> viewCache = new HashMap<>();

    /*
     Called automatically when the application launches.
     Loads the Event Management panel as the default starting view.
     */
    @Override public void initialize(URL location, ResourceBundle resources) {
        // Load the Event screen first when the app opens
        loadDynamicSubView("/fxml/EventManagement.fxml");
    }

    /*
     Handles the User Administration navigation button click.
     Loads the User Management panel into the center of the layout.
     */
    @FXML private void handleUserManagementNavigation(ActionEvent event) {
        loadDynamicSubView("/fxml/UserManagement.fxml");
    }

    /*
     Handles the Event Management navigation button click.
     Loads the Event Management panel into the center of the layout.
     */
    @FXML private void handleEventManagementNavigation(ActionEvent event) {
        loadDynamicSubView("/fxml/EventManagement.fxml");
    }

    /*
     Handles the Booking Operations navigation button click.
     Loads the Booking Management panel into the center of the layout.
     */
    @FXML private void handleBookingManagementNavigation(ActionEvent event) {
        loadDynamicSubView("/fxml/BookingManagement.fxml");
    }

    /*
     Handles the Waitlist Integrity navigation button click.
     Loads the Waitlist Management panel into the center of the layout.
     */
    @FXML
    private void handleWaitlistManagementNavigation(ActionEvent event) {
        loadDynamicSubView("/fxml/WaitlistManagement.fxml");
    }

    /*
     Loads an FXML file and displays it in the center of the main layout.
     Uses a cache so each panel is only loaded once - subsequent visits
     reuse the already-loaded panel which preserves its state.
     @param fxmlResourcePath the path to the FXML file to load
     */
    private void loadDynamicSubView(String fxmlResourcePath) {
        try {
            // Only load the FXML file if it hasn't been loaded before
            if (!viewCache.containsKey(fxmlResourcePath)) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlResourcePath));
                Node viewNode = loader.load();
                // Store in cache for future navigation
                viewCache.put(fxmlResourcePath, viewNode);
            }
            // Swap the center panel to show the requested module
            mainBorderPane.setCenter(viewCache.get(fxmlResourcePath));
        } catch (IOException e) {
            System.err.println("Error loading screen: " + fxmlResourcePath);
            e.printStackTrace();
        }
    }
}