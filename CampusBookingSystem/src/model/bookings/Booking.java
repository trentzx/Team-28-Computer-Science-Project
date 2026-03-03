package model.bookings;

import java.time.LocalDateTime;

public class Booking {
    private String bookingId;
    private String userId;
    private String eventId;
    private LocalDateTime createdAt;
    private String bookingStatus; // Confirmed, Waitlisted, Cancelled

    public Booking(String bookingId, String userId, String eventId, String bookingStatus) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.eventId = eventId;
        this.createdAt = LocalDateTime.now();
        this.bookingStatus = bookingStatus;
    }

    // Constructor for loading from CSV with existing timestamp
    public Booking(String bookingId, String userId, String eventId, LocalDateTime createdAt, String bookingStatus) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.eventId = eventId;
        this.createdAt = createdAt;
        this.bookingStatus = bookingStatus;
    }
    //Getters and Booking status setter
    public String getBookingId() { return bookingId; }
    public String getUserId() { return userId; }
    public String getEventId() { return eventId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public String getBookingStatus() { return bookingStatus; }
    public void setBookingStatus(String bookingStatus) { this.bookingStatus = bookingStatus; }

    @Override
    public String toString() {
        return bookingId + " | " + userId + " | " + eventId + " | " + bookingStatus;
    }
}