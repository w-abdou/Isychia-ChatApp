package com.isychia.isychiachatapp;


import java.util.List;
import java.util.ArrayList;
import org.bson.Document;
import com.mongodb.client.MongoCollection;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import com.isychia.isychiachatapp.UserService;

public class LoginUI {
    private Scene loginScene;
    private TextField usernameOrEmailField;
    private PasswordField passwordField;
    private Label errorLabel;
    private UserService userService;

    private Runnable showRegistrationScreen;
    private LoginCallback loginCallback;

    private MongoCollection<Document> userCollection;



    public LoginUI(UserService userService) {
        this.userService = userService;
        createLoginScene();
    }



    private void createLoginScene() {
        VBox loginContainer = new VBox(20);
        loginContainer.getStyleClass().add("auth-container");
        loginContainer.setAlignment(Pos.CENTER);

        // logo
        Circle logo = new Circle(50);
        logo.setFill(Color.rgb(114, 137, 218));

        // title
        Label title = new Label("IsychiaChat");
        title.getStyleClass().add("auth-title");

        Label subtitle = new Label("Log in to your account");
        subtitle.getStyleClass().add("auth-subtitle");

        // form fields
        usernameOrEmailField = new TextField();
        usernameOrEmailField.setPromptText("Email or Username");
        usernameOrEmailField.getStyleClass().add("auth-field");

        passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.getStyleClass().add("auth-field");

        // error message
        errorLabel = new Label("");
        errorLabel.getStyleClass().add("error-label");
        errorLabel.setVisible(false);

        // login button
        Button loginButton = new Button("Log In");
        loginButton.getStyleClass().add("auth-button");
        loginButton.setOnAction(e -> handleLogin());

        // reg link
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
        // clear previous errors
        errorLabel.setText("");

        // get input values from form fields
        String username = usernameOrEmailField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Username and password are required");
            return;
        }

        boolean loginSuccessful = userService.login(username, password);
        if (loginSuccessful) {
            User user = userService.getCurrentUser();
            if (loginCallback != null) {
                loginCallback.onLoginSuccess(user);
            }
        } else {
            showError("Invalid username or password");
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

    public List<User> getAllUsers() {

        List<User> users = new ArrayList<>();
        for (Document doc : userCollection.find()) {
            users.add(User.fromDocument(doc));
        }
        return users;
    }


    public interface LoginCallback {
        void onLoginSuccess(User user);
    }
}