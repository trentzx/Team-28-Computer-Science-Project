package service;

import model.bookings.Booking;
import model.events.Event;
import model.users.User;

import java.util.*;

/*
  BookingService governs the complete booking lifecycle:
  creating, cancelling, waitlist promotion, and roster queries.
 */
public class BookingService {
    //main service class that handles all booking operations
    private List<Booking> bookings = new ArrayList<>();

    // eventId -> ordered list of userIds with confirmed seats
    private Map<String, List<String>> confirmedRoster = new HashMap<>();

    // eventId -> FIFO queue of userIds on the waitlist (first-come, first-served)
    private Map<String, LinkedList<String>> waitlistRoster = new HashMap<>();

    //counter used to generate unique book ID
    private int bookingCounter = 1;

    // Roster reconstruction from loaded CSV bookings

    /*
      Called by SystemData after loading bookings.csv so that the in-memory
      rosters match the persisted state.
      Waitlist order is determined by createdAt (earliest first) among
      all bookings with status Waitlisted.
     */
    public void reconstructRosters(List<Booking> loadedBookings, List<Event> events) {
        //rebuilds all booking data from saved CVS file
        this.bookings.addAll(loadedBookings);

        // Track the highest numeric booking ID so new ones don't collide
        for (Booking b : loadedBookings) {
            try {
                int num = Integer.parseInt(b.getBookingId().replaceAll("[^0-9]", ""));
                if (num >= bookingCounter) bookingCounter = num + 1;
            } catch (NumberFormatException ignored) {}
        }

        // Build confirmed rosters
        for (Booking b : loadedBookings) {
            //added confirmed bookings into comfirmed roster
            if ("Confirmed".equals(b.getBookingStatus())) {
                confirmedRoster.computeIfAbsent(b.getEventId(), k -> new ArrayList<>())
                        .add(b.getUserId());
            }
        }

        // Build waitlists sorted by createdAt ascending
        Map<String, List<Booking>> waitlisted = new HashMap<>();

        //collect all waitlisted bookings by event
        for (Booking b : loadedBookings) {
            if ("Waitlisted".equals(b.getBookingStatus())) {
                waitlisted.computeIfAbsent(b.getEventId(), k -> new ArrayList<>()).add(b);
            }
        }
        for (Map.Entry<String, List<Booking>> entry : waitlisted.entrySet()) {
            entry.getValue().sort(Comparator.comparing(Booking::getCreatedAt));
            //Sort Waitlist by time (earliest first)
            LinkedList<String> queue = new LinkedList<>();
            for (Booking b : entry.getValue()) queue.add(b.getUserId());
            waitlistRoster.put(entry.getKey(), queue);
        }

        // Ensure every event has an initialised roster entry
        for (Event e : events) {
            confirmedRoster.putIfAbsent(e.getEventId(), new ArrayList<>());
            waitlistRoster.putIfAbsent(e.getEventId(), new LinkedList<>());
        }
    }

    //Book Event

    /*
      Creates a booking for the given user and event, enforcing all rules:
       - Event must be Active
       - No duplicate bookings
       - Per-type booking limits (Student 3 / Staff 5 / Guest 1)
       - Confirmed if capacity available, Waitlisted if full

      @return human-readable result message
     */
    public String bookEvent(User user, Event event) {
        //handles booking logic for user and event
        String userId  = user.getUserId();
        String eventId = event.getEventId();

        //prevents booking if event is Cancelled
        if ("Cancelled".equals(event.getStatus())) {
            return "Cannot book a cancelled event.";
        }

        // Duplicate check (active bookings only)
        for (Booking b : bookings) {
            //checks if user already booked this event
            if (b.getUserId().equals(userId) && b.getEventId().equals(eventId)
                    && !"Cancelled".equals(b.getBookingStatus())) {
                return "User already has an active booking for this event.";
            }
        }

        // Booking-limit check
        int confirmed = countConfirmedBookings(userId);
        //checks if the booking number is over the max allowed slots
        if (confirmed >= user.getMaxBookings()) {
            return "User has reached their maximum confirmed-booking limit of " + user.getMaxBookings() + ".";
        }

        // Ensure roster maps are initialised
        confirmedRoster.putIfAbsent(eventId, new ArrayList<>());
        //ensure event has roster list ready
        waitlistRoster.putIfAbsent(eventId, new LinkedList<>());

        String status;
        if (confirmedRoster.get(eventId).size() < event.getCapacity()) {
            //if space available confirm booking
            confirmedRoster.get(eventId).add(userId);
            status = "Confirmed";
        } else {
            //if full add user to waitlist
            waitlistRoster.get(eventId).add(userId);
            status = "Waitlisted";
        }

        String bookingId = "B" + String.format("%04d", bookingCounter++);
        //generate unique booking ID
        bookings.add(new Booking(bookingId, userId, eventId, status));
        return "Booking " + bookingId + " created with status: " + status + ".";
    }

