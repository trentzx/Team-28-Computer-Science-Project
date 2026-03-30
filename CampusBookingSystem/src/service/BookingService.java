package service;

import model.bookings.Booking;
import model.events.Event;
import model.users.User;

import java.util.*;

public class BookingService {

    private List<Booking> bookings = new ArrayList<>();
    // Tracks confirmed bookings per event: eventId -> list of userIds
    private Map<String, List<String>> confirmedRoster = new HashMap<>();
    // Tracks waitlist per event: eventId -> ordered list of userIds
    private Map<String, LinkedList<String>> waitlistRoster = new HashMap<>();

    private int bookingCounter = 1;

    /*
     Books an event for a user. Enforces all booking rules:
      - No duplicate bookings
      - Booking limits by user type
      - Confirmed if capacity available, Waitlisted if full
     */
    public String bookEvent(User user, Event event) {
        String userId = user.getUserId();
        String eventId = event.getEventId();

        // Check if event is cancelled
        if (event.getStatus().equals("Cancelled")) {
            return "Cannot book a cancelled event.";
        }

        // Check for duplicate booking
        for (Booking b : bookings) {
            if (b.getUserId().equals(userId) && b.getEventId().equals(eventId)
                    && !b.getBookingStatus().equals("Cancelled")) {
                return "User already has a booking for this event.";
            }
        }

        // Check booking limits by user type
        int confirmedCount = countConfirmedBookings(userId);
        if (confirmedCount >= user.getMaxBookings()) {
            return "User has reached their maximum booking limit of " + user.getMaxBookings() + ".";
        }

        // Initialize rosters if needed
        confirmedRoster.putIfAbsent(eventId, new ArrayList<>());
        waitlistRoster.putIfAbsent(eventId, new LinkedList<>());

        // Check capacity
        int confirmedForEvent = confirmedRoster.get(eventId).size();
        String status;

        if (confirmedForEvent < event.getCapacity()) {
            // Space available - confirm the booking
            confirmedRoster.get(eventId).add(userId);
            status = "Confirmed";
        } else {
            // Event is full - add to waitlist
            waitlistRoster.get(eventId).add(userId);
            status = "Waitlisted";
        }

        // Create and store the booking
        String bookingId = "B" + String.format("%04d", bookingCounter++);
        Booking booking = new Booking(bookingId, userId, eventId, status);
        bookings.add(booking);

        return "Booking " + bookingId + " created with status: " + status;
    }


     /*Cancels a booking. If it was Confirmed, promotes the first
     waitlisted user automatically.
      */

    public String cancelBooking(String bookingId) {
        Booking booking = findBookingById(bookingId);
        if (booking == null) return "Booking not found.";
        if (booking.getBookingStatus().equals("Cancelled")) return "Booking is already cancelled.";

        String eventId = booking.getEventId();
        String userId = booking.getUserId();
        String previousStatus = booking.getBookingStatus();

        // Cancel the booking
        booking.setBookingStatus("Cancelled");

        if (previousStatus.equals("Confirmed")) {
            // Remove from confirmed roster
            confirmedRoster.getOrDefault(eventId, new ArrayList<>()).remove(userId);

            // Promote first waitlisted user
            LinkedList<String> waitlist = waitlistRoster.getOrDefault(eventId, new LinkedList<>());
            if (!waitlist.isEmpty()) {
                String promotedUserId = waitlist.poll(); // removes first
                confirmedRoster.get(eventId).add(promotedUserId);

                // Update their booking status
                for (Booking b : bookings) {
                    if (b.getUserId().equals(promotedUserId) && b.getEventId().equals(eventId)
                            && b.getBookingStatus().equals("Waitlisted")) {
                        b.setBookingStatus("Confirmed");
                        return "Booking cancelled. User " + promotedUserId + " has been promoted from waitlist to Confirmed.";
                    }
                }
            }
        } else if (previousStatus.equals("Waitlisted")) {
            // Remove from waitlist roster
            waitlistRoster.getOrDefault(eventId, new LinkedList<>()).remove(userId);
        }

        return "Booking " + bookingId + " has been cancelled.";
    }

    //Returns all bookings for a specific user

    public List<Booking> getBookingsForUser(String userId) {
        List<Booking> result = new ArrayList<>();
        for (Booking b : bookings) {
            if (b.getUserId().equals(userId)) result.add(b);
        }
        return result;
    }

    //Returns confirmed roster for an event

    public List<String> getConfirmedRoster(String eventId) {
        return confirmedRoster.getOrDefault(eventId, new ArrayList<>());
    }


    //Returns waitlist for an event

    public List<String> getWaitlist(String eventId) {
        return new ArrayList<>(waitlistRoster.getOrDefault(eventId, new LinkedList<>()));
    }

    public List<Booking> getAllBookings() { return bookings; }

    private int countConfirmedBookings(String userId) {
        int count = 0;
        for (Booking b : bookings) {
            if (b.getUserId().equals(userId) && b.getBookingStatus().equals("Confirmed")) count++;
        }
        return count;
    }

    private Booking findBookingById(String bookingId) {
        for (Booking b : bookings) {
            if (b.getBookingId().equals(bookingId)) return b;
        }
        return null;
    }
}