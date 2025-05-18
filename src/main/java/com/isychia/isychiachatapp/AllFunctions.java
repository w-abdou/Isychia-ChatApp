package com.isychia.isychiachatapp;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface AllFunctions extends Remote {

    String getId(String name) throws RemoteException;

    void registerListener(String username, ChatUpdateListener listener) throws RemoteException;

    void notifyNewMessage(String receiverUsername, String senderUsername, String message) throws RemoteException;


}



