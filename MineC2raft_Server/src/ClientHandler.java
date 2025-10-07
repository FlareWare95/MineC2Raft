import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class ClientHandler extends Thread{
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    


    public ClientHandler(Socket soc) {
        socket = soc;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line);

                out.println(line);
            }
        } catch (IOException e) {
            System.out.println("Connection lost: " + socket.getRemoteSocketAddress());
            Server.clients.remove(this);
            
        } finally {
            try {
                socket.close();
            } catch (IOException ignored) {

            }
        }
    }

    public void sendMessage(String msg) {
        if (out != null) {
            out.println(msg);
        }
    }

    public int getPort() {
        return socket.getLocalPort();
    }

    public InetAddress getInetAddr() {
        return socket.getInetAddress();
    }
}
