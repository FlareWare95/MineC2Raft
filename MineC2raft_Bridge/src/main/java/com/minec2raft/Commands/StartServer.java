package com.minec2raft.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import com.minec2raft.MineBridge;
import com.minec2raft.Server.Server;

public class StartServer implements CommandExecutor{
    private JavaPlugin plugin;

    public StartServer(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        
        try {
            if(Server.isEnabled()) {
                MineBridge.server.runServer(plugin);
            } else {
                sender.sendMessage("The server has already been enabled!");
            }
            return true;
        } catch(Exception e) {
            System.out.println("Error Starting Server!" + e);
            return false;
        }
        
    }
    
}
