package com.minec2raft.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.minec2raft.Server.Server;

public class GetHelp implements CommandExecutor{

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(Server.commandHandler("coms"));
        return true;
    }
    
}
