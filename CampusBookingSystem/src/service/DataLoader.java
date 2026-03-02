package service;

import model.users.*;
import java.io.*;
import java.util.*;

public class DataLoader {
    private List<User> users = new ArrayList<>();

    public void loadUsers(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                String userId = parts[0];
                String name = parts[1];
                String email = parts[2];
                String type = parts[3];

                switch (type) {
                    case "Student" -> users.add(new Student(userId, name, email));
                    case "Staff" -> users.add(new Staff(userId, name, email));
                    case "Guest" -> users.add(new Guest(userId, name, email));
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading users: " + e.getMessage());
        }
    }

    public List<User> getUsers() { return users; }
}