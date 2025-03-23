package com.isychia.isychiachatapp;

import java.util.HashMap;
import java.util.Map;

public class UserService {
    private static UserService instance;
    private Map<String, User> usersByEmail;
    private Map<String, User> usersByUsername;
    private User currentUser;

    private UserService() {
        usersByEmail = new HashMap<>();
        usersByUsername = new HashMap<>();

        // Add some demo users
        addDemoUsers();
    }

    public static UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }

    private void addDemoUsers() {
        // Add demo users
        String[] users = {"Verue", "Farida", "Mahmoud", "Ahmed", "Hassan"};

        for (String username : users) {
            String email = username.toLowerCase() + "@example.com";
            User user = new User(username, email, "password123");
            usersByEmail.put(email, user);
            usersByUsername.put(username, user);
        }
    }

    public boolean registerUser(String username, String email, String password) {
        // Check if email or username already exists
        if (usersByEmail.containsKey(email) || usersByUsername.containsKey(username)) {
            return false;
        }

        // Create new user and add to maps
        User user = new User(username, email, password);
        usersByEmail.put(email, user);
        usersByUsername.put(username, user);
        return true;
    }

    public User loginUser(String identifier, String password) {
        User user = null;

        // Check if identifier is an email
        if (identifier.contains("@")) {
            user = usersByEmail.get(identifier);
        } else {
            // Otherwise treat it as a username
            user = usersByUsername.get(identifier);
        }

        // Check if user exists and password matches
        if (user != null && user.getPassword().equals(password)) {
            currentUser = user;
            return user;
        }

        return null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void logout() {
        currentUser = null;
    }

    public Map<String, User> getAllUsers() {
        return usersByUsername;
    }
}