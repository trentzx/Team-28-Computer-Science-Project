package service;

import model.bookings.Booking;
import model.events.Event;
import model.users.User;

import java.util.*;

/**
 * BookingService governs the complete booking lifecycle:
 * creating, cancelling, waitlist promotion, and roster queries.
 */
public class BookingService {

    private List<Booking> bookings = new ArrayList<>();

    // eventId -> ordered list of userIds with confirmed seats
    private Map<String, List<String>> confirmedRoster = new HashMap<>();

    // eventId -> FIFO queue of userIds on the waitlist (first-come, first-served)
    private Map<String, LinkedList<String>> waitlistRoster = new HashMap<>();

    private int bookingCounter = 1;

    // ── Roster reconstruction from loaded CSV bookings ────────────────────────

    /**
     * Called by SystemData after loading bookings.csv so that the in-memory
     * rosters match the persisted state.
     * Waitlist order is determined by createdAt (earliest first) among
     * all bookings with status Waitlisted.
     */
    public void reconstructRosters(List<Booking> loadedBookings, List<Event> events) {
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
            if ("Confirmed".equals(b.getBookingStatus())) {
                confirmedRoster.computeIfAbsent(b.getEventId(), k -> new ArrayList<>())
                        .add(b.getUserId());
            }
        }

        // Build waitlists sorted by createdAt ascending
        Map<String, List<Booking>> waitlisted = new HashMap<>();
        for (Booking b : loadedBookings) {
            if ("Waitlisted".equals(b.getBookingStatus())) {
                waitlisted.computeIfAbsent(b.getEventId(), k -> new ArrayList<>()).add(b);
            }
        }
        for (Map.Entry<String, List<Booking>> entry : waitlisted.entrySet()) {
            entry.getValue().sort(Comparator.comparing(Booking::getCreatedAt));
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

    // ── Book an event ─────────────────────────────────────────────────────────

    /**
     * Creates a booking for the given user and event, enforcing all rules:
     *  - Event must be Active
     *  - No duplicate bookings
     *  - Per-type booking limits (Student 3 / Staff 5 / Guest 1)
     *  - Confirmed if capacity available, Waitlisted if full
     *
     * @return human-readable result message
     */
    public String bookEvent(User user, Event event) {
        String userId  = user.getUserId();
        String eventId = event.getEventId();

        if ("Cancelled".equals(event.getStatus())) {
            return "Cannot book a cancelled event.";
        }

        // Duplicate check (active bookings only)
        for (Booking b : bookings) {
            if (b.getUserId().equals(userId) && b.getEventId().equals(eventId)
                    && !"Cancelled".equals(b.getBookingStatus())) {
                return "User already has an active booking for this event.";
            }
        }

        // Booking-limit check
        int confirmed = countConfirmedBookings(userId);
        if (confirmed >= user.getMaxBookings()) {
            return "User has reached their maximum confirmed-booking limit of " + user.getMaxBookings() + ".";
        }

        // Ensure roster maps are initialised
        confirmedRoster.putIfAbsent(eventId, new ArrayList<>());
        waitlistRoster.putIfAbsent(eventId, new LinkedList<>());

        String status;
        if (confirmedRoster.get(eventId).size() < event.getCapacity()) {
            confirmedRoster.get(eventId).add(userId);
            status = "Confirmed";
        } else {
            waitlistRoster.get(eventId).add(userId);
            status = "Waitlisted";
        }

        String bookingId = "B" + String.format("%04d", bookingCounter++);
        bookings.add(new Booking(bookingId, userId, eventId, status));
        return "Booking " + bookingId + " created with status: " + status + ".";
    }

    // ── Cancel a booking ──────────────────────────────────────────────────────

    /**
     * Cancels a booking. If it was Confirmed the first waitlisted user is
     * automatically promoted to Confirmed.
     *
     * @return human-readable result message (includes promotion notice if applicable)
     */
    public String cancelBooking(String bookingId) {
        Booking booking = findBookingById(bookingId);
        if (booking == null)                            return "Booking not found.";
        if ("Cancelled".equals(booking.getBookingStatus())) return "Booking is already cancelled.";

        String eventId        = booking.getEventId();
        String userId         = booking.getUserId();
        String previousStatus = booking.getBookingStatus();

        booking.setBookingStatus("Cancelled");

        if ("Confirmed".equals(previousStatus)) {
            confirmedRoster.getOrDefault(eventId, new ArrayList<>()).remove(userId);

            // Promote first waitlisted user
            LinkedList<String> waitlist = waitlistRoster.getOrDefault(eventId, new LinkedList<>());
            if (!waitlist.isEmpty()) {
                String promoted = waitlist.poll();
                confirmedRoster.get(eventId).add(promoted);
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
            waitlistRoster.getOrDefault(eventId, new LinkedList<>()).remove(userId);
        }

        return "Booking " + bookingId + " has been cancelled.";
    }

    // ── Cancel all bookings for an event (called when event is cancelled) ─────

    /**
     * Sets every Confirmed and Waitlisted booking for the given event to Cancelled
     * and clears the waitlist. Called automatically when an event is cancelled.
     */
    public void cancelAllBookingsForEvent(String eventId) {
        for (Booking b : bookings) {
            if (b.getEventId().equals(eventId)
                    && !"Cancelled".equals(b.getBookingStatus())) {
                b.setBookingStatus("Cancelled");
            }
        }
        confirmedRoster.getOrDefault(eventId, new ArrayList<>()).clear();
        waitlistRoster.getOrDefault(eventId, new LinkedList<>()).clear();
    }

    // ── Queries ───────────────────────────────────────────────────────────────

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

    // ── Helpers ───────────────────────────────────────────────────────────────

    private int countConfirmedBookings(String userId) {
        int count = 0;
        for (Booking b : bookings) {
            if (b.getUserId().equals(userId) && "Confirmed".equals(b.getBookingStatus())) count++;
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
