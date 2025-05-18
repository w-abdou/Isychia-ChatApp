package com.isychia.isychiachatapp;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

import org.bson.Document;


public class Message {
    private String messageID;
    private String senderID;
    private String receiverID;
    private String encryptedContent;
    private LocalDateTime timestamp;
    private boolean isDeleted;
    private boolean isRead;
    private String iv;
    private String sender;
    private String receiver;

    private static final String AES_ALGORITHM = "AES/CBC/PKCS5Padding";
    private static SecretKey secretKey;



    // Normal Constructor for sending messages
    public Message(String sender, String receiver, String content, String senderID, String receiverID) throws Exception {
        this.messageID = UUID.randomUUID().toString();
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.timestamp = LocalDateTime.now();
        this.isDeleted = false;
        this.isRead = false;
        this.iv = generateIV();
        this.sender = sender;
        this.receiver = receiver;
        this.encryptedContent = encryptAndSendMessage(content);


    }

    // Constructor to load mn el doc in db
    public Message(Document doc) {
        this.messageID = doc.getString("messageID");
        this.senderID = doc.getString("senderID");
        this.receiverID = doc.getString("receiverID");
        this.encryptedContent = doc.getString("encryptedContent");
        this.timestamp = LocalDateTime.parse(doc.getString("timestamp"));
        this.isDeleted = doc.getBoolean("isDeleted", false);
        this.isRead = doc.getBoolean("isRead", false);
        this.iv = doc.getString("iv");
        this.sender = doc.getString("sender");
        this.receiver = doc.getString("receiver");
    }

    // Encrypt content
    public String encryptAndSendMessage(String content) throws Exception {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Message content cannot be empty.");
        }

        if (secretKey == null) {
            secretKey = generateAESKey();
        }

        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        IvParameterSpec ivSpec = new IvParameterSpec(Base64.getDecoder().decode(iv));
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
        byte[] encryptedData = cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedData);
    }

    //Decrypt
    public String decryptReceivedMessage() throws Exception {
        if (encryptedContent == null || encryptedContent.trim().isEmpty()) {
            throw new IllegalArgumentException("No encrypted message found.");
        }

        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        IvParameterSpec ivSpec = new IvParameterSpec(Base64.getDecoder().decode(iv));
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
        byte[] decryptedData = cipher.doFinal(Base64.getDecoder().decode(encryptedContent));
        return new String(decryptedData, StandardCharsets.UTF_8);
    }

    private static SecretKey generateAESKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256);
        return keyGenerator.generateKey();
    }

    private String generateIV() {
        byte[] ivBytes = new byte[16];
        new SecureRandom().nextBytes(ivBytes);
        return Base64.getEncoder().encodeToString(ivBytes);
    }

    // Convert todocument
    public Document toDocument() {
        return new Document("messageID", messageID)
                .append("senderID", senderID)
                .append("receiverID", receiverID)
                .append("encryptedContent", encryptedContent)
                .append("timestamp", timestamp.toString())
                .append("isDeleted", isDeleted)
                .append("isRead", isRead)
                .append("iv", iv)
                .append("sender", sender)
                .append("receiver", receiver);
    }

    // Getters
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

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public boolean isRead() {
        return isRead;
    }

    public String getIv() {
        return iv;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }
}
