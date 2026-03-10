package com.minec2raft.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class ClientHandler extends Thread{
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    public String recentLn = "";
    public BlockingQueue<String> cmdQueue = new LinkedBlockingQueue<String>(); //omg a queue being used in a project
    public static CommandSender sender;

    public ClientHandler(Socket soc) {
        socket = soc;
    }

    @Override
    public synchronized void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            String line;
            while((line = in.readLine()) != null) { // when we finally get something, we'll put it in the queue
                cmdQueue.put(line);
            }
        } catch (IOException e) {
            Bukkit.broadcastMessage("Connection lost: " + socket.getRemoteSocketAddress());
            Server.clients.remove(this);
            
        }catch(InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            try {
                socket.close();
            } catch (IOException ignored) {}
        }
    }

    public String waitForBroadcast() throws InterruptedException {
        StringBuilder builder = new StringBuilder();
        String line;
        while((line = cmdQueue.poll(10,TimeUnit.SECONDS)) != null) {
            if(line.equals( "__END__")) {
                break;
            }
            System.out.println("Current Line: " + line + "\n");
            builder.append(line + "\n");
        }

        return builder.toString();
    }

    public void sendMessage(String msg) {
        if (out != null) {
            out.println(msg);
        }
    }
    
    public String getRecentLn() {
        return recentLn;
    }

    public int getPort() {
        return socket.getLocalPort();
    }

    public InetAddress getInetAddr() {
        return socket.getInetAddress();
    }

    public InputStream getInputStream() throws IOException{
        return socket.getInputStream();
    }
}
