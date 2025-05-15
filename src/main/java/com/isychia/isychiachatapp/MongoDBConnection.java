package com.isychia.isychiachatapp;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;

public class MongoDBConnection {
    private static MongoDBConnection instance;
    private MongoDatabase database;

    public MongoDBConnection() {
        MongoClient mongoClient = MongoClients.create("mongodb+srv://faridasoliman:farida123@isychia.bbrqq0g.mongodb.net/?retryWrites=true&w=majority&appName=Isychia");
        database = mongoClient.getDatabase("IsychiaDB");
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
        MongoCollection<Document> collection = database.getCollection("messages");
        collection.insertOne(message.toDocument());
    }

    // ✅ NEW: Fetch unread messages for a user
    public List<Message> getUnreadMessagesForUser(String receiverID) {
        MongoCollection<Document> collection = database.getCollection("messages");

        Bson filter = Filters.and(
                Filters.eq("receiverID", receiverID),
                Filters.eq("isRead", false),
                Filters.eq("isDeleted", false)
        );

        List<Message> messages = new ArrayList<>();
        for (Document doc : collection.find(filter)) {
            messages.add(new Message(doc)); // requires the new Message(Document) constructor
        }

        return messages;
    }


    // ✅ Optional: Mark message as read
    public void markMessageAsRead(String messageID) {
        MongoCollection<Document> collection = database.getCollection("messages");
        collection.updateOne(Filters.eq("messageID", messageID), new Document("$set", new Document("isRead", true)));
    }
}
