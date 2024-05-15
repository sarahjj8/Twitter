package model.entity;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class SetUp {
    private static int PORT;
    private static final String HOST = "localhost";
//    private static final String path = "..\\config.txt";
    private static final String path = "C:\\Users\\DELL INSPIRON\\Desktop\\ap final\\config.txt";

    public static int getPORT() {
        return PORT;
    }

    public static String getHOST() {
        return HOST;
    }

    public static void setUp() throws IOException {
        String DB_URL = null;
        String DBname = null;
        String USER = null;
        String PASS = null;
        boolean createdBefore = true;
//        File file = new File("..\\config.txt");
        File file = new File(path);
        Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] parts = line.split("=");
            if (parts.length == 2) {
                String key = parts[0].trim();
                String value = parts[1].trim();
                switch (key) {
                    case "PORT":
                        PORT = Integer.parseInt(value);
                        break;
                    case "DB_URL":
                        DB_URL = value;
                        break;
                    case "DBname":
                        DBname = value;
                        break;
                    case "USER":
                        USER = value;
                        break;
                    case "PASS":
                        PASS = value;
                        break;
                    case "Created_Before":
                        if(value.equals("true"))
                            createdBefore = true;
                        else if(value.equals("false"))
                            createdBefore = false;
                        else
                            throw new IOException("Invalid value for createdBefore");
                        break;
                }
            }
        }
        Server.setConfigure(PORT, DB_URL, DBname, USER, PASS, createdBefore);
        scanner.close();
    }

    public static void setUpClient() throws IOException {
        File file = new File(path);
        Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] parts = line.split("=");
            if (parts.length == 2) {
                String key = parts[0].trim();
                String value = parts[1].trim();
                if(key.equals("PORT")){
                    PORT = Integer.parseInt(value);
                    HttpClientResponse.setConfigure(PORT, HOST);
                    scanner.close();
                    return;
                }
            }
        }

    }
}
