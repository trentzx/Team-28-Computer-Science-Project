package com.campusbooking;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import service.DataSaver;
import service.SystemData;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Load the main window layout from the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainLayout.fxml"));
            Parent root = loader.load();

            // Kick off SystemData which reads all three CSV files at startup
            // so the app launches with all existing users, events, and bookings already loaded
            SystemData.getInstance();

            // Set up the window size and title
            Scene scene = new Scene(root, 1280, 800);
            primaryStage.setTitle("ENGG*1420 Campus Event Booking System");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(1024);
            primaryStage.setMinHeight(768);
            primaryStage.show();

            // When the user closes the window, save everything back to the CSV files
            // so nothing is lost between sessions
            primaryStage.setOnCloseRequest(e -> {
                DataSaver saver = new DataSaver();
                saver.saveAll();
                System.out.println("System state saved. Goodbye!");
            });

        } catch (IOException e) {
            System.err.println("Error: Cannot load MainLayout.fxml.");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
