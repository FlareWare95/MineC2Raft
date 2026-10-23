package com.minec2raft.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.minec2raft.MineBridge;
import com.minec2raft.Server.Server;

public class StopServer implements CommandExecutor{

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(MineBridge.server != null) {
            Server.stopServer();
            MineBridge.server = null;
            String quip = "";
            int i = (int)(10 * Math.random());
            switch (i) {
                case 0:
                    quip = "We hate to see you go...";
                    break;
                case 1:
                    quip = "We'll meet again soon, right?";
                    break;
                case 2:
                    quip = "It's ok, I'll wait for you here...";
                    break;
                case 3:
                    quip = "My battery is low and it's getting dark";
                    break;
                default:
                    quip = "Thanks for playing!";

            } 

            Bukkit.broadcastMessage(Server.BRAND_IDENTITY + "Server Stopped. " + quip);

        } else {
            sender.sendMessage("The server is already disabled!");
        }
        return true;
    }
    
}
