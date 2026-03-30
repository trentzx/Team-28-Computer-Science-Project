package service;

import model.bookings.Booking;
import model.events.*;
import model.users.User;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

/*
 DataSaver is responsible for writing everything back to the CSV files
 when the program closes. Without this, any users, events, or bookings
 created during a session would be lost on exit.
 */
public class DataSaver {

    // Formatter to make sure dates are saved in the same format we load them
    private static final DateTimeFormatter DT_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    // Save Users

    /*
     Grabs every user currently in the system and writes them to users.csv.
     This overwrites the old file so we always have a fresh, accurate copy.
     */
    public void saveUsers(String filename) {
        List<User> users = SystemData.getInstance().getUsers();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            // Write the header row first so the file is readable
            bw.write("userId,name,email,userType");
            bw.newLine();
            // Write one row per user
            for (User u : users) {
                bw.write(u.getUserId() + ","
                        + u.getName() + ","
                        + u.getEmail() + ","
                        + u.getUserType());
                bw.newLine();
            }
            System.out.println("Users saved to " + filename);
        } catch (IOException e) {
            System.err.println("Error saving users: " + e.getMessage());
        }
    }

    //Save Events

    /*
      Grabs every event and writes them to events.csv.
      Each event type (Workshop/Seminar/Concert) has one type-specific field.
      We put that field in the right column and leave the other two blank
      so the format stays consistent with what DataLoader expects.
     */
    public void saveEvents(String filename) {
        List<Event> events = SystemData.getInstance().getEvents();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            bw.write("eventId,title,dateTime,location,capacity,status,eventType,topic,speakerName,ageRestriction");
            bw.newLine();
            for (Event e : events) {
                // Start with all three type-specific fields blank
                String topic          = "";
                String speakerName    = "";
                String ageRestriction = "";

                // Fill in whichever one applies to this event type
                if (e instanceof Workshop w)       topic          = w.getTopic();
                else if (e instanceof Seminar s)   speakerName    = s.getSpeakerName();
                else if (e instanceof Concert c)   ageRestriction = c.getAgeRestriction();

                bw.write(e.getEventId() + ","
                        + e.getTitle() + ","
                        + e.getDateTime() + ","
                        + e.getLocation() + ","
                        + e.getCapacity() + ","
                        + e.getStatus() + ","
                        + e.getEventType() + ","
                        + topic + ","
                        + speakerName + ","
                        + ageRestriction);
                bw.newLine();
            }
            System.out.println("Events saved to " + filename);
        } catch (IOException e) {
            System.err.println("Error saving events: " + e.getMessage());
        }
    }

    //Save bookings

    /*
      Grabs every booking and writes them to bookings.csv.
      This includes Confirmed, Waitlisted, and Cancelled bookings so that
      the full history is preserved and waitlist order can be reconstructed on reload.
     */
    public void saveBookings(String filename) {
        List<Booking> bookings = SystemData.getInstance().getBookingService().getAllBookings();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            bw.write("bookingId,userId,eventId,createdAt,bookingStatus");
            bw.newLine();
            for (Booking b : bookings) {
                bw.write(b.getBookingId() + ","
                        + b.getUserId() + ","
                        + b.getEventId() + ","
                        + b.getCreatedAt().format(DT_FMT) + ","
                        + b.getBookingStatus());
                bw.newLine();
            }
            System.out.println("Bookings saved to " + filename);
        } catch (IOException e) {
            System.err.println("Error saving bookings: " + e.getMessage());
        }
    }

    // Save All

    /*
      Saves everything in one go. This is what gets called when the window closes.
     */
    public void saveAll() {
        saveUsers("users.csv");
        saveEvents("events.csv");
        saveBookings("bookings.csv");
    }
}
