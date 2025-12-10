package com.minec2raft.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class ClientHandler extends Thread{
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    public String recentLn = "";

    public ClientHandler(Socket soc) {
        socket = soc;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            String line;
            System.out.println(Server.GREEN + "Start of broadcast @ port " + socket.getLocalPort() + Server.RESET + "\n");
            while ((line = in.readLine()) != null) {
                System.out.println(line);
                out.println(line);
            }
            System.out.println("\n" + Server.GREEN + "Broadcast completed @ Port " + socket.getLocalPort() + "." + Server.RESET);
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
