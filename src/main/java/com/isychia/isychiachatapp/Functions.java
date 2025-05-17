package com.isychia.isychiachatapp;

import java.rmi.RemoteException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class Functions implements AllFunctions {
    private final List<ChatUpdateListener> clients = new CopyOnWriteArrayList<>();

    @Override
    public void registerClient(ChatUpdateListener listener) throws RemoteException {
        clients.add(listener);
    }

    @Override
    public void notifyClients(String sender, String receiver) throws RemoteException {
        for (ChatUpdateListener client : clients) {
            client.onNewMessage(sender, receiver);
        }
    }
}

