package com.isychia.isychiachatapp;

import com.mongodb.client.*;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.*;

public class UserService {

    private final MongoCollection<Document> userCollection;
    private User currentUser;

    // MongoDB connection and collection setup
    public UserService() {
        MongoClient mongoClient = MongoClients.create("mongodb+srv://woroodabdou:ZdbwsoKdsI22Q532@cluster0.mongodb.net/isychia?retryWrites=true&w=majority");
        MongoDatabase database = mongoClient.getDatabase("isychia");
        this.userCollection = database.getCollection("users");
    }

    // Login method to authenticate user
    public boolean login(String username, String password) {
        Document user = userCollection.find(
                and(
                        eq("username", username),
                        eq("password", password)
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

    // Register method
    public boolean registerUser(String username, String email, String password) {
        // Check if email already exists
        Document existingUser = userCollection.find(eq("email", email)).first();
        if (existingUser != null) {
            return false; // Email already in use
        }

        // Create new user document
        Document newUser = new Document("username", username)
                .append("email", email)
                .append("password", password); // Reminder: hash in production!

        userCollection.insertOne(newUser);
        return true;
    }

    // Get all users as User objects
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        FindIterable<Document> iterable = userCollection.find();
        for (Document doc : iterable) {
            users.add(User.fromDocument(doc));
        }
        return users;
    }

    // Logout method (clear the current user)
    public void logout() {
        currentUser = null;
        System.out.println("User has been logged out.");
    }
}
