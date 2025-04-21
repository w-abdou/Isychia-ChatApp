package com.isychia.isychiachatapp;

import com.mongodb.client.*;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.*;

public class UserService {

    private final MongoCollection<Document> userCollection;
    private final MongoDatabase database;
    private User currentUser;

    // MongoDB connection and collection setup
    public UserService() {
        MongoClient mongoClient = MongoClients.create("mongodb+srv://woroodabdou:ZdbwsoKdsI22Q532@cluster0.mongodb.net/isychia?retryWrites=true&w=majority");
        this.database = mongoClient.getDatabase("isychiaDB");
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
        MongoCollection<Document> usersCollection = database.getCollection("users");

        Document existingUser = usersCollection.find(eq("email", email)).first();
        if (existingUser != null) {
            return false; // Email already in use
        }

        Document newUser = new Document("username", username)
                .append("email", email)
                .append("password", password); // In production, hash this password!

        usersCollection.insertOne(newUser);
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


    public void addUser(User user) {
        Document newUser = new Document("username", user.getUsername())
                .append("email", user.getEmail())
                .append("password", user.getPassword());

        userCollection.insertOne(newUser);
    }

    public boolean userExists(String email, String username) {
        Bson filter = or(
                eq("email", email),
                eq("username", username)
        );
        Document existingUser = userCollection.find(filter).first();
        return existingUser != null;
    }
}