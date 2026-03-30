package model.users;

public abstract class User {
    //Atributes
    private String userId;
    private String name;
    private String email;

    //Main Constructor
    public User(String userId, String name, String email) {
        this.userId = userId;
        this.name = name;
        this.email = email;
    }

    //Getters
    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public abstract int getMaxBookings();

    public abstract String getUserType();

    //toString takes all variables into a string format
    //uses getusertype from each subclass to get the correct usertype
    @Override
    public String toString() {
        return getUserType() + " | " + userId + " | " + name + " | " + email;
    }
}