package model.events;

public class Workshop extends Event {
    private String topic;

    public Workshop(String eventId, String title, String dateTime, String location, int capacity, String status, String topic) {
        super(eventId, title, dateTime, location, capacity, status);
        this.topic = topic;
    }

    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }

    @Override
    public String getEventType() { return "Workshop"; }

    @Override
    public String getTypeSpecificField() { return topic; }
}