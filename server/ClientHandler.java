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
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            System.out.println("Error setting up client handler: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            // Request and validate username
            while (true) {
                out.println("SUBMITNAME"); // Signal client to submit username
                String name = in.readLine();

                if (name == null) {
                    return;
                }

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

            out.println("NAMEACCEPTED " + userName);

            String message;
            while ((message = in.readLine()) != null) {
                System.out.println(userName + ": " + message);
                out.println("You said: " + message);
            }
        } catch (IOException e) {
            System.out.println(userName + " disconnected.");
        } finally {
            if (userName != null) {
                ChatServer.userNames.remove(userName);
            }
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println("Error closing socket.");
            }
        }
    }
}
