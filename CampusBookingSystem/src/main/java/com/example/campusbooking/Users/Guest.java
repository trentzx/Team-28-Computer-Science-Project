package com.example.campusbooking.Users;

public class Guest extends User{

    private static final int MAX_CONFIRMED_BOOKINGS = 1;

    public Guest(String UserID, String Name, String email) {
        super(UserID, Name, email);
    }


    public int getMaxedConfirmedBookings() {
        return MAX_CONFIRMED_BOOKINGS;
    }

    @Override
    public String getUserType(){
        return "Guest";
    }
}
