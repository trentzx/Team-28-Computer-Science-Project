import java.util.*;

public class Event {

    private final String eventName; // initalizing variables
    private final int capacity;

    // Confirmed list , set added because we only want one name
    private final Set<String> confirmedNames = new HashSet<>();

    // WAITLIST:
    // Using a Queue for waitlist
    private final Queue<String> waitlistNames = new ArrayDeque<>();

    public Event(String eventName, int capacity) { //Constuctor Event
        this.eventName = eventName;
        this.capacity = capacity;
    }

    // Basic getters
    public String getEventName() { return eventName; }
    public int getCapacity() { return capacity; }

    // How many confirmed seats taken?
    public int getConfirmedCount() { return confirmedNames.size(); }

    // Is this person already confirmed?
    public boolean isConfirmed(String bookingId) { return confirmedNames.contains(bookingId); }

    // Is this person already on waitlist?
    public boolean isWaitlisted(String bookingId) { return waitlistNames.contains(bookingId); }

    // Does the event have room?
    public boolean hasSpace() { return confirmedNames.size() < capacity; }

    // Add a confirmed seat
    public void addConfirmedName(String bookingId) { confirmedNames.add(bookingId); }

    // Remove a confirmed seat
    public void removeConfirmedName(String bookingId) { confirmedNames.remove(bookingId); }


    // Add to waitlist


    public void addToWaitlist(String bookingId) {
        if (!waitlistNames.contains(bookingId)) {
            waitlistNames.add(bookingId);
        }
    }

    // Remove from waitlist (used when someone cancels while waiting)
    public void removeFromWaitlist(String bookingId) {
        waitlistNames.remove(bookingId);
    }


    //Remove and return the next person in the waitlist


    public String popNextWaitlistedName() {
        return waitlistNames.poll(); // poll() returns null if the waitlist is empty
    }


    // get and returning confirmed names
    public Set<String> getConfirmedNames() {
        return confirmedNames;
    }

    // getting and returning waitlist
    public Queue<String> getWaitlistNames() {
        return waitlistNames;
    }
}