package model.entity;

import java.io.PrintWriter;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;

public class DirectDatabase extends DatabaseTables {
    public DirectDatabase(Statement stmt, Connection conn) {
        super(stmt, conn);
    }

    public void createTableDirect() throws SQLException {
        String sql = "CREATE TABLE Directs " +
                "(`sender username` VARCHAR(20)," +
                "`receiver username` VARCHAR(20)," +
                "text VARCHAR(2056)," +
                "date DATETIME," +
                "FOREIGN KEY (`sender username`) REFERENCES profiles(username)," +
                "FOREIGN KEY (`receiver username`) REFERENCES profiles(username))";
        stmt.executeUpdate(sql);
        System.out.println("Created table Directs in given database...");
    }

    public void direct(String senderUsername, String receiverUsername, String text, HttpResponse httpResponse) throws SQLException {
        if(!Database.getProfileDatabaseInstance().usernameExists(senderUsername, httpResponse)){
            return;
        }
        if(!Database.getProfileDatabaseInstance().usernameExists(receiverUsername, httpResponse)){
            return;
        }
        if(Database.getProfileDatabaseInstance().isBlocked(receiverUsername, senderUsername) ||
                Database.getProfileDatabaseInstance().isBlocked(senderUsername, receiverUsername)){
            if (httpResponse != null){
                httpResponse.writeResponse(403, "Not allowed");
                httpResponse.setGotResponse(true);
            }
            return;
        }
        PreparedStatement pstmt = conn.prepareStatement("INSERT INTO `directs` " +
                "(`sender username`,`receiver username`,`text`,`date`) " +
                "VALUES(?,?,?,CURRENT_TIMESTAMP)");
        pstmt.setString(1, senderUsername);
        pstmt.setString(2, receiverUsername);
        pstmt.setString(3, text);
        pstmt.executeUpdate();
        if (httpResponse != null) {
            httpResponse.writeResponse(201, "model.entity.Direct sent successfully");
            httpResponse.setGotResponse(true);
        }
    }

    public ArrayList<Direct> getDirects(String username, String toUsername, HttpResponse httpResponse) throws SQLException {
        if(!Database.getProfileDatabaseInstance().usernameExists(username, httpResponse)){
            return null;
        }
        if(!Database.getProfileDatabaseInstance().usernameExists(toUsername, httpResponse)){
            return null;
        }
        ArrayList<Direct> directs = new ArrayList<>();
        PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM directs WHERE (`sender username` = ? AND `receiver username` = ?) OR (`sender username` = ? AND `receiver username` = ?) ORDER BY date ASC");
        pstmt.setString(1, username);
        pstmt.setString(2, toUsername);
        pstmt.setString(3, toUsername);
        pstmt.setString(4, username);
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            String senderUsername = rs.getString("sender username");
            String receiverUsername = rs.getString("receiver username");
            String text = rs.getString("text");
            LocalDateTime date = rs.getTimestamp("date").toLocalDateTime();
            Direct direct = new Direct(senderUsername, receiverUsername, text, date);
            directs.add(direct);
        }
        if(directs.isEmpty() && httpResponse != null){
            httpResponse.writeResponse(200, "No Directs found ...");
            httpResponse.setGotResponse(true);
        } else if (!directs.isEmpty() && httpResponse != null) {
            PrintWriter writer = httpResponse.getWriter();
            httpResponse.writeJsonResponse(200, null);
            writer.println(JsonHelper.toJsonWithAdapter(directs));
            httpResponse.setGotResponse(true);
        }
        return directs;
    }

    /**
     * This method gets all the profiles that the user has direct with before
     */
    public ArrayList<Profile> hasDirectWith(String username, HttpResponse httpResponse) throws SQLException {
        if(!Database.getProfileDatabaseInstance().usernameExists(username, httpResponse)){
            return null;
        }
        ArrayList<String> usernames = new ArrayList<>();
        ArrayList<Profile> profiles = new ArrayList<>();
        PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM directs WHERE `sender username` = ? OR `receiver username` = ? ORDER BY date DESC");
        pstmt.setString(1, username);
        pstmt.setString(2, username);
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            String senderUsername = rs.getString("sender username");
            String receiverUsername = rs.getString("receiver username");
            if(!senderUsername.equals(username)){
                if (!usernames.contains(senderUsername)) {
                    profiles.add(Database.getProfileDatabaseInstance().getProfile(senderUsername, false));
                    usernames.add(senderUsername);
                }
            } else if (!receiverUsername.equals(username)) {
                if (!usernames.contains(receiverUsername)) {
                    profiles.add(Database.getProfileDatabaseInstance().getProfile(receiverUsername, false));
                    usernames.add(receiverUsername);
                }
            }
        }
        Iterator<Profile> iterator = profiles.iterator();
        while (iterator.hasNext()) {
            Profile profile = iterator.next();
            if (Database.getProfileDatabaseInstance().isBlocked(username, profile.getUsername()) ||
                    Database.getProfileDatabaseInstance().isBlocked(profile.getUsername(), username)) {
                iterator.remove();
            }
        }
        if(profiles.isEmpty() && httpResponse != null){
            httpResponse.writeResponse(200, "No Directs found ...");
            httpResponse.setGotResponse(true);
        } else if (!profiles.isEmpty() && httpResponse != null) {
            PrintWriter writer = httpResponse.getWriter();
            httpResponse.writeJsonResponse(200, null);
            writer.println(JsonHelper.toJsonWithAdapter(profiles));
            httpResponse.setGotResponse(true);
        }
        return profiles;
    }
}