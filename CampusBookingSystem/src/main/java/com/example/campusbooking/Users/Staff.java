package com.example.campusbooking.Users;

public class Staff extends User {

    private static final int MAX_CONFIRMED_BOOKINGS = 5;

    public Staff(String UserID, String Name, String email) {
        super(UserID, Name, email);
    }


    public int getMaxedConfirmedBookings() {
        return MAX_CONFIRMED_BOOKINGS;
    }

    @Override
    public String getUserType(){
        return "Staff";
    }
}
