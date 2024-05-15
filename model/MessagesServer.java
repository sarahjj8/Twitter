package model;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MessagesServer{
    private ServerSocket serverSocket;
    private final int PORT;

    public MessagesServer(int port) {
        PORT = port;
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Chat room server is listening on port " + PORT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void startServer(){
        try{
            while (true){
                Socket socket = serverSocket.accept();
                System.out.println("Connected to a new client");
                MessagesClientHandler clientHandler = new MessagesClientHandler(socket);
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException e){
            System.out.println("Error in the server: " + e.getMessage());
        } finally {
            closeServerSocket();
        }
    }

    public void closeServerSocket(){
        if (serverSocket != null){
            try {
                serverSocket.close();
            } catch (IOException e) {
                System.out.println("Error in the server: " + e.getMessage());
            }
        }
    }


    public static void main(String[] args) {
        MessagesServer server = new MessagesServer(6000);
        server.startServer();
    }
}

