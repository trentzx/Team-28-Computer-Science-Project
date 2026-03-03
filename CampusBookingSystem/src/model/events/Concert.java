package model.events;

public class Concert extends Event {
    private String ageRestriction;

    /*
    creates a new concert with the given details
     */
    public Concert(String eventId, String title, String dateTime, String location, int capacity, String status, String ageRestriction) {
        super(eventId, title, dateTime, location, capacity, status);
        this.ageRestriction = ageRestriction;
    }

    public String getAgeRestriction() { return ageRestriction; }
    public void setAgeRestriction(String ageRestriction) { this.ageRestriction = ageRestriction; }

    /*
    Returns the event type as a string
     */

    @Override
    public String getEventType() { return "Concert"; }

    @Override
    public String getTypeSpecificField() { return ageRestriction; }
}