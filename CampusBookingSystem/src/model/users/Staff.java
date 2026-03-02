package model.users;

public class Staff extends User {
    public Staff(String userId, String name, String email) {
        super(userId, name, email);
    }

    @Override
    public int getMaxBookings() { return 5; }

    @Override
    public String getUserType() { return "Staff"; }
}