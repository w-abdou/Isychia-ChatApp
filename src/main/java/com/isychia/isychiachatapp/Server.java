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
            r.bind("Ma7shy", stub);
            System.out.println("Server is ready.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
