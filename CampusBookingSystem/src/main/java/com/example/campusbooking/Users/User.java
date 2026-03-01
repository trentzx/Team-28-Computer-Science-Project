package com.example.campusbooking.Users;

public class User {
    public String UserID;
    public String Name;
    public String email;

    public User (String UserID, String Name,String email){
        this.UserID = UserID;
        this.Name = Name;
        this.email = email;
    }
    public User (String UserID, String Name){
        this.UserID = UserID;
        this.Name = Name;
    }
    public String getUserID(){return UserID;}
    public String getName(){return Name;}
    public String getEmail(){return email;}

    public int getMaxConfirmedBookings() {
        return 0;
    }

    public String getUserType() {
        return null;
    }

    @Override
    public String toString(){
        return UserID + "|" + Name + "|" + email + "|" + getUserType();
    }



}
