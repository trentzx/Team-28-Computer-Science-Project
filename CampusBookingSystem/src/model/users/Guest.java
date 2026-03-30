package model.users;

public class Guest extends User {
    public Guest(String userId, String name, String email) {
        super(userId, name, email);
    }

    @Override
    public int getMaxBookings() { return 1; }

    @Override
    public String getUserType() { return "Guest"; }
}