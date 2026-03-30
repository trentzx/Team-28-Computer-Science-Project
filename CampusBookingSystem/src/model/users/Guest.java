package model.users;

public class Guest extends User {//extends parents class User
    //constructor for guest
    public Guest(String userId, String name, String email) {
        super(userId, name, email); //uses super for variables from parents class
    }

    //gives maxbookings variable 1 for this subclass
    @Override
    public int getMaxBookings() { return 1; }

    //returns usertype as a string
    @Override
    public String getUserType() { return "Guest"; }
}