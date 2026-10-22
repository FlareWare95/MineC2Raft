package com.minec2raft.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class GetHelp implements CommandExecutor{

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage("               ********** MineC2raft Commands ***********\n" 
                            + "/targets: displays all connected clients.\n"
                            + "/cd [client]: Change directory of specified [client].\n"
                            + "/cmd [command] [client]: Sends [command] to the specified [client].\n"
                            + "/dir [client]: List current directory of specified [client].\n"
                            + "       ******************************************\n\n" 
                            + "lowk you can use a remote terminal and send shii \n" 
                            + "Thats all you need to know foo\n");
        return true;
    }
    
}
