package com.isychia.isychiachatapp;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class MongoDBConnection {
    public static void main(String[] args) {
        // Get the MongoDB URI from the environment variable
        String connectionString = System.getenv("MONGO_URI");

        // Ensure the connection string is not null or empty
        if (connectionString == null || connectionString.isEmpty()) {
            System.err.println("MONGO_URI environment variable is not set.");
            return;
        }

        // Configure server API for MongoDB
        ServerApi serverApi = ServerApi.builder()
                .version(ServerApiVersion.V1)
                .build();

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .serverApi(serverApi)
                .build();

        // Create a new client and connect to the server
        try (MongoClient mongoClient = MongoClients.create(settings)) {
            try {
                // Get the database and ping it
                MongoDatabase database = mongoClient.getDatabase("admin");  // Specify the correct database here
                database.runCommand(new Document("ping", 1));  // Send a ping command to check the connection
                System.out.println("Pinged your deployment. You successfully connected to MongoDB!");
            } catch (MongoException e) {
                e.printStackTrace();
            }
        }
    }

    public static void closeConnection() {
        // Logic for closing the connection, if needed
    }
}
