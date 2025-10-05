import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Server {
    private ServerSocket serverSocket;
    // private final ConcurrentMap<Integer, ClientHandler> clients = new ConcurrentHashMap<>();
    private volatile boolean running = true; //the volitle means changes are visible across threads
    private final AtomicInteger nextId = new AtomicInteger(1);

    public Server(int port) throws IOException{
        serverSocket = new ServerSocket(port);
        System.out.println("Server listening on port " + port);
    }
    
    public void start() {
        Thread acceptor = new Thread(() -> {
            while(running) {
                try {
                    Socket client = serverSocket.accept();
                    client.setTcpNoDelay(true);
                    int id = nextId.getAndIncrement();
                    
                } catch (IOException e) {
                    System.err.println("Accept error: " + e.getMessage());
                }
            }
        });
    }
}
