package com.minec2raft.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.minec2raft.Server.Server;

public class GetCredits implements CommandExecutor{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage("\n" + Server.GLITCH + "a" + Server.RESET + "░▒▓ MINEC2RAFT ▓▒░" + Server.GLITCH + "a" + Server.RESET + "\n" 
                        + "Version: " + Server.VERSION_NUM 
                        + "\nWritten by  for RIT's Red Team.\n" 
                        + "For fun too lowk...\n\n" 
                        + "A dud?\n/give @p oak_sapling 100");
        return true;
    }
}
