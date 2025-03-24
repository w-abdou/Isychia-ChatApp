package com.isychia.isychiachatapp;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class UserService {
    private Map<String, User> usersByUsername;
    private Map<String, User> usersByEmail;
    private User currentUser;

    public UserService() {
        usersByUsername = new HashMap<>();
        usersByEmail = new HashMap<>();
    }

    public boolean registerUser(String username, String email, String password) {
        if (username == null || email == null || password == null ||
                username.trim().isEmpty() || email.trim().isEmpty() || password.trim().isEmpty()) {
            return false;
        }

        if (usersByUsername.containsKey(username) || usersByEmail.containsKey(email)) {
            return false;
        }

        User newUser = new User(username, email, password);
        usersByUsername.put(username, newUser);
        usersByEmail.put(email, newUser);
        return true;
    }


    public boolean login(String username, String password) {
        User user = usersByUsername.get(username);
        if (user != null && user.getPassword().equals(password)) {
            currentUser = user;
            return true;
        }
        return false;
    }


    public void logout() {
        currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean updateUserProfile(String oldUsername, String newUsername, String newEmail) {
        // Get the user
        User user = usersByUsername.get(oldUsername);
        if (user == null) {
            return false;
        }

        // Check if new username or email is already taken (by another user)
        if (!oldUsername.equals(newUsername) && usersByUsername.containsKey(newUsername)) {
            return false;
        }

        if (!user.getEmail().equals(newEmail) && usersByEmail.containsKey(newEmail)) {
            return false;
        }

        // If username is changing, update maps
        if (!oldUsername.equals(newUsername)) {
            usersByUsername.remove(oldUsername);
            user.setUsername(newUsername);
            usersByUsername.put(newUsername, user);
        }

        // If email is changing, update maps
        if (!user.getEmail().equals(newEmail)) {
            usersByEmail.remove(user.getEmail());
            user.setEmail(newEmail);
            usersByEmail.put(newEmail, user);
        }

        // If this is the current user, update reference
        if (currentUser != null && currentUser.getUsername().equals(oldUsername)) {
            currentUser = user;
        }

        return true;
    }

    public boolean updateUserPassword(String username, String newPassword) {
        User user = usersByUsername.get(username);
        if (user == null) {
            return false;
        }

        user.setPassword(newPassword);
        return true;
    }

    public boolean updateUserStatus(String username, String newStatus) {
        User user = usersByUsername.get(username);
        if (user == null) {
            return false;
        }

        user.setStatus(newStatus);
        return true;
    }

    public boolean deleteUser(String username) {
        User user = usersByUsername.get(username);
        if (user == null) {
            return false;
        }

        // Remove user from maps
        usersByUsername.remove(username);
        usersByEmail.remove(user.getEmail());

        // If this was the current user, set current user to null
        if (currentUser != null && currentUser.getUsername().equals(username)) {
            currentUser = null;
        }

        return true;
    }

    public boolean isUsernameTaken(String username) {
        return usersByUsername.containsKey(username);
    }

    public boolean isEmailTaken(String email) {
        return usersByEmail.containsKey(email);
    }

    public void saveUsersToFile(String filePath) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(usersByUsername);
            oos.writeObject(usersByEmail);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public void loadUsersFromFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            usersByUsername = (Map<String, User>) ois.readObject();
            usersByEmail = (Map<String, User>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Map<String, User> getAllUsers() {
        return new HashMap<>(usersByUsername);
    }
}