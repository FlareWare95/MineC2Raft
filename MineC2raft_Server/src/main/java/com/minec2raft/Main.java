package com.minec2raft;

import java.io.IOException;

public class Main {
    
    public static void main(String args[]) throws IOException {
        System.out.println("Initializing Server...");
        Server server = new Server();
        server.runServer();
        
    }
}
