package model.events;

public class Concert extends Event {//subclass for event
    private String ageRestriction; //variable for only concert class


    //Constructor for concert with the given details
    public Concert(String eventId, String title, String dateTime, String location, int capacity, String status, String ageRestriction) {
        super(eventId, title, dateTime, location, capacity, status); //super uses values from superclass event
        this.ageRestriction = ageRestriction;
    }
    //getters and setters for Concert
    public String getAgeRestriction() { return ageRestriction; }
    public void setAgeRestriction(String ageRestriction) { this.ageRestriction = ageRestriction; }

    //Returns the event type as a string
    //overrides replace methode from parent classes
    @Override
    public String getEventType() { return "Concert"; }

    @Override
    public String getTypeSpecificField() { return ageRestriction; }
}