public class User {

    private final String name; // Just the Users name
    private final String bookingId; // Unique booking id for this user

    private static int nextId = 1; // Keeps track of the next booking id number

    public User(String name) { // Creating the constructor
        this.name = name;
        this.bookingId = String.format("%03d", nextId); // Makes 1 become "001", 2 become "002", etc.
        nextId++; // Increases so the next user gets a new id
    }

    public String getName() { // getting the name
        return name;
    }

    public String getBookingId() { // getting the unique booking id
        return bookingId;
    }
}