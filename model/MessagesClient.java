package model;

import controller.MessagesController;
import model.entity.Direct;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;

public class MessagesClient {
    private String username;
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private MessagesController messagesController;

    public MessagesClient(String username, Socket socket, MessagesController messagesController) {
        this.username = username;
        this.socket = socket;
        this.messagesController = messagesController;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            System.out.println("IO exception occurred in client: " + e.getMessage());
            closeResources(socket, bufferedReader, bufferedWriter);
        }
    }

    public void listenForMessages() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        String input = bufferedReader.readLine();
                        if (input != null) {
                            Direct sentDirect = parseDirect(input);
                            if (sentDirect != null) {
                                if (sentDirect.getReceiverUsername().equals(username)) {
                                    System.out.println("Received message: " + sentDirect.getText());
                                    messagesController.addDirect(sentDirect);
                                    //page of the direct should be loaded here
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    System.out.println("IO exception occurred in client: " + e.getMessage());
                    closeResources(socket, bufferedReader, bufferedWriter);
                }
            }
        }).start();
    }

    private Direct parseDirect(String input) {
        // Assuming the input format is: sender,receiver,text
        String[] parts = input.split(",");
        if (parts.length == 3) {
            String sender = parts[0].trim();
            String receiver = parts[1].trim();
            String text = parts[2].trim();
            LocalDateTime sentDate = LocalDateTime.now();
            return new Direct(sender, receiver, text,sentDate);
        }
        return null;
    }

//    public static void main(String[] args) {
//        if (args.length != 1) {
//            System.out.println("Please provide the username as a command-line argument.");
//            return;
//        }
//        String username = args[0];
//        try {
//            Socket socket = new Socket("127.0.0.1", 6000);
//            MessagesClient client = new MessagesClient(username, socket);
//            client.listenForMessages();
//        } catch (IOException e) {
//            System.out.println("IO exception occurred in client: " + e.getMessage());
//        }
//    }

    public void closeResources(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        try {
            if (bufferedReader != null)
                bufferedReader.close();
            if (bufferedWriter != null)
                bufferedWriter.close();
            if (socket != null)
                socket.close();
        } catch (IOException e) {
            System.out.println("IO exception occurred in client: " + e.getMessage());
        }
    }
}
