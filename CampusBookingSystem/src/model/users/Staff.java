package model.users;

public class Staff extends User { //extends parents class User
    //constructor for staff
    public Staff(String userId, String name, String email) {
        super(userId, name, email); //uses super for variables from parent class
    }
    //gives maxbookings variable 5 for this subclass
    @Override
    public int getMaxBookings() { return 5; }

    //returns usertype as a string
    @Override
    public String getUserType() { return "Staff"; }
}