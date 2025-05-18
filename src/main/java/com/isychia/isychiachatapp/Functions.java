package com.isychia.isychiachatapp;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

public class Functions implements AllFunctions {

    private final Map<String, ChatUpdateListener> listeners = new HashMap<>();

    @Override
    public String getId(String name) throws RemoteException {
        return name + "_id"; // example logic
    }


    @Override
    public void registerListener(String username, ChatUpdateListener listener) throws RemoteException {
        listeners.put(username, listener);
        System.out.println("✅ RMI Listener registered for user: " + username);


    }



    @Override
    public void notifyNewMessage(String receiverUsername, String senderUsername, String message) throws RemoteException {
        ChatUpdateListener listener = listeners.get(receiverUsername);
        if (listener != null) {
            listener.onNewMessage(senderUsername, message);
        }
    }
}
