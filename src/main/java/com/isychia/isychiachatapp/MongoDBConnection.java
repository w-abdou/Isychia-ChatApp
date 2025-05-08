package com.isychia.isychiachatapp;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class MongoDBConnection {
    private static MongoDBConnection instance;
    private MongoDatabase database;

    public MongoDBConnection() {
        MongoClient mongoClient = MongoClients.create("mongodb+srv://faridasoliman:<db_password>@isychia.bbrqq0g.mongodb.net/?retryWrites=true&w=majority&appName=Isychia"); // Update with your MongoDB URI
        database = mongoClient.getDatabase("IsychiaChatApp"); // Update with your database name
    }

    public static MongoDBConnection getInstance() {
        if (instance == null) {
            instance = new MongoDBConnection();
        }
        return instance;
    }

    public MongoDatabase getDatabase() {
        return database;
    }
    public void sendMessageToDB(Message message) {
        MongoCollection<Document> collection = database.getCollection("messages"); // You can change collection name
        collection.insertOne(message.toDocument());
    }
}
