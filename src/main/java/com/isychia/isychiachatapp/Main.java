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


        // initialize services
        userService = new UserService();
        // UI components
        initializeUI();

        //nav
        setupNavigation();

        // login first
        showLoginScreen();

        primaryStage.show();
    }

    private void initializeUI() {

        loginUI = new LoginUI(userService);
        registerUI = new RegisterUI(userService);
        chatInterface = new ChatInterface(userService);

    }

    private void setupNavigation() {

        loginUI.setShowRegistrationScreen(this::showRegistrationScreen);
        loginUI.setLoginCallback(this::handleLoginSuccess);


        registerUI.setShowLoginScreen(this::showLoginScreen);


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