package com.isychia.isychiachatapp;

import javafx.application.Platform;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.input.KeyCode;
import java.util.List;
import java.util.stream.Collectors;
import java.util.function.Function;
import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;
import java.util.Set;
import java.util.HashSet;
import com.mongodb.client.FindIterable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import com.isychia.isychiachatapp.UserService;
import org.bson.Document;
import com.mongodb.client.MongoCollection;

public class ChatInterface {
    private Scene chatScene;
    private BorderPane mainLayout;
    private ListView<HBox> chatList;
    private VBox messagesContainer;
    private ScrollPane scrollPane;
    private TextField messageInput;
    private VBox chatWindow, welcomePanel;
    private String currentChatUser;
    private User currentUser;
    private UserService userService;
    private Runnable onOpenProfileAction;
    private Thread messagePollingThread;
    private boolean pollingActive = false;
    private Set<String> displayedMessageIds = new HashSet<>();
    private final Map<String, VBox> chatHistories = new HashMap<>();
    private AllFunctions rmiStub;



    // Callback
    private Runnable onLogoutAction;


    private final String[] statusOptions = {"Online", "Away", "Busy"};
    private final Color[] avatarColors = {
            Color.rgb(66, 133, 244),   // Blue
            Color.rgb(219, 68, 55),    // Red
            Color.rgb(244, 180, 0),    // Yellow
            Color.rgb(15, 157, 88),    // Green
            Color.rgb(171, 71, 188)    // Purple
    };

    public ChatInterface(UserService userService) {
        this.userService = userService;
        createChatScene();
        connectToRMIServer();

    }

    public void initialize(User user) {
        this.currentUser = user;
        initializeChatData();
    }

    private void createChatScene() {
        mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: #36393F;");

        chatScene = new Scene(mainLayout, 1000, 700);
        String cssPath = getClass().getResource("/com/isychia/isychiachatapp/style.css").toExternalForm();
        chatScene.getStylesheets().add(cssPath);
    }

    private void initializeChatData() {

        // Get all registered users
        List<User> userList = userService.getAllUsers();

        for (User user : userList) {
            if (!user.getUsername().equals(currentUser.getUsername())) {
                String chatPartnerID = user.getUsername();

                // Initialize an empty chat history
                VBox chatHistory = createNewChatHistory();
                chatHistories.put(chatPartnerID, chatHistory);

                // Pre-load messages from the database
                loadMessagesFromDatabase(chatPartnerID);
            }
        }

        // Set up the chat UI after loading messages
        setupChatUI();
    }

    private void setupChatUI() {
        // App header with logo and status
        HBox header = createHeader();
        mainLayout.setTop(header);



        // Chat List (Left Panel)
        chatList = new ListView<>();
        List<User> userList = userService.getAllUsers(); // ✅ get the list
        Map<String, User> allUsers = new HashMap<>();    // ✅ create the map
        for (User user : userList) {
            allUsers.put(user.getUsername(), user);      // ✅ fill the map using username as key
        }

        for (User user : allUsers.values()) {
            if (!user.getUsername().equals(currentUser.getUsername())) {
                chatList.getItems().add(createContactListItem(user.getUsername()));
            }
        }

        chatList.setCellFactory(param -> new ListCell<HBox>() {
            @Override
            protected void updateItem(HBox item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setGraphic(item);
                }
                setPadding(new Insets(5, 0, 5, 0));
            }
        });

