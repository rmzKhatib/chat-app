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

            String fromServer;
            String fromUser;

            while ((fromServer = in.readLine()) != null) {
                if (fromServer.startsWith("SUBMITNAME")) {
                    System.out.print("Enter username: ");
                    fromUser = input.readLine();
                    out.println(fromUser);
                } else if (fromServer.startsWith("NAMEINUSE")) {
                    System.out.println("Username already taken, try another.");
                } else if (fromServer.startsWith("NAMEACCEPTED")) {
                    System.out.println("Username accepted. You can start chatting!");
                    break;
                }
            }

            // Now enter chat loop
            while ((fromUser = input.readLine()) != null) {
                out.println(fromUser);
                System.out.println(in.readLine());
            }

        } catch (IOException e) {
            System.out.println("Client error: " + e.getMessage());
        }
    }
}
