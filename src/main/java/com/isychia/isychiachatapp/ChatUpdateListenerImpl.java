package com.isychia.isychiachatapp;
import com.isychia.isychiachatapp.ChatInterface;


import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ChatUpdateListenerImpl extends UnicastRemoteObject implements ChatUpdateListener {

    private final ChatInterface chatInterface;

    protected ChatUpdateListenerImpl(ChatInterface chatInterface) throws RemoteException {
        super();
        this.chatInterface = chatInterface;
    }

    @Override
    public void onNewMessage(String sender, String receiver) throws RemoteException {
        chatInterface.handleNewMessageNotification(sender, receiver);
    }
}
