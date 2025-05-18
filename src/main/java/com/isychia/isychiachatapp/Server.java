package com.isychia.isychiachatapp;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server {
    public static void main(String[] args) {
        try {
            Functions f = new Functions();
            AllFunctions stub = (AllFunctions) UnicastRemoteObject.exportObject(f, 0);
            Registry r = LocateRegistry.createRegistry(1234);
            r.rebind("ChatService", stub);
            System.out.println("RMI Server is running...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

