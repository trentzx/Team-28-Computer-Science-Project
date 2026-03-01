package com.example.campusbooking.Users;

public class Student extends User {
    public static final int MAX_CONFIRMED_BOOKINGS = 3;

    public Student(String UserID, String Name, String email){
        super (UserID,Name,email);
    }

    public int getMaxedConfirmedBookings(){
        return MAX_CONFIRMED_BOOKINGS;
    }
    @Override
    public String getUserType(){
        return "Student";
    }
}
