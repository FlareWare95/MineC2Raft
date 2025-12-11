package com.minec2raft.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.minec2raft.Server.Server;

public class GetCd implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String userin = "";
        for(int i = 0; i < args.length; i++) {
            userin = args[i];
        }
        sender.sendMessage(Server.commandHandler("cd " + userin));
        return true;
    }
    
}   
