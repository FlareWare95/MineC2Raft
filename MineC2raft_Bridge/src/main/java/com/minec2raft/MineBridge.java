package com.minec2raft;

import java.io.PrintStream;
import java.util.Scanner;

import org.bukkit.plugin.java.JavaPlugin;

import com.minec2raft.Commands.SendCommand;
import com.minec2raft.Commands.StartServer;
import com.minec2raft.Commands.StopServer;
import com.minec2raft.Commands.TestCommand;
import com.minec2raft.Server.BroadcastPrintStream;
import com.minec2raft.Server.Server;

public class MineBridge extends JavaPlugin{

    public static Server server;
    public static Scanner minecraftStream;

     
    @Override
    public void onEnable() {
        PrintStream oldPrintStream = System.out;
        System.setOut(new BroadcastPrintStream(oldPrintStream));
        server = new Server();
        getCommand("test").setExecutor(new TestCommand());
        getCommand("startServer").setExecutor(new StartServer(this));
        getCommand("stopServer").setExecutor(new StopServer());
        getCommand("cmd").setExecutor(new SendCommand());
        System.out.println("MineC2raft Started."); 
    }

    @Override
    public void onDisable() {
        getLogger().info("MineC2raft Disconnected.");
    }

}
