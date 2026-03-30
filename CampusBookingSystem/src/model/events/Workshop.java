package model.events;

public class Workshop extends Event {//extends superclass event
    private String topic;//workshop variable

    //constructor for workshop
    public Workshop(String eventId, String title, String dateTime, String location, int capacity, String status, String topic) {
        super(eventId, title, dateTime, location, capacity, status);//gets variables from superclass event
        this.topic = topic;
    }
    //getters and setters
    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }

    //returns eventtype as string
    @Override
    public String getEventType() { return "Workshop"; }

    //returns topic as variable value
    @Override
    public String getTypeSpecificField() { return topic; }
}