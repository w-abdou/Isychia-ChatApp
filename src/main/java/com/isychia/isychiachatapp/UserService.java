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

    // MongoDB connection
    public UserService() {
        MongoClient mongoClient = MongoClients.create("mongodb+srv://faridasoliman:farida123@isychia.bbrqq0g.mongodb.net/?retryWrites=true&w=majority&appName=Isychia");
        this.database = mongoClient.getDatabase("IsychiaDB");
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
            currentUser = User.fromDocument(user);
            return true;
        } else {
            return false;
        }
    }

    // Get current logged-in user
    public User getCurrentUser() {
        return currentUser;
    }


    // Register (baad el fix)
    public boolean registerUser(String username, String email, String password) {
        // Check if username OR email already exists
        Document existingUser = userCollection.find(
                or(
                        eq("username", username),
                        eq("email", email)
                )
        ).first();

        if (existingUser != null) {
            return false;
        }

        Document newUser = new Document("username", username)
                .append("email", email)
                .append("password", password);

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

    // Logout method 
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
