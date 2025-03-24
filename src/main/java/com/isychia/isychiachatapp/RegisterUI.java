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
        emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.setPrefWidth(280);


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

        emailField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                if (isValidEmail(newValue)) {
                    emailField.setStyle("-fx-border-color: green;");
                } else {
                    emailField.setStyle("-fx-border-color: red;");
                }
            } else {
                emailField.setStyle("");
            }
        });


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

        // Input validation
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showError("All fields are required");
            return;
        }

        // Email validation
        if (!isValidEmail(email)) {
            showError("Please enter a valid email address");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError("Passwords don't match");
            return;
        }

        // Registration logic
        boolean registrationSuccess = userService.registerUser(username, email, password);

        if (registrationSuccess) {
            // Registration was successful
            // momken ne3ml otp!??

            resetForm();
            showLoginScreen.run();
        } else {
            showError("Username or email already exists");
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    private boolean isValidUsername(String username) {
        String usernameRegex = "^[a-zA-Z0-9_]+$";
        return username != null && username.matches(usernameRegex);
    }

    private boolean isValidPassword(String password) {
        String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/? ])(?=\\S+$).{6,}$";
        return password != null && password.matches(passwordRegex);
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email != null && email.matches(emailRegex);
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