package com.minec2raft.Commands;

import java.util.Arrays;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.minec2raft.Server.Server;

public class SendCommand implements CommandExecutor{
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        System.out.println(Arrays.toString(args));
        String userin = "";
        for(int i = 0; i < args.length; i++) {
            userin = args[i];
        }
        System.out.println("Command attempting to be sent: " + userin);
        sender.sendMessage(Server.commandHandler(userin));
        
        
        return false;
    }
}
