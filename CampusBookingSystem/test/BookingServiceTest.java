import model.bookings.Booking;
import model.events.Workshop;
import model.users.Guest;
import model.users.Staff;
import model.users.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.BookingService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BookingServiceTest {

    private BookingService service;
    private Workshop workshop;
    private Student student;

    @BeforeEach
    void setup() {
        // Fresh service and test data before each test
        service  = new BookingService();
        workshop = new Workshop("E001", "Test Workshop", "2026-04-01T10:00",
                "Room 1", 2, "Active", "Java");
        student  = new Student("U001", "Alice", "alice@test.com");
    }

    // Test 1: Booking when capacity is available → should be Confirmed
    @Test
    void testBookingUnderCapacity() {
        String result = service.bookEvent(student, workshop);
        assertTrue(result.contains("Confirmed"),
                "Booking should be Confirmed when capacity is available");
    }

    // Test 2: Booking when event is full → should be Waitlisted
    @Test
    void testBookingWhenFull() {
        Student student2 = new Student("U002", "Bob", "bob@test.com");
        Student student3 = new Student("U003", "Charlie", "charlie@test.com");

        service.bookEvent(student,  workshop); // fills seat 1
        service.bookEvent(student2, workshop); // fills seat 2 (now full)
        String result = service.bookEvent(student3, workshop); // should waitlist

        assertTrue(result.contains("Waitlisted"),
                "Booking should be Waitlisted when event is full");
    }

    // Test 3: Cancel confirmed booking → first waitlisted user gets promoted
    @Test
    void testWaitlistPromotion() {
        Student student2 = new Student("U002", "Bob", "bob@test.com");
        Student student3 = new Student("U003", "Charlie", "charlie@test.com");

        service.bookEvent(student,  workshop); // confirmed - booking B0001
        service.bookEvent(student2, workshop); // confirmed - booking B0002 (now full)
        service.bookEvent(student3, workshop); // waitlisted - booking B0003

        // Cancel the first confirmed booking
        service.cancelBooking("B0001");

        // Charlie should now be promoted to Confirmed
        List<Booking> bookings = service.getAllBookings();
        Booking charliesBooking = bookings.stream()
                .filter(b -> b.getUserId().equals("U003"))
                .findFirst()
                .orElse(null);

        assertNotNull(charliesBooking, "Charlie's booking should exist");
        assertEquals("Confirmed", charliesBooking.getBookingStatus(),
                "Charlie should be promoted to Confirmed after cancellation");
    }

    // Test 4: Booking the same event twice → should be blocked
    @Test
    void testDuplicateBookingPrevented() {
        service.bookEvent(student, workshop); // first booking succeeds
        String result = service.bookEvent(student, workshop); // second should fail

        assertTrue(result.contains("already"),
                "Duplicate booking should be blocked");
    }
}