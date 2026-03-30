package service;

import model.users.*;
import model.events.*;
import java.util.*;

public class SystemData {
    private static SystemData instance;
    private List<User> users = new ArrayList<>();
    private List<Event> events = new ArrayList<>();
    private BookingService bookingService;

    private SystemData() {
        DataLoader loader = new DataLoader();
        loader.loadUsers("users.csv");
        loader.loadEvents("events.csv");
        users = loader.getUsers();
        events = loader.getEvents();
        bookingService = new BookingService();
    }

    public static SystemData getInstance() {
        if (instance == null) instance = new SystemData();
        return instance;
    }

    public List<User> getUsers() { return users; }
    public List<Event> getEvents() { return events; }
    public BookingService getBookingService() { return bookingService; }

    public void addUser(User user) { users.add(user); }
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
}