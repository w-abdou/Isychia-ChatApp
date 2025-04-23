package com.isychia.isychiachatapp;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    private Stage primaryStage;
    private LoginUI loginUI;
    private RegisterUI registerUI;
    private ChatInterface chatInterface;
    private UserService userService;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("IsychiaChat");

        // Initialize services
        userService = new UserService();
        // Initialize UI components
        initializeUI();

        // Set up navigation
        setupNavigation();

        // Start with login screen
        showLoginScreen();

        primaryStage.show();
    }

    private void initializeUI() {
        // Create UI components
        loginUI = new LoginUI(userService);
        registerUI = new RegisterUI(userService);
        chatInterface = new ChatInterface(userService);
        //MongoDBConnection mongoDBConnection = new MongoDBConnection();
        //mongoDBConnection.connectToMongo();
    }

    private void setupNavigation() {
        // Set up login navigation
        loginUI.setShowRegistrationScreen(this::showRegistrationScreen);
        loginUI.setLoginCallback(this::handleLoginSuccess);

        // Set up registration navigation
        registerUI.setShowLoginScreen(this::showLoginScreen);

        // Set up chat navigation
        chatInterface.setOnLogoutAction(this::handleLogout);
    }

    private void showLoginScreen() {
        primaryStage.setScene(loginUI.getScene());
    }

    private void showRegistrationScreen() {
        primaryStage.setScene(registerUI.getScene());
    }

    private void showChatScreen() {
        primaryStage.setScene(chatInterface.getScene());
    }

    private void handleLoginSuccess(User user) {
        chatInterface.initialize(user);
        showChatScreen();
    }

    private void handleLogout() {
        userService.logout();
        showLoginScreen();
    }

    public static void main(String[] args) {
        launch(args);
    }
}