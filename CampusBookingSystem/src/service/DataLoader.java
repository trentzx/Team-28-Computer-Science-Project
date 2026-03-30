package service;

import model.bookings.Booking;
import model.events.*;
import model.users.*;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * DataLoader reads the three starter CSV files at application startup
 * and constructs the corresponding Java objects to restore the full system state.
 */
public class DataLoader {

    private List<User>    users    = new ArrayList<>();
    private List<Event>   events   = new ArrayList<>();
    private List<Booking> bookings = new ArrayList<>();

    private static final DateTimeFormatter DT_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    // ── Users

    /*
      Reads users.csv and creates Student, Staff, or Guest objects.
      Header: userId,name,email,userType
     */
    public void loadUsers(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",", -1);
                if (p.length < 4) continue;

                String userId = p[0].trim();
                String name   = p[1].trim();
                String email  = p[2].trim();
                String type   = p[3].trim();

                switch (type) {
                    case "Student" -> users.add(new Student(userId, name, email));
                    case "Staff"   -> users.add(new Staff(userId, name, email));
                    case "Guest"   -> users.add(new Guest(userId, name, email));
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading users: " + e.getMessage());
        }
    }

    // ── Events ───────────────────────────────────────────────────────────────

    /**
     * Reads events.csv and creates Workshop, Seminar, or Concert objects.
     * Header: eventId,title,dateTime,location,capacity,status,eventType,topic,speakerName,ageRestriction
     */
    public void loadEvents(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",", -1);
                if (p.length < 7) continue;

                String eventId   = p[0].trim();
                String title     = p[1].trim();
                String dateTime  = p[2].trim();
                String location  = p[3].trim();
                int    capacity  = Integer.parseInt(p[4].trim());
                String status    = p[5].trim();
                String eventType = p[6].trim();

                switch (eventType) {
                    case "Workshop" -> {
                        String topic = p.length > 7 ? p[7].trim() : "";
                        events.add(new Workshop(eventId, title, dateTime, location, capacity, status, topic));
                    }
                    case "Seminar" -> {
                        String speaker = p.length > 8 ? p[8].trim() : "";
                        events.add(new Seminar(eventId, title, dateTime, location, capacity, status, speaker));
                    }
                    case "Concert" -> {
                        String ageRestriction = p.length > 9 ? p[9].trim() : "";
                        events.add(new Concert(eventId, title, dateTime, location, capacity, status, ageRestriction));
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading events: " + e.getMessage());
        }
    }

    // ── Bookings ─────────────────────────────────────────────────────────────

    /**
     * Reads bookings.csv and creates Booking objects.
     * Header: bookingId,userId,eventId,createdAt,bookingStatus
     * Waitlist order is determined by createdAt (earliest first).
     */
    public void loadBookings(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",", -1);
                if (p.length < 5) continue;

                String bookingId    = p[0].trim();
                String userId       = p[1].trim();
                String eventId      = p[2].trim();
                LocalDateTime createdAt = LocalDateTime.parse(p[3].trim(), DT_FMT);
                String status       = p[4].trim();

                bookings.add(new Booking(bookingId, userId, eventId, createdAt, status));
            }
        } catch (IOException e) {
            System.out.println("Error loading bookings: " + e.getMessage());
        }
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public List<User>    getUsers()    { return users; }
    public List<Event>   getEvents()   { return events; }
    public List<Booking> getBookings() { return bookings; }
}
