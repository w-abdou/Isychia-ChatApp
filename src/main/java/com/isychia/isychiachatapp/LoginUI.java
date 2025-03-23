package com.isychia.isychiachatapp;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class LoginUI {
    private Scene loginScene;
    private TextField usernameOrEmailField;
    private PasswordField passwordField;
    private Label errorLabel;
    private UserService userService;

    // Callbacks
    private Runnable showRegistrationScreen;
    private LoginCallback loginCallback;

    public LoginUI(UserService userService) {
        this.userService = userService;
        createLoginScene();
    }

    private void createLoginScene() {
        VBox loginContainer = new VBox(20);
        loginContainer.getStyleClass().add("auth-container");
        loginContainer.setAlignment(Pos.CENTER);

        // Logo
        Circle logo = new Circle(50);
        logo.setFill(Color.rgb(114, 137, 218));

        // Title
        Label title = new Label("IsychiaChat");
        title.getStyleClass().add("auth-title");

        Label subtitle = new Label("Log in to your account");
        subtitle.getStyleClass().add("auth-subtitle");

        // Form fields
        usernameOrEmailField = new TextField();
        usernameOrEmailField.setPromptText("Email or Username");
        usernameOrEmailField.getStyleClass().add("auth-field");

        passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.getStyleClass().add("auth-field");

        // Error message
        errorLabel = new Label("");
        errorLabel.getStyleClass().add("error-label");
        errorLabel.setVisible(false);

        // Login button
        Button loginButton = new Button("Log In");
        loginButton.getStyleClass().add("auth-button");
        loginButton.setOnAction(e -> handleLogin());

        // Register link
        HBox registerLinkContainer = new HBox();
        registerLinkContainer.setAlignment(Pos.CENTER);
        Label registerPrompt = new Label("Don't have an account? ");
        registerPrompt.setStyle("-fx-text-fill: #B9BBBE");
        Label registerLink = new Label("Register");
        registerLink.getStyleClass().add("auth-link");
        registerLink.setOnMouseClicked(e -> {
            if (showRegistrationScreen != null) {
                resetForm();
                showRegistrationScreen.run();
            }
        });
        registerLinkContainer.getChildren().addAll(registerPrompt, registerLink);

        loginContainer.getChildren().addAll(
                logo, title, subtitle,
                usernameOrEmailField, passwordField,
                errorLabel, loginButton,
                registerLinkContainer
        );

        loginScene = new Scene(loginContainer, 1000, 700);
        String cssPath = getClass().getResource("/com/isychia/isychiachatapp/style.css").toExternalForm();
        loginScene.getStylesheets().add(cssPath);
    }

    private void handleLogin() {
        String identifier = usernameOrEmailField.getText().trim();
        String password = passwordField.getText();

        if (identifier.isEmpty() || password.isEmpty()) {
            showError("Please enter all fields");
            return;
        }

        User user = userService.loginUser(identifier, password);
        if (user != null) {
            // Login successful
            resetForm();
            if (loginCallback != null) {
                loginCallback.onLoginSuccess(user);
            }
        } else {
            // Login failed
            showError("Invalid username/email or password");
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    public void resetForm() {
        usernameOrEmailField.clear();
        passwordField.clear();
        errorLabel.setVisible(false);
    }

    public Scene getScene() {
        return loginScene;
    }

    public void setShowRegistrationScreen(Runnable showRegistrationScreen) {
        this.showRegistrationScreen = showRegistrationScreen;
    }

    public void setLoginCallback(LoginCallback loginCallback) {
        this.loginCallback = loginCallback;
    }

    // Callback interface
    public interface LoginCallback {
        void onLoginSuccess(User user);
    }
}