        chatList.setStyle("-fx-background-color: #2C2F33; -fx-border-color: #23272A; -fx-border-width: 0 1 0 0;");
        chatList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                // Extract username from HBox
                Label nameLabel = (Label) ((VBox)newVal.getChildren().get(1)).getChildren().get(0);
                currentChatUser = nameLabel.getText();
                showChatWindow();
                updateChatHistory();
            }
        });

        // Search box for contacts
        TextField searchField = new TextField();
        searchField.setPromptText("Search contacts...");
        searchField.setStyle("-fx-background-color: #40444B; -fx-text-fill: white; -fx-prompt-text-fill: #72767D;");
        searchField.getStyleClass().add("search-field");

        // Chat Window Panel
        chatWindow = new VBox(0);
        chatWindow.setStyle("-fx-background-color: #36393F;");

        // Messages area
        messagesContainer = new VBox(10);
        messagesContainer.setPadding(new Insets(15));
        messagesContainer.setStyle("-fx-background-color: #36393F;");

        scrollPane = new ScrollPane(messagesContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background-color: #36393F; -fx-background: #36393F; -fx-border-width: 0;");


        // Message input area
        HBox inputArea = createMessageInputArea();

        chatWindow.getChildren().addAll(createChatHeader(), scrollPane, inputArea);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        // Welcome Screen
        welcomePanel = createWelcomePanel();

        chatScene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                showWelcomePanel();
            }
        });

        // Left sidebar with search and contacts
        VBox leftPanel = new VBox(10);
        leftPanel.setPadding(new Insets(0, 0, 10, 0));

        // Add logout button
        Button logoutButton = new Button("Logout");
        logoutButton.getStyleClass().add("chat-header-button");
        logoutButton.setOnAction(e -> {
            if (onLogoutAction != null) {
                onLogoutAction.run();
            }
        });

        // User info panel
        HBox userInfoPanel = new HBox(10);
        userInfoPanel.setPadding(new Insets(10));
        userInfoPanel.setAlignment(Pos.CENTER_LEFT);
        userInfoPanel.setStyle("-fx-background-color: #292B2F;");

        StackPane userAvatar = createAvatar(currentUser.getUsername());
        Label userName = new Label(currentUser.getUsername());
        userName.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

        userInfoPanel.getChildren().addAll(userAvatar, userName, new Pane(), logoutButton);
        HBox.setHgrow(userName, Priority.ALWAYS);

        leftPanel.getChildren().addAll(userInfoPanel, searchField, chatList);
        leftPanel.setStyle("-fx-background-color: #2C2F33;");
        leftPanel.setPrefWidth(250);
        VBox.setMargin(searchField, new Insets(10, 10, 5, 10));
        VBox.setVgrow(chatList, Priority.ALWAYS);

        // Set components to the layout
        mainLayout.setLeft(leftPanel);
        mainLayout.setCenter(welcomePanel);
    }

    private HBox createContactListItem(String username) {
        // Avatar (circle with first letter)
        StackPane avatar = createAvatar(username);

        // User info (name and status)
        Label nameLabel = new Label(username);
        nameLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

        String status = statusOptions[new Random().nextInt(statusOptions.length)];
        Label statusLabel = new Label(status);
        statusLabel.setStyle("-fx-text-fill: #B9BBBE; -fx-font-size: 11px;");

        VBox userInfo = new VBox(2);
        userInfo.getChildren().addAll(nameLabel, statusLabel);
        // Contact list item
        HBox contactItem = new HBox(10);
        contactItem.setAlignment(Pos.CENTER_LEFT);
        contactItem.setPadding(new Insets(5, 15, 5, 15));
        contactItem.getChildren().addAll(avatar, userInfo);

        contactItem.setOnMouseClicked(event -> {
            currentChatUser = username;
            updateChatHistory();
            showChatWindow();

            messageInput.requestFocus();
        });
        return contactItem;
    }

    private StackPane createAvatar(String username) {
        Circle avatarBg = new Circle(20);
        avatarBg.setFill(avatarColors[Math.abs(username.hashCode()) % avatarColors.length]);

        Text initial = new Text(username.substring(0, 1).toUpperCase());
        initial.setFill(Color.WHITE);
        initial.setFont(Font.font("System", FontWeight.BOLD, 16));

        StackPane avatar = new StackPane();
        avatar.getChildren().addAll(avatarBg, initial);
        return avatar;
    }

    private HBox createHeader() {
        Label appName = new Label("IsychiaChat");
        appName.setFont(Font.font("System", FontWeight.BOLD, 18));
        appName.setStyle("-fx-text-fill: white;");

        // Simple logo (circle for now)
        Circle logo = new Circle(15);
        logo.setFill(Color.rgb(114, 137, 218));

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(10, 15, 10, 15));
        header.setStyle("-fx-background-color: #23272A;");
        header.getChildren().addAll(logo, appName);

        return header;
    }

    private HBox createChatHeader() {
        Label nameLabel = new Label("");
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        nameLabel.setStyle("-fx-text-fill: white;");

    /**

       Button callButton = new Button("Call");
        callButton.getStyleClass().add("chat-header-button");

        Button videoButton = new Button("Video");
        videoButton.getStyleClass().add("chat-header-button");

    **/

        HBox chatHeader = new HBox(15);
        chatHeader.setAlignment(Pos.CENTER_LEFT);
        chatHeader.setPadding(new Insets(15));
        chatHeader.setStyle("-fx-background-color: #2F3136; -fx-border-color: #23272A; -fx-border-width: 0 0 1 0;");
        chatHeader.getChildren().addAll(nameLabel, new Pane()  /** , callButton, videoButton **/   );
        HBox.setHgrow(nameLabel, Priority.ALWAYS);

        return chatHeader;
    }

    private HBox createMessageInputArea() {
        messageInput = new TextField();
        messageInput.setPromptText("Type a message...");
        messageInput.setStyle("-fx-background-color: #40444B; -fx-text-fill: white; -fx-prompt-text-fill: #72767D;");
        messageInput.getStyleClass().add("message-input");
        messageInput.setOnAction(e -> sendMessage());

        Button attachButton = new Button("+");
        attachButton.getStyleClass().add("rounded-button");

        Button sendButton = new Button("Send");
        sendButton.getStyleClass().add("send-button");
        sendButton.setOnAction(e -> sendMessage());

        HBox inputBox = new HBox(10);
        inputBox.setAlignment(Pos.CENTER);
        inputBox.setPadding(new Insets(10, 15, 15, 15));
        inputBox.setStyle("-fx-background-color: #36393F;");
        inputBox.getChildren().addAll(attachButton, messageInput, sendButton);
        HBox.setHgrow(messageInput, Priority.ALWAYS);

        return inputBox;
    }

    private VBox createWelcomePanel() {
        VBox panel = new VBox();
        panel.setAlignment(Pos.CENTER);
        panel.setSpacing(15);
        panel.setPadding(new Insets(50));
        panel.setStyle("-fx-background-color: #36393F;");

        Circle welcomeIcon = new Circle(50);
        welcomeIcon.setFill(Color.rgb(114, 137, 218));

        Label welcomeHeader = new Label("Welcome to IsychiaChat");
        welcomeHeader.setFont(Font.font("System", FontWeight.BOLD, 24));
        welcomeHeader.setStyle("-fx-text-fill: white;");

        Label welcomeText = new Label("Select a contact from the list to start chatting");
        welcomeText.setStyle("-fx-text-fill: #B9BBBE; -fx-font-size: 14px;");

        panel.getChildren().addAll(welcomeIcon, welcomeHeader, welcomeText);

        return panel;
    }

    private void showWelcomePanel() {
        // Clear the current chat selection if any
        currentChatUser = null;

        // Replace the chat window with the welcome panel in the center area
        mainLayout.setCenter(welcomePanel);

        stopMessagePolling(); // 👈 Stop polling when chat is closed

    }

    private VBox createNewChatHistory() {
        VBox history = new VBox(10);
        history.setPadding(new Insets(10));
        return history;
    }


    private void updateChatHistory() {
        if (currentChatUser != null) {
            // Update chat header
            HBox chatHeader = (HBox) chatWindow.getChildren().get(0);
            Label nameLabel = (Label) chatHeader.getChildren().get(0);
            nameLabel.setText(currentChatUser);

            // Load messages from DB if not already loaded
            if (!chatHistories.containsKey(currentChatUser)) {
                loadMessagesFromDatabase(currentChatUser);
            }

            // Clear the message container and load ONLY this user's messages
            messagesContainer.getChildren().clear();
            if (chatHistories.containsKey(currentChatUser)) {
                messagesContainer.getChildren().addAll(chatHistories.get(currentChatUser).getChildren());
            }

            scrollPane.setVvalue(1.0); // scroll to bottom
            startMessagePolling();
        }
    }




    private void loadMessagesFromDatabase(String chatPartnerID) {
        try {
            MongoDBConnection dbConnection = new MongoDBConnection();
            MongoCollection<Document> messagesCollection = dbConnection.getDatabase().getCollection("messages");

            String currentUserID = currentUser.getUsername();

            Document query = new Document("$or", Arrays.asList(
                    new Document("sender", currentUserID).append("receiver", chatPartnerID),
                    new Document("sender", chatPartnerID).append("receiver", currentUserID)
            ));

            FindIterable<Document> results = messagesCollection.find(query).sort(new Document("timestamp", 1));

            // Create new container if it doesn't exist
            chatHistories.putIfAbsent(chatPartnerID, createNewChatHistory());
            VBox chatHistory = chatHistories.get(chatPartnerID);

            for (Document doc : results) {
                String messageId = doc.getObjectId("_id").toHexString(); // Unique message ID
                if (displayedMessageIds.contains(messageId)) {
                    continue; // Already displayed
                }

                // Decrypt the message
                String sender = doc.getString("sender");
                String receiver = doc.getString("receiver");
                String encryptedContent = doc.getString("message");
                String iv = doc.getString("iv");

                Message message = new Message(sender, receiver, encryptedContent, iv, null);
                String decryptedMessage = message.decryptReceivedMessage();

                // Create message bubble
                HBox messageBox = new HBox();
                messageBox.setPadding(new Insets(5, 0, 5, 0));

                VBox messageBubble = new VBox(3);
                messageBubble.setMaxWidth(400);
                messageBubble.setStyle("-fx-padding: 10 15 10 15; -fx-background-radius: 18;");

                Label messageText = new Label(decryptedMessage);
                messageText.setWrapText(true);

                Date timestamp = doc.getDate("timestamp");
                String formattedTime = new SimpleDateFormat("HH:mm").format(timestamp);
                Label timeLabel = new Label(formattedTime);
                timeLabel.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.7); -fx-font-size: 10px;");

                messageBubble.getChildren().addAll(messageText, timeLabel);

                if (sender.equals(currentUserID)) {
                    messageBox.setAlignment(Pos.CENTER_RIGHT);
                    messageBubble.setStyle(messageBubble.getStyle() + "-fx-background-color: #7289DA;");
                    messageText.setStyle("-fx-text-fill: white;");
                } else {
                    messageBox.setAlignment(Pos.CENTER_LEFT);
                    messageBubble.setStyle(messageBubble.getStyle() + "-fx-background-color: #40444B;");
                    messageText.setStyle("-fx-text-fill: white;");
                }

                messageBox.getChildren().add(messageBubble);
                chatHistory.getChildren().add(messageBox);

                // If this is the active chat, also add to messagesContainer
                if (chatPartnerID.equals(currentChatUser)) {
                    javafx.application.Platform.runLater(() -> messagesContainer.getChildren().add(messageBox));
                }

                // Mark as displayed
                displayedMessageIds.add(messageId);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    private void sendMessage() {
        String message = messageInput.getText().trim();
        if (!message.isEmpty() && currentChatUser != null) {
            String currentUserID = currentUser.getUsername();

            HBox messageBox = new HBox();
            messageBox.setAlignment(Pos.CENTER_RIGHT);
            messageBox.setPadding(new Insets(5, 0, 5, 0));

            VBox messageBubble = new VBox(3);
            messageBubble.setStyle("-fx-background-color: #7289DA; -fx-background-radius: 18; -fx-padding: 10 15 10 15;");
            messageBubble.setMaxWidth(400);

            Label messageText = new Label(message);
            messageText.setWrapText(true);
            messageText.setStyle("-fx-text-fill: white;");

            String timestamp = new SimpleDateFormat("HH:mm").format(new Date());
            Label timeLabel = new Label(timestamp);
            timeLabel.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.7); -fx-font-size: 10px;");

            messageBubble.getChildren().addAll(messageText, timeLabel);
            messageBox.getChildren().add(messageBubble);

            messagesContainer.getChildren().add(messageBox);
            scrollPane.setVvalue(1.0);

            try {
                Message newMessage = new Message(currentUserID, currentChatUser, message, null, null);
                newMessage.encryptAndSendMessage(message);

                if (rmiStub != null) {
                    rmiStub.notifyNewMessage(currentChatUser, currentUser.getUsername(), message);

                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            messageInput.clear();
        }
    }





    private void addMockReply(String replyText) {
        // Create message bubble for the reply
        HBox messageBox = new HBox(10);
        messageBox.setAlignment(Pos.CENTER_LEFT);
        messageBox.setPadding(new Insets(5, 0, 5, 0));

        // Add avatar
        StackPane avatar = createAvatar(currentChatUser);
        avatar.setScaleX(0.7);
        avatar.setScaleY(0.7);

        VBox messageBubble = new VBox(3);
        messageBubble.setStyle("-fx-background-color: #40444B; -fx-background-radius: 18; -fx-padding: 10 15 10 15;");
        messageBubble.setMaxWidth(400);

        Label messageText = new Label(replyText);
        messageText.setWrapText(true);
        messageText.setStyle("-fx-text-fill: white;");

        String timestamp = new SimpleDateFormat("HH:mm").format(new Date());
        Label timeLabel = new Label(timestamp);
        timeLabel.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.7); -fx-font-size: 10px;");

        messageBubble.getChildren().addAll(messageText, timeLabel);
        messageBox.getChildren().addAll(avatar, messageBubble);

        // Add to the chat history
        chatHistories.get(currentChatUser).getChildren().add(messageBox);

        // Directly append the reply message to the messagesContainer
        messagesContainer.getChildren().add(messageBox);

        // Scroll to the most recent message
        scrollPane.setVvalue(1.0);
    }

    private void showChatWindow() {
        mainLayout.setCenter(chatWindow);
    }

    public Scene getScene() {
        return chatScene;
    }

    public void setOnLogoutAction(Runnable action) {
        this.onLogoutAction = action;
    }



    private void startMessagePolling() {
        stopMessagePolling(); // Stop any existing thread

        pollingActive = true;
        messagePollingThread = new Thread(() -> {
            while (pollingActive) {
                try {
                    Thread.sleep(2000); // Poll every 2 seconds

                    if (currentChatUser != null) {
                        // Load only new messages from DB and update UI
                        loadMessagesFromDatabase(currentChatUser);

                        javafx.application.Platform.runLater(() -> {
                            // ONLY scroll, no need to clear and reload
                            scrollPane.setVvalue(1.0);
                        });
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });

        messagePollingThread.setDaemon(true);
        messagePollingThread.start();
    }


private void stopMessagePolling() {
    pollingActive = false;
    if (messagePollingThread != null && messagePollingThread.isAlive()) {
        messagePollingThread.interrupt();
    }
}
    public void registerForRMINotifications() {
        try {
            AllFunctions stub = RMIClient.connect();
            if (stub != null) {
                ChatUpdateListenerImpl listener = new ChatUpdateListenerImpl(this);
                stub.registerListener(currentUser.getUsername(), listener);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void connectToRMIServer() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1234); // match your server settings
            AllFunctions stub = (AllFunctions) registry.lookup("ChatService");

            ChatUpdateListener listener = new ChatUpdateListenerImpl(this);
            stub.registerListener(currentUser.getUsername(), listener);

            // Save the stub to use it when sending messages
            this.rmiStub = stub;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void handleIncomingMessageNotification(String senderUsername, String messageText) {
        if (senderUsername.equals(currentChatUser)) {
            HBox messageBox = new HBox();
            messageBox.setAlignment(Pos.CENTER_LEFT);
            messageBox.setPadding(new Insets(5, 0, 5, 0));

            VBox messageBubble = new VBox(3);
            messageBubble.setStyle("-fx-background-color: #e0e0e0; -fx-background-radius: 18; -fx-padding: 10 15 10 15;");
            messageBubble.setMaxWidth(400);

            Label messageLabel = new Label(messageText);
            messageLabel.setWrapText(true);
            messageLabel.setStyle("-fx-text-fill: black;");

            String timestamp = new SimpleDateFormat("HH:mm").format(new Date());
            Label timeLabel = new Label(timestamp);
            timeLabel.setStyle("-fx-text-fill: rgba(0, 0, 0, 0.5); -fx-font-size: 10px;");

            messageBubble.getChildren().addAll(messageLabel, timeLabel);
            messageBox.getChildren().add(messageBubble);

            messagesContainer.getChildren().add(messageBox);
            scrollPane.setVvalue(1.0);
        }
    }


}
