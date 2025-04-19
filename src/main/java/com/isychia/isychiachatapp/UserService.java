package com.isychia.isychiachatapp;

import org.bson.Document;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.FindIterable;
import java.util.ArrayList;
import java.util.List;

public class UserService {

    private MongoCollection<Document> userCollection;
    private User currentUser; // Changed from Document to User
    private MongoCollection<Document> collection;

    // Constructor that takes a MongoCollection
    public UserService(MongoCollection<Document> usercollection) {
        this.collection = usercollection;
    }


    // Login method to authenticate user
    public boolean login(String username, String password) {
        Document user = userCollection.find(
                Filters.and(
                        Filters.eq("username", username),
                        Filters.eq("password", password)
                )
        ).first();

        if (user != null) {
            currentUser = User.fromDocument(user); // Convert Document to User
            return true;
        } else {
            return false;
        }
    }

    // Get current logged-in user
    public User getCurrentUser() {
        return currentUser;
    }

    // Register method (if you want to add this, else leave it out)
    public boolean registerUser(String username, String password, String email) {
        // Check if the user already exists
        Document existingUser = userCollection.find(Filters.eq("username", username)).first();
        if (existingUser != null) {
            return false; // User already exists
        }

        // Create new user document
        Document newUser = new Document("username", username)
                .append("password", password)
                .append("email", email);

        // Insert new user into collection
        userCollection.insertOne(newUser);
        return true; // Registration successful
    }

    // Get all users as User objects
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        FindIterable<Document> iterable = userCollection.find();
        for (Document doc : iterable) {
            users.add(User.fromDocument(doc)); // Convert each Document to User
        }
        return users;
    }

    // Logout method (clear the current user)
    public void logout() {
        currentUser = null;
    }
}
