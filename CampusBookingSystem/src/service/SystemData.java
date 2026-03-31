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
    //holds all users in system
    private final List<User>    users;
    //holds all events in system
    private final List<Event>   events;
    //handles all booking related operations
    private final BookingService bookingService;

    private SystemData() {
        //provide constructor to prevent multiple instances
        DataLoader loader = new DataLoader();
        //load all from CSV files
        loader.loadUsers("users.csv");
        loader.loadEvents("events.csv");
        loader.loadBookings("bookings.csv");

        users  = loader.getUsers();
        events = loader.getEvents();

        //create booking service
        bookingService = new BookingService();
        // Rebuild in-memory rosters from the loaded bookings
        bookingService.reconstructRosters(loader.getBookings(), events);
    }

    public static SystemData getInstance() {
        //returns the single instance of systemdata (creates if needed)
        if (instance == null) instance = new SystemData();
        //returns the shared instance
        return instance;
    }

    // ── Accessors ─────────────────────────────────────────────────────────────
    //returns list of users, events, and the booking services
    public List<User>    getUsers()         { return users; }
    public List<Event>   getEvents()        { return events; }
    public BookingService getBookingService() { return bookingService; }

    //adds new user and event to system
    public void addUser(User user)   { users.add(user); }
    public void addEvent(Event event) { events.add(event); }

    //searches for user by ID
    public User findUserById(String userId) {
        for (User u : users) {
            if (u.getUserId().equals(userId)) return u;
        }
        //return null if not found
        return null;
    }

    //searches for event by ID
    public Event findEventById(String eventId) {
        for (Event e : events) {
            //check if event ID matches
            if (e.getEventId().equals(eventId)) return e;
        }
        //return null if not founds
        return null;
    }

    public boolean userIdExists(String userId) {
        return findUserById(userId) != null;
    } //check if User ID exist in system

    public boolean eventIdExists(String eventId) {
        return findEventById(eventId) != null;
    } //check if event ID exists in system
}
