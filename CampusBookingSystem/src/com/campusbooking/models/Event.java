package com.campusbooking.models;

public class Event {
    private String eventId;
    private String title;
    private String dateTime;
    private String location;
    private int capacity;
    private String status;
    private String eventType;

    public Event(String eventId, String title, String dateTime, String location, int capacity, String status, String eventType) {
        this.eventId = eventId;
        this.title = title;
        this.dateTime = dateTime;
        this.location = location;
        this.capacity = capacity;
        this.status = status;
        this.eventType = eventType;
    }

    // Getters and Setters required by the TableView PropertyValueFactory
    public String getEventId() { return eventId; }
    public String getTitle() { return title; }
    public String getDateTime() { return dateTime; }
    public String getLocation() { return location; }
    public int getCapacity() { return capacity; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getEventType() { return eventType; }
}