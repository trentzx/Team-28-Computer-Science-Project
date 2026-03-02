
package com.campusbooking;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Load the main layout file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainLayout.fxml"));
            Parent root = loader.load();

            // Set up the window size
            Scene scene = new Scene(root, 1280, 800);

            primaryStage.setTitle("ENGG*1420 Campus Event Booking System");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(1024);
            primaryStage.setMinHeight(768);
            primaryStage.show();

            // Load the starter CSV files
            initializeSystemPersistence();

        } catch (IOException e) {
            System.err.println("Error: Cannot load MainLayout.fxml.");
            e.printStackTrace();
        }
    }

    private void initializeSystemPersistence() {
        // Code to read users.csv, events.csv, and bookings.csv goes here.
    }

    public static void main(String args) {
        launch(args);
    }
}