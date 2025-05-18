package com.isychia.isychiachatapp;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import javafx.application.Platform;

public class ChatUpdateListenerImpl extends UnicastRemoteObject implements ChatUpdateListener {

    private final ChatInterface chatInterface;

    public ChatUpdateListenerImpl(ChatInterface chatInterface) throws RemoteException {
        this.chatInterface = chatInterface;
    }


    @Override
    public void onNewMessage(String senderUsername, String messageText) throws RemoteException {
        Platform.runLater(() -> {
            chatInterface.handleIncomingMessageNotification(senderUsername, messageText);
        });


    }
}
