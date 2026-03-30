package service;

import model.bookings.Booking;
import model.events.Event;
import model.users.User;

import java.util.List;

/**
 * SystemData is the single source of truth for all runtime data.
 * It loads users, events, and bookings from CSV at startup and
 * exposes them to every controller through a singleton.
 */
public class SystemData {

    private static SystemData instance;

    private final List<User>    users;
    private final List<Event>   events;
    private final BookingService bookingService;

    private SystemData() {
        DataLoader loader = new DataLoader();
        loader.loadUsers("users.csv");
        loader.loadEvents("events.csv");
        loader.loadBookings("bookings.csv");

        users  = loader.getUsers();
        events = loader.getEvents();

        bookingService = new BookingService();
        // Rebuild in-memory rosters from the loaded bookings
        bookingService.reconstructRosters(loader.getBookings(), events);
    }

    public static SystemData getInstance() {
        if (instance == null) instance = new SystemData();
        return instance;
    }

    // ── Accessors ─────────────────────────────────────────────────────────────

    public List<User>    getUsers()         { return users; }
    public List<Event>   getEvents()        { return events; }
    public BookingService getBookingService() { return bookingService; }

    public void addUser(User user)   { users.add(user); }
    public void addEvent(Event event) { events.add(event); }

    public User findUserById(String userId) {
        for (User u : users) {
            if (u.getUserId().equals(userId)) return u;
        }
        return null;
    }

    public Event findEventById(String eventId) {
        for (Event e : events) {
            if (e.getEventId().equals(eventId)) return e;
        }
        return null;
    }

    public boolean userIdExists(String userId) {
        return findUserById(userId) != null;
    }

    public boolean eventIdExists(String eventId) {
        return findEventById(eventId) != null;
    }
}