    //  Cancel a booking

    /*
      Cancels a booking. If it was Confirmed the first waitlisted user is
      automatically promoted to Confirmed.

     @return human-readable result message (includes promotion notice if applicable)
     */
    public String cancelBooking(String bookingId) {
        //cancel a booking and updates roster
        Booking booking = findBookingById(bookingId);
        //return message if booking not found
        if (booking == null)                            return "Booking not found.";
        if ("Cancelled".equals(booking.getBookingStatus())) return "Booking is already cancelled.";

        String eventId        = booking.getEventId();
        String userId         = booking.getUserId();
        String previousStatus = booking.getBookingStatus();

        booking.setBookingStatus("Cancelled");

        //mark booking as cancelled
        if ("Confirmed".equals(previousStatus)) {
            confirmedRoster.getOrDefault(eventId, new ArrayList<>()).remove(userId);

            // Promote first waitlisted user
            //and removes from confirmed list
            LinkedList<String> waitlist = waitlistRoster.getOrDefault(eventId, new LinkedList<>());
            if (!waitlist.isEmpty()) {
                //promote next user from waitlist
                String promoted = waitlist.poll();
                confirmedRoster.get(eventId).add(promoted);
                //add promoted user to confirmed list
                for (Booking b : bookings) {
                    if (b.getUserId().equals(promoted) && b.getEventId().equals(eventId)
                            && "Waitlisted".equals(b.getBookingStatus())) {
                        b.setBookingStatus("Confirmed");
                        return "Booking cancelled. User " + promoted
                                + " has been promoted from the waitlist to Confirmed.";
                    }
                }
            }
        } else if ("Waitlisted".equals(previousStatus)) {
            //if waitlisted remove from waitlist
            waitlistRoster.getOrDefault(eventId, new LinkedList<>()).remove(userId);
        }

        return "Booking " + bookingId + " has been cancelled.";
    }

    //  Cancel all bookings for an event (called when event is cancelled)

    /*
      Sets every Confirmed and Waitlisted booking for the given event to Cancelled
      and clears the waitlist. Called automatically when an event is cancelled.
     */
    public void cancelAllBookingsForEvent(String eventId) {
        //cancel all bookings for a specific event
        for (Booking b : bookings) {
            if (b.getEventId().equals(eventId)
                    && !"Cancelled".equals(b.getBookingStatus())) {
                //set all bookings to cancel
                b.setBookingStatus("Cancelled");
            }
        }
        confirmedRoster.getOrDefault(eventId, new ArrayList<>()).clear();
        //clear all roster for event
        waitlistRoster.getOrDefault(eventId, new LinkedList<>()).clear();
    }

    //  Queries
    //returns all bookings for a user
    public List<Booking> getBookingsForUser(String userId) {
        List<Booking> result = new ArrayList<>();
        for (Booking b : bookings) {
            if (b.getUserId().equals(userId)) result.add(b);
        }
        return result;
    }

    public List<String> getConfirmedRoster(String eventId) {
        return confirmedRoster.getOrDefault(eventId, new ArrayList<>());
    }

    public List<String> getWaitlist(String eventId) {
        return new ArrayList<>(waitlistRoster.getOrDefault(eventId, new LinkedList<>()));
    }

    public List<Booking> getAllBookings() { return bookings; }

    //  Helpers
    //counts confirmed bookings for a user
    private int countConfirmedBookings(String userId) {
        int count = 0;
        for (Booking b : bookings) {
            //increase count if booking is confirmed
            if (b.getUserId().equals(userId) && "Confirmed".equals(b.getBookingStatus())) count++;
        }
        return count;
    }

    //finds a booking by its ID
    private Booking findBookingById(String bookingId) {
        for (Booking b : bookings) {
            //returns book if founds
            if (b.getBookingId().equals(bookingId)) return b;
        }
        return null;
    }
}
