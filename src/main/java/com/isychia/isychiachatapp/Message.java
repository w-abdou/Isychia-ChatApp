package com.isychia.isychiachatapp;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;
import org.bson.Document;
import com.mongodb.client.MongoCollection;
import java.util.Date;

public class Message {
    private String messageID;
    private String senderID;
    private String receiverID; // Can be UserID or GroupID
    private String encryptedContent;
    private LocalDateTime timestamp;
    private boolean isDeleted;
    private boolean isRead;
    private String iv; // Initialization Vector for Encryption
    private String sender;
    private String receiver;

    private static final String AES_ALGORITHM = "AES/CBC/PKCS5Padding";
    private static SecretKey secretKey;

    // Constructor
    public Message(String sender, String receiver, String content, String senderID, String receiverID) throws Exception {
        this.messageID = UUID.randomUUID().toString();
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.timestamp = LocalDateTime.now();
        this.isDeleted = false;
        this.isRead = false;
        this.iv = generateIV();
        this.encryptedContent = encryptAndSendMessage(content);
        this.sender = sender;  // Set sender
        this.receiver = receiver; // Set receiver
    }


    // Setters & Getters
    public String getMessageID() {
        return messageID;
    }

    public String getSenderID() {
        return senderID;
    }

    public String getReceiverID() {
        return receiverID;
    }

    public String getEncryptedContent() {
        return encryptedContent;
    }

    public void setEncryptedContent(String encryptedContent) {
        this.encryptedContent = encryptedContent;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public String getIv() {
        return iv;
    }

    public void setIv(String iv) {
        this.iv = iv;
    }

    // Encrypt message content before sending
    public String encryptAndSendMessage(String content) throws Exception {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Message content cannot be empty.");
        }
        if (secretKey == null) {
            secretKey = generateAESKey();
        }

        // Encrypt the content
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        IvParameterSpec ivSpec = new IvParameterSpec(Base64.getDecoder().decode(iv));
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
        byte[] encryptedData = cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));
        String encryptedMessage = Base64.getEncoder().encodeToString(encryptedData);

        // Save to MongoDB (make sure MongoDBConnection is set up correctly)
        MongoDBConnection dbConnection = new MongoDBConnection();  // Assuming MongoDBConnection class exists
        MongoCollection<Document> messagesCollection = dbConnection.getDatabase().getCollection("messages");
        Document messageDoc = new Document("messageID", messageID)
                .append("sender", sender)
                .append("receiver", receiver)
                .append("message", encryptedMessage)
                .append("timestamp", new Date())  // Add timestamp
                .append("isDeleted", isDeleted)
                .append("isRead", isRead)
                .append("iv", iv);  // Save IV for decryption

        messagesCollection.insertOne(messageDoc); // Insert the message document into MongoDB

        return encryptedMessage;
    }

    // Decrypt received message
    public String decryptReceivedMessage( String encryptedContent, String iv) throws Exception {
        if (encryptedContent == null || encryptedContent.trim().isEmpty()) {
            throw new IllegalArgumentException("No encrypted message found.");
        }

        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        IvParameterSpec ivSpec = new IvParameterSpec(Base64.getDecoder().decode(iv));
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
        byte[] decryptedData = cipher.doFinal(Base64.getDecoder().decode(encryptedContent));

        return new String(decryptedData, StandardCharsets.UTF_8);
    }

    // Generate AES secret key
    private static SecretKey generateAESKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256);
        return keyGenerator.generateKey();
    }

    // Generate IV (Initialization Vector)
    private String generateIV() {
        byte[] ivBytes = new byte[16];
        new SecureRandom().nextBytes(ivBytes);
        return Base64.getEncoder().encodeToString(ivBytes);
    }

    // Convert message to MongoDB document
    public Document toDocument() {
        return new Document("messageID", messageID)
                .append("senderID", senderID)
                .append("receiverID", receiverID)
                .append("encryptedContent", encryptedContent)
                .append("timestamp", timestamp.toString())
                .append("isDeleted", isDeleted)
                .append("isRead", isRead)
                .append("iv", iv);
    }
}