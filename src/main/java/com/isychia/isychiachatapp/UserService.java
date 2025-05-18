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

    // mongoDB connection and collection setup
    public UserService() {
        MongoClient mongoClient = MongoClients.create("mongodb+srv://faridasoliman:farida123@isychia.bbrqq0g.mongodb.net/?retryWrites=true&w=majority&appName=Isychia");
        this.database = mongoClient.getDatabase("IsychiaDB");
        this.userCollection = database.getCollection("users");
    }


    // login to authenticate user
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


    public User getCurrentUser() {
        return currentUser;
    }



    public boolean registerUser(String username, String email, String password) {
      // if username aw email exists in the db
        Document existingUser = userCollection.find(
                or(
                        eq("username", username),
                        eq("email", email)
                )
        ).first();

        if (existingUser != null) {
            return false; // email aw username already in db
        }

        Document newUser = new Document("username", username)
                .append("email", email)
                .append("password", password);

        userCollection.insertOne(newUser);
        return true;
    }


    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        FindIterable<Document> iterable = userCollection.find();
        for (Document doc : iterable) {
            users.add(User.fromDocument(doc));
        }
        return users;
    }

    // logout
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