package service;

import model.users.*;
import model.events.*;
import java.io.*;
import java.util.*;

/*
 DataLoader is responsible for reading the starter CSV files at application startup
 and constructing the corresponding Java objects to restore the full system state.
 */
public class DataLoader {
    // Lists to store the loaded users and events
    private List<User> users = new ArrayList<>();
    private List<Event> events = new ArrayList<>();

    /*
     Reads users.csv and creates Student, Staff, or Guest objects
     based on the userType field in each row.
     @param filename path to the users.csv file
     */
    public void loadUsers(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            br.readLine(); // skip the header row (userId,name,email,userType)

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 4) continue; // skip malformed rows

                String userId = parts[0].trim();
                String name = parts[1].trim();
                String email = parts[2].trim();
                String type = parts[3].trim();

                // Create the correct subclass based on the userType field
                switch (type) {
                    case "Student" -> users.add(new Student(userId, name, email));
                    case "Staff" -> users.add(new Staff(userId, name, email));
                    case "Guest" -> users.add(new Guest(userId, name, email));
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading users: " + e.getMessage());
        }
    }

    /**
     * Reads events.csv and creates Workshop, Seminar, or Concert objects
     * based on the eventType field in each row.
     * Each event type has one extra type-specific field:
     *   - Workshop: topic (column 8)
     *   - Seminar: speakerName (column 9)
     *   - Concert: ageRestriction (column 10)
     * @param filename path to the events.csv file
     */
    public void loadEvents(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            br.readLine(); // skip the header row

            while ((line = br.readLine()) != null) {
                // Use -1 limit to preserve empty trailing fields
                String[] parts = line.split(",", -1);
                if (parts.length < 7) continue; // skip malformed rows

                // Parse the common fields shared by all event types
                String eventId = parts[0].trim();
                String title = parts[1].trim();
                String dateTime = parts[2].trim();
                String location = parts[3].trim();
                int capacity = Integer.parseInt(parts[4].trim());
                String status = parts[5].trim();
                String eventType = parts[6].trim();

                // Create the correct event subclass and pass the type-specific field
                switch (eventType) {
                    case "Workshop" -> {
                        // Workshop requires a topic field
                        String topic = parts.length > 7 ? parts[7].trim() : "";
                        events.add(new Workshop(eventId, title, dateTime, location, capacity, status, topic));
                    }
                    case "Seminar" -> {
                        // Seminar requires a speakerName field
                        String speakerName = parts.length > 8 ? parts[8].trim() : "";
                        events.add(new Seminar(eventId, title, dateTime, location, capacity, status, speakerName));
                    }
                    case "Concert" -> {
                        // Concert requires an ageRestriction field (informational only, not enforced)
                        String ageRestriction = parts.length > 9 ? parts[9].trim() : "";
                        events.add(new Concert(eventId, title, dateTime, location, capacity, status, ageRestriction));
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading events: " + e.getMessage());
        }
    }

    // Returns the list of loaded users
    public List<User> getUsers() { return users; }

    // Returns the list of loaded events
    public List<Event> getEvents() { return events; }
}