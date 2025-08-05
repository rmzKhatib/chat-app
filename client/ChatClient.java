package client;

import java.io.*;
import java.net.*;

public class ChatClient {
    public static void main(String[] args) {
        String serverAddress = "localhost";
        int port = 12345;

        try (Socket socket = new Socket(serverAddress, port);
             BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Handle login first
            String fromServer;
            while ((fromServer = in.readLine()) != null) {
                if (fromServer.startsWith("SUBMITNAME")) {
                    System.out.print("Enter username: ");
                    String username = input.readLine();
                    out.println(username);
                } else if (fromServer.startsWith("NAMEINUSE")) {
                    System.out.println("Username already taken, try another.");
                } else if (fromServer.startsWith("NAMEACCEPTED")) {
                    String name = fromServer.substring("NAMEACCEPTED".length()).trim();
                    System.out.println("Welcome " + name + "! You can start chatting.");
                    break;
                }
            }

            // Start a background thread to listen to server messages
            Thread listener = new Thread(() -> {
                try {
                    String msg;
                    while ((msg = in.readLine()) != null) {
                        System.out.println(msg);
                    }
                } catch (IOException e) {
                    System.out.println("Disconnected from server.");
                }
            });
            listener.start();

            // Main thread handles sending input
            String userInput;
            while ((userInput = input.readLine()) != null) {
                out.println(userInput);
            }

        } catch (IOException e) {
            System.out.println("Client error: " + e.getMessage());
        }
    }
}
