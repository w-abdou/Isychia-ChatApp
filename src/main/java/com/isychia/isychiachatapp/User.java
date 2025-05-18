package com.isychia.isychiachatapp;

import org.bson.Document;

public class User {
    private String username;
    private String email;
    private String password;

    // def constructor
    public User() {}

    // constructor with all fields
    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;   // Add email to the constructor
        this.password = password;
    }

    // convert document to user
    public static User fromDocument(Document doc) {
        return new User(
                doc.getString("username"),
                doc.getString("email"),
                doc.getString("password")
        );
    }

    // convert User to mongo document
    public Document toDocument() {
        return new Document("username", username)
                .append("email", email)
                .append("password", password);
    }

    // getters and setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
