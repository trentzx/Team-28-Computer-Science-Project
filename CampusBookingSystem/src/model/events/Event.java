package model.events;

public abstract class Event {
    //all variables declared for superclass
    private String eventId;
    private String title;
    private String dateTime;
    private String location;
    private int capacity;
    private String status;

    //Constructor for superclass and to be used with super variable for subclass constructors
    public Event(String eventId, String title, String dateTime, String location, int capacity, String status) {
        this.eventId = eventId;
        this.title = title;
        this.dateTime = dateTime;
        this.location = location;
        this.capacity = capacity;
        this.status = status;
    }

    //Getters and Setters
    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDateTime() { return dateTime; }
    public void setDateTime(String dateTime) { this.dateTime = dateTime; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) {
        //makes sure it has a capacity of greater than 0
        if (capacity <= 0) throw new IllegalArgumentException("Capacity must be > 0");
        this.capacity = capacity;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public abstract String getEventType();
    public abstract String getTypeSpecificField();

    //returns entire statement with each variable for superclass
    //has geteventtype to get the variable from the required subclass
    @Override
    public String toString() {
        return getEventType() + " | " + eventId + " | " + title + " | " + dateTime + " | " + location + " | " + capacity + " | " + status;
    }
}