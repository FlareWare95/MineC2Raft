package com.minec2raft;

import java.io.PrintStream;
import java.util.Scanner;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.io.BukkitObjectOutputStream;

import com.minec2raft.Commands.GetCd;
import com.minec2raft.Commands.GetDir;
import com.minec2raft.Commands.GetHelp;
import com.minec2raft.Commands.GetTargets;
import com.minec2raft.Commands.SendCommand;
import com.minec2raft.Commands.StartServer;
import com.minec2raft.Commands.StopServer;
import com.minec2raft.Commands.TestCommand;

import com.minec2raft.Server.Server;

//TODO - test new commands 
//TODO - alias tests
//TODO - test MULTIPLE PLAYERS!!!!
//TODO - CONVERT CLIENT CODE TO C OR LOWER FOR COOLNESS
public class MineBridge extends JavaPlugin{

    public static Server server;
    public static Scanner minecraftStream;

    @Override
    public void onEnable() {
        server = new Server();
        getCommand("test").setExecutor(new TestCommand());
        getCommand("startServer").setExecutor(new StartServer(this));
        getCommand("stopServer").setExecutor(new StopServer());
        getCommand("cmd").setExecutor(new SendCommand());
        getCommand("targets").setExecutor(new GetTargets());
        getCommand("dir").setExecutor(new GetDir());
        getCommand("cd").setExecutor(new GetCd());
        getCommand("c2help").setExecutor(new GetHelp());
        Bukkit.broadcast("MineC2raft Started.","bukkit.broadcast.user"); 
    }

    @Override
    public void onDisable() {
        getLogger().info("MineC2raft Disabled.");
    }

}
