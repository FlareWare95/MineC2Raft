package com.minec2raft.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;


import com.minec2raft.Server.Server;

public class SendCommand implements CommandExecutor{
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String userin = "";
        for(int i = 0; i < args.length; i++) {
            userin = args[i];
        }
        String target = args[args.length - 1];
        try {
            sender.sendMessage(Server.commandHandler(userin, args[args.length - 1], sender));
        } catch(Exception e) {
            sender.sendMessage("Invalid!");
        }
        
        
        
        return false;
    }
}
