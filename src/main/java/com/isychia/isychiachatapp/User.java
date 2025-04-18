package com.isychia.isychiachatapp;

import org.bson.Document;

public class User {
    private String username;
    private String password;

    public User() {}

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public static User fromDocument(Document doc) {
        return new User(
                doc.getString("username"),
                doc.getString("password")
        );
    }

    public Document toDocument() {
        return new Document("username", username)
                .append("password", password);
    }

    // Getters & Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
