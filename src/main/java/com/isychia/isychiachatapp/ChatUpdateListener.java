package com.isychia.isychiachatapp;



import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ChatUpdateListener extends Remote {
    void onNewMessage(String sender, String receiver) throws RemoteException;
}
