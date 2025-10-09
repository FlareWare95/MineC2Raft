package com.minec2raft;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import org.bukkit.Bukkit;

public class BridgeConnection extends Thread{
    private final MineBridge plugin;
    private Socket socket;
    private BufferedReader reader;
    



    public BridgeConnection(MineBridge plugin) {
        this.plugin = plugin;
    }


    @Override
    public void run() {

        try {
            socket = new Socket("localhost", 5050);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String line;
            while((line = reader.readLine()) != null) {
                String msg = line;
                Bukkit.getScheduler().runTask(plugin, () -> Bukkit.broadcastMessage("§b[MineC2raft]§r: " + msg));
            }
        } catch(Exception e) {
            System.out.println("BridgeConnection run() error!");
        }
    }

    public void shutdown() {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch( IOException ignored) {

        }
    }
}
