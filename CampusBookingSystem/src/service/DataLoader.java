package service;

import model.users.*;
import model.events.*;
import java.io.*;
import java.util.*;

public class DataLoader {
    private List<User> users = new ArrayList<>();
    private List<Event> events = new ArrayList<>();

    public void loadUsers(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 4) continue;
                String userId = parts[0].trim();
                String name = parts[1].trim();
                String email = parts[2].trim();
                String type = parts[3].trim();

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

    public void loadEvents(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length < 7) continue;
                String eventId = parts[0].trim();
                String title = parts[1].trim();
                String dateTime = parts[2].trim();
                String location = parts[3].trim();
                int capacity = Integer.parseInt(parts[4].trim());
                String status = parts[5].trim();
                String eventType = parts[6].trim();

                switch (eventType) {
                    case "Workshop" -> {
                        String topic = parts.length > 7 ? parts[7].trim() : "";
                        events.add(new Workshop(eventId, title, dateTime, location, capacity, status, topic));
                    }
                    case "Seminar" -> {
                        String speakerName = parts.length > 8 ? parts[8].trim() : "";
                        events.add(new Seminar(eventId, title, dateTime, location, capacity, status, speakerName));
                    }
                    case "Concert" -> {
                        String ageRestriction = parts.length > 9 ? parts[9].trim() : "";
                        events.add(new Concert(eventId, title, dateTime, location, capacity, status, ageRestriction));
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading events: " + e.getMessage());
        }
    }

    public List<User> getUsers() { return users; }
    public List<Event> getEvents() { return events; }
}