package com.minec2raft;

import org.bukkit.plugin.java.JavaPlugin;

import com.minec2raft.Server.Server;
import com.minec2raft.Server.Commands.TestCommand;

public class MineBridge extends JavaPlugin{

    
    private BridgeConnection bridge;
    public static Server server;

    
    @Override
    public void onEnable() {
        Server server = new Server();
        bridge = new BridgeConnection(this);

        this.getCommand("test").setExecutor(new TestCommand());
        bridge.start();
        System.out.println("MineC2raft Connected.");
    }

    @Override
    public void onDisable() {
        if(bridge != null) {
            bridge.shutdown();
        }
        getLogger().info("MineC2raft Disconnected.");
    }

}
