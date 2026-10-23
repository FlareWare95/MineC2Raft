package com.minec2raft.Server;

/**
 * Created to keep track of each individual client's data for use in the linux/windows arrays.
 * ClientData
 */
public class ClientData {
    private String lastKnownIp;
    private String detectedOs;
    private ClientHandler handler;
    private boolean isConnected;

    public ClientData(String ip, String os, ClientHandler handler, boolean connected) {
        lastKnownIp = ip;
        detectedOs = os;
        this.handler = handler;
        isConnected = connected;
    }

    public String getLastKnownIp() {
        return lastKnownIp;
    }

    public void setLastKnownIp(String lastKnownIp) {
        this.lastKnownIp = lastKnownIp;
    }

    public String getDetectedOs() {
        return detectedOs;
    }

    public void setDetectedOs(String detectedOs) {
        this.detectedOs = detectedOs;
    }

    public ClientHandler getHandler() {
        return handler;
    }

    public void setHandler(ClientHandler handler) {
        this.handler = handler;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean isConnected) {
        this.isConnected = isConnected;
    }
    
}
