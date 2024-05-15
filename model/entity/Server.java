package model.entity;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;

public class Server {
    private static int PORT;
    private static String DB_URL;
    private static String DBname;
    private static String USER;
    private static String PASS;
    private static boolean createdBefore = true;

    public static int getPORT() {
        return PORT;
    }

    public static void main(String[] args) throws SQLException {
        try {
            SetUp.setUp();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return;
        }
        Database database = new Database(DB_URL, USER, PASS);
        Database.getInstance().makeOrUseDB(DBname, createdBefore);
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server listening on port " + PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clientHandler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setConfigure(int PORT, String DB_Url, String DBname, String USER, String PASS ,boolean createdBefore) {
        Server.PORT = PORT;
        Server.DB_URL = DB_Url;
        Server.DBname = DBname;
        Server.USER = USER;
        Server.PASS = PASS;
        Server.createdBefore = createdBefore;
    }

    //if the database has created before we have to use this DB_URL
//    static final String DB_URL = "jdbc:mysql://localhost/test";
    //if the database hasn't created before we have to use this DB_URL
//    static final String DB_URL = "jdbc:mysql://localhost";
//    private static int PORT = 8081;
//    private static String DB_URL = "jdbc:mysql://localhost/test2";
//    private static String USER = "root";
//    private static String PASS = "20042002";
//    private static boolean createdBefore = true;
}
