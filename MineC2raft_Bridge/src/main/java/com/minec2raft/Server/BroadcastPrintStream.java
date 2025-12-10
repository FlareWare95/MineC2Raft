package com.minec2raft.Server;

import java.io.PrintStream;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class BroadcastPrintStream extends PrintStream{

    public BroadcastPrintStream(PrintStream original) {
        super(original);
    }
    
    /**
     * Overrides the regular println, so that it broadcasts to all players (CHANGE THIS AT SOME POINT SO THAT IT BECOMES PER CLIENT)
     */
    @Override
    public void println(String x) {
        super.println(x);

        for(Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage("§3[MineC2Raft] §f" + x);
        }
        // TODO - change println to handle both all clents OR single clients depending on how this is called
    }

    public static void println(String x, boolean addHeader) {

        for(Player p : Bukkit.getOnlinePlayers()) {
            if(addHeader) {
                p.sendMessage("§3[MineC2Raft] §f" + x);
            } else {
                p.sendMessage(x);
            }
            
        }
    }

    
    
}
