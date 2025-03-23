package com.isychia.isychiachatapp;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class RegisterUI {
    private Scene registrationScene;
    private TextField usernameField;
    private TextField emailField;
    private PasswordField passwordField;
    private PasswordField confirmPasswordField;
    private Label errorLabel;
    private UserService userService;

    // Callback
    private Runnable showLoginScreen;

    public RegisterUI(UserService userService) {
        this.userService = userService;
        createRegistrationScene();
    }

    private void createRegistrationScene() {
        VBox registrationContainer = new VBox(15);
        registrationContainer.getStyleClass().add("auth-container");
        registrationContainer.setAlignment(Pos.CENTER);

        // Logo
        Circle logo = new Circle(40);
        logo.setFill(Color.rgb(114, 137, 218));

        // Title
        Label title = new Label("Create an Account");
        title.getStyleClass().add("auth-title");

        // Form fields
        usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.getStyleClass().add("auth-field");

        emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.getStyleClass().add("auth-field");

        passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.getStyleClass().add("auth-field");

        confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm Password");
        confirmPasswordField.getStyleClass().add("auth-field");

        // Error message
        errorLabel = new Label("");
        errorLabel.getStyleClass().add("error-label");
        errorLabel.setVisible(false);

        // Register button
        Button registerButton = new Button("Register");
        registerButton.getStyleClass().add("auth-button");
        registerButton.setOnAction(e -> handleRegistration());

        // Login link
        HBox loginLinkContainer = new HBox();
        loginLinkContainer.setAlignment(Pos.CENTER);
        Label loginPrompt = new Label("Already have an account? ");
        loginPrompt.setStyle("-fx-text-fill: #B9BBBE");
        Label loginLink = new Label("Log In");
        loginLink.getStyleClass().add("auth-link");
        loginLink.setOnMouseClicked(e -> {
            if (showLoginScreen != null) {
                resetForm();
                showLoginScreen.run();
            }
        });
        loginLinkContainer.getChildren().addAll(loginPrompt, loginLink);

        registrationContainer.getChildren().addAll(
                logo, title,
                usernameField, emailField,
                passwordField, confirmPasswordField,
                errorLabel, registerButton,
                loginLinkContainer
        );

        registrationScene = new Scene(registrationContainer, 1000, 700);
        String cssPath = getClass().getResource("/com/isychia/isychiachatapp/style.css").toExternalForm();
        registrationScene.getStylesheets().add(cssPath);
    }

    private void handleRegistration() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Basic validation
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showError("Please fill in all fields");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError("Passwords don't match");
            return;
        }

        if (!email.contains("@") || !email.contains(".")) {
            showError("Please enter a valid email address");
            return;
        }

        if (password.length() < 6) {
            showError("Password must be at least 6 characters");
            return;
        }

        // Try to register
        boolean success = userService.registerUser(username, email, password);
        if (success) {
            // Registration successful - go to login
            resetForm();
            if (showLoginScreen != null) {
                showLoginScreen.run();
            }
        } else {
            showError("Username or email already exists");
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    public void resetForm() {
        usernameField.clear();
        emailField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        errorLabel.setVisible(false);
    }

    public Scene getScene() {
        return registrationScene;
    }

    public void setShowLoginScreen(Runnable showLoginScreen) {
        this.showLoginScreen = showLoginScreen;
    }
}