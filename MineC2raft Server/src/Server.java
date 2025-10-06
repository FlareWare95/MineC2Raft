import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
    private ServerSocket serverSocket; //UDP socket
    private static final CopyOnWriteArrayList<ClientHandler> clients = new CopyOnWriteArrayList<>();


    public Server() throws IOException{
        serverSocket = new ServerSocket(50000);
        

        System.out.println("Server Created. Listening for clients...");
    }
    

    public void runServer() throws IOException {
        BufferedReader userReader = new BufferedReader(new InputStreamReader(System.in));
        String userIn = userReader.readLine();

        while(userIn != "done") {  // we keep getting incoming packets until we no longer want to
            Socket clientSocket = serverSocket.accept();

            ClientHandler handler = new ClientHandler(clientSocket);
            clients.add(handler);
            handler.start();
            
            commandHandler(userIn, handler);
        }
    }

    public static void commandHandler(String cmd, ClientHandler clientHandler) {
         while(cmd != null && !cmd.equals("done")) {
        switch(cmd) {
            case "os":
                cmd = System.getProperty("os.name")+ " " + System.getProperty("os.version");
                broadcast(cmd, clientHandler);
                break;
            case "user":
                cmd = System.getProperty("user.name");
                break;
            case "HELP": 
                cmd = "               ********** MineC2raft Commands *********** \n\n\t\tos: prints the os of the client.\n\t\tuser: prints the name of the client.\n\t\tdone: exits the client port\nt\\tabout: about the program.\n\nWINDOWS ONLY\n(Maybe i'll add linux soon) but like lowk you can use a remote terminal \nThats all you need to know foo";
                System.out.println(cmd);
                break;
            case "poop":
                System.out.println("Poop");
                break;
            case "about":
                cmd = "░▒▓ MINEC2RAFT ▓▒░\nVersion: " + "0.01" + "\nWritten by Austin Hall for RIT's Red Team.\nWritten for fun too lowk...\n\nA dud?\n/give @p minecraft:oak_sapling";
                System.out.println(cmd);
                break;                
            default: 
                broadcast(cmd, clientHandler);
                break;
            }
        }
    }

    public static void broadcast(String msg, ClientHandler sender) {
        for(ClientHandler client : clients) {
            if (client != sender) {
                client.sendMessage("[Broadcast]: " + msg);
            }
        }
    }

}
