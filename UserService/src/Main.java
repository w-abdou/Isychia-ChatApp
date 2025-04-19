package com.isychia.isychiachatapp;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import javafx.application.Application;
import javafx.stage.Stage;
import org.bson.Document;

public class Main extends Application {
    private Stage primaryStage;
    private LoginUI loginUI;
    private RegisterUI registerUI;
    private ChatInterface chatInterface;
    private UserService userService;
    private MongoCollection<Document> userCollection;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("IsychiaChat");

        // Initialize MongoDB client and collection
        MongoDatabase database = MongoClients.create("mongodb://localhost:27017")
                .getDatabase("IsychiaChatDB");
         //userCollection = database.getCollection("users");
        MongoCollection<Document> collection = database.getCollection("users");
        // Initialize services
        //userService = new UserService();
        UserService userService = new UserService(collection);
        // Initialize UI components
        initializeUI();

        // Set up navigation
        setupNavigation();

        // Start with login screen
        showLoginScreen();

        primaryStage.show();
    }

    private void initializeUI() {
        // Create UI components with both userService and userCollection
        loginUI = new LoginUI(userService, userCollection);
        registerUI = new RegisterUI(userService);
        chatInterface = new ChatInterface(userService);
    }

    private void setupNavigation() {
        // Set up login navigation
        loginUI.set
