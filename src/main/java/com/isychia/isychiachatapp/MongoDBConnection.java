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

    public void connectToMongo(){
        String uri = "mongodb+srv://faridasoliman:farida123@isychia.bbrqq0g.mongodb.net/?retryWrites=true&w=majority&appName=Isychia";
        try{
            MongoClient mongoClient = MongoClients.create(uri);
            MongoDatabase database = mongoClient.getDatabase("isychiaDB");
            System.out.println("Connected to the database successfully");
        } catch (Exception e) {
            System.out.println("Error connecting to the database: " + e.getMessage());
        }
    }
    public static void main(String[] args) {
    MongoDBConnection connection = new MongoDBConnection();
    connection.connectToMongo();
    }
}
