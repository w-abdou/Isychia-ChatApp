package com.isychia.isychiachatapp;

import java.rmi.Remote;
import java.rmi.RemoteException;


public interface AllFunctions extends Remote {
    void registerClient(ChatUpdateListener listener) throws RemoteException;
    void notifyClients(String sender, String receiver) throws RemoteException;
}
