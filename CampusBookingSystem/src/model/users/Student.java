package model.users;

public class Student extends User { //extends User parent class
    //constructor for Student
    public Student(String userId, String name, String email) {
        super(userId, name, email);//super for variables from parent class
    }
    //gives maxbookings variable 5 for this subclass
    @Override
    public int getMaxBookings() { return 3; }

    //returns usertype student as a string
    @Override
    public String getUserType() { return "Student"; }
}