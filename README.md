# Team-28-Computer-Science-Project

# ENGG*1420 Campus Event Booking System

## Team 28

## How to Compile and Run

### Requirements
- IntelliJ IDEA
- JDK 25
- JavaFX 21

### Setup
1. Clone the repository
2. Open the project in IntelliJ IDEA
3. Go to File → Project Structure → Libraries and add:
    - org.openjfx:javafx-controls:21
    - org.openjfx:javafx-fxml:21
4. Go to Run → Edit Configurations
5. Set Main class to: `com.campusbooking.Main`
6. Add VM options:
```
--module-path "PATH_TO_JAVAFX" --add-modules javafx.controls,javafx.fxml
```
7. Place `users.csv`, `events.csv`, and `bookings.csv` in the project root
8. Run the program

### CSV File Format
- `users.csv` — userId, name, email, userType
- `events.csv` — eventId, title, dateTime, location, capacity, status, eventType, topic, speakerName, ageRestriction
- `bookings.csv` — bookingId, userId, eventId, createdAt, bookingStatus

## Running the JUnit Test Suite

### Requirements
- IntelliJ IDEA
- JUnit Jupiter 5.10.0 (added via Maven in Project Structure)

### How to Run
1. Open the project in IntelliJ IDEA
2. Navigate to the `test` folder in the project panel
3. Right click `BookingServiceTest`
4. Click **Run 'BookingServiceTest'**
5. The results panel will open at the bottom of the screen

### What the Tests Cover
- **testBookingUnderCapacity** — verifies that a booking is Confirmed when the event has available capacity
- **testBookingWhenFull** — verifies that a booking is Waitlisted when the event is at capacity
- **testWaitlistPromotion** — verifies that the first waitlisted user is automatically promoted to Confirmed when a confirmed booking is cancelled
- **testDuplicateBookingPrevented** — verifies that a user cannot book the same event twice