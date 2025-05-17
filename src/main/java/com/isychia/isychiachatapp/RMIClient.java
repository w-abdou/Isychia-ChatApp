package com.isychia.isychiachatapp;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RMIClient {
    private static AllFunctions stub;

    public static AllFunctions connect() {
        if (stub == null) {
            try {
                Registry r = LocateRegistry.getRegistry(1234);
                stub = (AllFunctions) r.lookup("Ma7shy");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return stub;
    }
}
