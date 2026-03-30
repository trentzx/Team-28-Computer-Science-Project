package model.events;

public class Seminar extends Event { //extends superclass Event
    private String speakerName; //variable for only Seminar

    //constructor for Seminar
    public Seminar(String eventId, String title, String dateTime, String location, int capacity, String status, String speakerName) {
        super(eventId, title, dateTime, location, capacity, status);//super uses variables from superclass event
        this.speakerName = speakerName;
    }

    //getter and setter for SpeakerName
    public String getSpeakerName() { return speakerName; }
    public void setSpeakerName(String speakerName) { this.speakerName = speakerName; }

    //returns event type as string
    @Override
    public String getEventType() { return "Seminar"; }

    //returns the value of Speakername
    @Override
    public String getTypeSpecificField() { return speakerName; }
}