package server;

import java.io.*;
import java.net.*;

public class ClientHandler implements Runnable {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String userName;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // Handle username
            while (true) {
                out.println("SUBMITNAME");
                String name = in.readLine();

                if (name == null) return;

                synchronized (ChatServer.userNames) {
                    if (!ChatServer.userNames.contains(name) && !name.isBlank()) {
                        ChatServer.userNames.add(name);
                        userName = name;
                        out.println("NAMEACCEPTED " + userName);
                        break;
                    } else {
                        out.println("NAMEINUSE");
                    }
                }
            }

            // Add client writer to broadcast list
            synchronized (ChatServer.clientWriters) {
                ChatServer.clientWriters.add(out);
            }

            // Announce new user
            broadcast(userName + " has joined the chat!", false);

            // Main message loop
            String message;
            while ((message = in.readLine()) != null) {
                System.out.println(userName + ": " + message);
                broadcast(userName + ": " + message, false);
            }

        } catch (IOException e) {
            System.out.println(userName + " disconnected.");
        } finally {
            // Cleanup
            if (userName != null) {
                ChatServer.userNames.remove(userName);
                System.out.println(userName + " left the chat.");
                broadcast(userName + " has left the chat.", false);
            }
            if (out != null) {
                ChatServer.clientWriters.remove(out);
            }
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println("Error closing socket.");
            }
        }
    }

    private void broadcast(String message, boolean includeSelf) {
        synchronized (ChatServer.clientWriters) {
            for (PrintWriter writer : ChatServer.clientWriters) {
                if (includeSelf || writer != out) {
                    writer.println(message);
                }
            }
        }
    }
}
