package com.isychia.isychiachatapp;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RMIClient {
    public static AllFunctions connect() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1234);
            return (AllFunctions) registry.lookup("ChatService");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
