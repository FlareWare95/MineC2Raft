package com.minec2raft;

import org.bukkit.plugin.java.JavaPlugin;

public class MineBridge extends JavaPlugin{

    
    private BridgeConnection bridge;

    
    @Override
    public void onEnable() {
        getLogger().info("MineC2raft enabled. Connecting...");
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
