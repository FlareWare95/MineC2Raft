
import java.io.IOException;

public class Main {

    public static void main(String args[]) throws IOException, InterruptedException {
        System.out.println("Initializing Server...");

        Sockets sockets = new Sockets(2000);
        // Server server = new Server();
        // System.out.println(sockets.getServerSocket().toString());
        // sockets.listen();
        System.out.println(sockets.toString());
        // System.out.println(server.toString());
        sockets.commandeer();



        

    }
}
