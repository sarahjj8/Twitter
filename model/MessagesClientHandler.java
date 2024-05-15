package model;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class MessagesClientHandler implements Runnable {
    private String username;
    private Socket socket;
    private static ArrayList<MessagesClientHandler> clientHandlers = new ArrayList<>();
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    public MessagesClientHandler(Socket socket) {
        try {
            this.socket = socket;
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            username = bufferedReader.readLine();
            broadcast(username + " joined chatroom!");
            clientHandlers.add(this);
        } catch (IOException e) {
            closeResources(socket, bufferedReader, bufferedWriter);
            System.out.println("IO exception occurred in client handler: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        String message;
        while (true){
            try{
                message = bufferedReader.readLine();
                if (message.endsWith(" left the chatroom!")) {
                    closeResources(socket, bufferedReader, bufferedWriter);
                    break;
                }
                broadcast(message);
            } catch (IOException e){
                closeResources(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }


    public synchronized void broadcast(String message){
        MessagesClientHandler sender = this;
        for(MessagesClientHandler clientHandler : clientHandlers){
            try {
                if(!clientHandler.equals(sender)){
                    clientHandler.bufferedWriter.write(message);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            } catch (IOException e){
                System.out.println("IO exception occurred in client handler: " + e.getMessage());
                closeResources(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    public void userLeft(){
        clientHandlers.remove(this);
        broadcast(username + " left the chatroom!");
    }
    public void closeResources(Socket socket ,BufferedReader bufferedReader, BufferedWriter bufferedWriter){
        userLeft();
        try {
            if(bufferedReader != null)
                bufferedReader.close();
            if(bufferedWriter != null)
                bufferedWriter.close();
            if(socket != null)
                socket.close();
        } catch (IOException e) {
            System.out.println("IO exception occurred in client handler: " + e.getMessage());
        }
    }
}