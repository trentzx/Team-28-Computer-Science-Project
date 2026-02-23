import model.users.*;

public class Main {
    public static void main(String[] args) {
        User student = new Student("U001", "Alice Smith", "alice@uoguelph.ca");
        User staff = new Staff("U002", "Bob Jones", "bob@uoguelph.ca");
        User guest = new Guest("U003", "Charlie Brown", "charlie@gmail.com");

        System.out.println(student);
        System.out.println("Max bookings: " + student.getMaxBookings());

        System.out.println(staff);
        System.out.println("Max bookings: " + staff.getMaxBookings());

        System.out.println(guest);
        System.out.println("Max bookings: " + guest.getMaxBookings());
    }
}