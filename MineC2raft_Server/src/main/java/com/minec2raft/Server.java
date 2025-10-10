package com.minec2raft;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

/**@author Austin Hall
 * Main server class that handles communication between multiple clients using the Client class.
 * Utilizes the ClientHandler class to offload some work between threads, and makes this class easier to read.
 * CHATGPT was used as a LEARNING TOOL in the creation of this software.
 */
public class Server {

    //Escape codes or sommn for different colors. 
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RESET = "\u001B[0m";
    
    
    private static final double VERSION_NUM = 0.02; //version number (change when I feel cheeky ;)
    public static final CopyOnWriteArrayList<ClientHandler> clients = new CopyOnWriteArrayList<>(); // array of clients as threads.

    private ServerSocket serverSocket; //UDP socket
    private ServerSocket minecraftSocket; //socket used to communicate directly to minecraft.

    private Socket mineSoc;

    /**
     * Creates a new Server object. please only make one of these bc idk what would happen if more than one is open...
     */
    public Server(){
        try {
            serverSocket = new ServerSocket(50000);
            minecraftSocket = new ServerSocket(5050);
        } catch(IOException e) {
            System.out.println("Creation of server Failed.");
        }
    } 
    
    /**
     * runs all of the code relating to client / server communication, and handles user input for commands.
     * @throws IOException
     */
    public void runServer() throws IOException {
        System.out.println(GREEN + "Server Created. Listening for clients..." + RESET);

        //thread to handle communications between server any any clients
        Thread connectionThread = new Thread(() -> {
            try {
                while(true) {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println(GREEN + "Client found at: " + YELLOW + clientSocket.getPort() + RESET);

                    ClientHandler handler = new ClientHandler(clientSocket);
                    clients.add(handler);
                    handler.start(); 
                    
                }
            } catch (IOException e) {
                System.out.println(RED + "Client thread stopped." + RESET);
            }
        });
        connectionThread.start();

        Thread minecraftThread = new Thread(() -> {
            try {
                mineSoc = minecraftSocket.accept();

                ClientHandler mineHandler = new ClientHandler(mineSoc);
                System.out.println("minecraft socket connected.");
                mineHandler.start();

            } catch (IOException e) {
                System.out.println("Minecraft thread error" + e);
            }
        });
        minecraftThread.start();

        // Start user input listening.
        BufferedReader userReader = new BufferedReader(new InputStreamReader(System.in));
        String userIn = "";
        System.out.println(YELLOW + "Type 'coms' for a list of commands." + RESET);

        while((userIn = userReader.readLine()) != null && !userIn.equals("quit")) {
            commandHandler(userIn);
            sendToMinecraft();
        }
    }    

    /**
     * Method that helps runServer() interpret user commands. 
     * Some special commands have been added for ease of use, and any non-special commands will be interpreted through the client's terminal.
     * @param cmd - the user input.
     */
    public static void commandHandler(String cmd) {
        String reply;
        switch(cmd) {
            case "os", "version":
                reply = "ver";
                broadcast(reply, null);
                break;
            case "user", "client":
                reply = "whoami";
                broadcast(reply, null);
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
                broadcast(cmd, null);
                break;  
        }
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
    public static void  broadcast(String msg, ClientHandler sender) {

        if(msg == null || msg.trim().isEmpty() || msg.equals("__END__")) {
            return;
        }
        if(clients.isEmpty()) {
            System.out.println(RED + "No clients connected." + RESET);
            return;
        }
        System.out.println("\nWhere do you want to send this command?");
        String response = broadcastDestinationHandler();

        if(response != null) {
            if(response.equals("all")) {
                for(ClientHandler client : clients) {
                    if (client != sender) {
                        client.sendMessage("CMD: " + msg);
                    }
                }
            } else {
                try {
                    clients.get(Integer.parseInt(response)).sendMessage("CMD: " + msg);
                } catch(Exception e) {
                    System.out.println("Not a valid option.");
                }
            }
        }
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


    public void sendToMinecraft() throws IOException{
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter writer = new PrintWriter(mineSoc.getOutputStream());
        String str;
        while((str = reader.readLine()) != null) {
            writer.println(str);
        }
        
    }

}
