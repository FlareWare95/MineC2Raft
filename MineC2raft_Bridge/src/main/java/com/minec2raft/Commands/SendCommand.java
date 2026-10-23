package com.minec2raft.Commands;

import java.util.Arrays;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;


import com.minec2raft.Server.Server;

public class SendCommand implements CommandExecutor{
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String userin = "";


        for(int i = 0; i < args.length - 1; i++) {
            System.out.println(args[i]);
            userin += " " + args[i];
        }
        System.out.println(Arrays.toString(args));
        System.out.println(userin);
        try {
            sender.sendMessage(Server.commandHandler(args[0], args[1], sender));
        } catch(Exception e) {
            sender.sendMessage("Invalid!");
        }
        
        return true;        
    }
}
