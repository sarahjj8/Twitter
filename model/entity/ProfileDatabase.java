package model.entity;

import java.io.PrintWriter;
import java.sql.*;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class ProfileDatabase extends DatabaseTables {
    public ProfileDatabase(Statement stmt, Connection conn) {
        super(stmt, conn);
    }

    public void createTableUser() throws SQLException {
        String sql = "CREATE TABLE Users " +
                "(username VARCHAR(20)," +
                " name VARCHAR(50) , " +
                " `last name` VARCHAR(50) , " +
                " email VARCHAR(320) , " +
                " `phone number` VARCHAR(15) , " +
                " country VARCHAR(50), " +
                " birthdate DATE , " +
                " `created date` DATETIME , " +
                "FOREIGN KEY (username) REFERENCES profiles(username))";
        stmt.executeUpdate(sql);
        System.out.println("Created table Users in given database...");
    }

    public void createTableProfile() throws SQLException {
        String sql = "CREATE TABLE Profiles(username VARCHAR(20) PRIMARY KEY , " +
                "password VARCHAR(25) NOT NULL , " +
                "`logged in` VARCHAR(6)," +    //This can be true or false
                "bio VARCHAR(160) , " +
                "location VARCHAR(50), " +
                "`website address` VARCHAR(2048) ," +
                "`avatar URL` VARCHAR(500) ," +
                "`header URL` VARCHAR(500) ," +
                "`last modified` DATETIME)";
        stmt.executeUpdate(sql);
        System.out.println("Created table Profiles in given database...");
    }

    public void createTableFollow() throws SQLException {
        String sql = "CREATE TABLE Follow " +
                "(`follower username` VARCHAR(20)," +
                "`followed username` VARCHAR(20)," +
                "FOREIGN KEY (`follower username`) REFERENCES profiles(username)," +
                "FOREIGN KEY (`followed username`) REFERENCES profiles(username))";
        stmt.executeUpdate(sql);
        System.out.println("Created table Follow in given database...");
    }

    public void createTableBlock() throws SQLException {
        String sql = "CREATE TABLE Block " +
                "(username VARCHAR(20)," +
                "`blocked username` VARCHAR(20)," +
                "FOREIGN KEY (username) REFERENCES profiles(username)," +
                "FOREIGN KEY (`blocked username`) REFERENCES profiles(username))";
        stmt.executeUpdate(sql);
        System.out.println("Created table Block in given database...");
    }

    /**
     * this method is to check if the entered username is unique or not
     */
    public boolean isValidUsername(String username) throws SQLException {
        String sql = "select count(*) from profiles where `username`  = '" + username + "'";
        ResultSet rs = stmt.executeQuery(sql);
        rs.next();
        int counter = rs.getInt("count(*)");
        if (counter == 0)
            return true;
        else
            return false;
    }

    public boolean usernameExists(String username, HttpResponse httpResponse) throws SQLException {
        String sql = "select count(*) from profiles where `username`  = '" + username + "'";
        ResultSet rs = stmt.executeQuery(sql);
        rs.next();
        int counter = rs.getInt("count(*)");
        if (counter == 0){
            if (httpResponse != null) {
                httpResponse.writeResponse(404, "User not found.");
                httpResponse.setGotResponse(true);
            }
            return false;
        }
        else
            return true;
    }

    /**
     * this method is to check if the entered phone number is unique or not
     */
    public boolean isUniquePhoneNumber(String phoneNumber) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) FROM users WHERE `phone number`  = ?");
        pstmt.setString(1, phoneNumber);
        ResultSet rs = pstmt.executeQuery();
        rs.next();
        int counter = rs.getInt("count(*)");
        if (counter == 0)
            return true;
        else
            return false;
    }

    /**
     * this method is to check if the entered email is unique or not
     */
    public boolean isUniqueEmail(String email) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) FROM users WHERE `email`  = ?");
        pstmt.setString(1, email);
        ResultSet rs = pstmt.executeQuery();
        rs.next();
        int counter = rs.getInt("count(*)");
        if (counter == 0)
            return true;
        else
            return false;
    }

    public HashMap<String, String> getNameAndLastName(String username) throws SQLException {
        //user not found
        if (!usernameExists(username,null)) {
            return null;
        }
        HashMap<String, String> nameAndLastName = new HashMap<>();
        PreparedStatement pstmt = conn.prepareStatement("SELECT `name`,`last name`,`created date` FROM users WHERE `username`  = ?");
        pstmt.setString(1, username);
        ResultSet rs = pstmt.executeQuery();
        rs.next();
        String name = rs.getString("name");
        nameAndLastName.put("name", name);
        String lastName = rs.getString("last name");
        nameAndLastName.put("last name", lastName);
        String createdDate = rs.getString("created date");
        nameAndLastName.put("created date", createdDate);
        return nameAndLastName;
    }

    /**
     * This method is to check if the user has logged into the system or not
     */
    public boolean hasLoggedIn(String username) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement("SELECT `logged in` FROM profiles WHERE `username`  = ?");
        pstmt.setString(1, username);
        ResultSet rs = pstmt.executeQuery();
        rs.next();
        String check = rs.getString("logged in");
        if (check.equals("true"))
            return true;
        else if (check.equals("false"))
            return false;
        else
            return false;
    }

    public void updateLoggedIn(String username, String newValue) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement("UPDATE profiles " +
                "SET `logged in` = ? WHERE `username` = ?");
        pstmt.setString(1, newValue);
        pstmt.setString(2, username);
        pstmt.executeUpdate();
    }

    public void addProfile(String username, User newUser, String password, HttpResponse httpResponse) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement("INSERT INTO `profiles`" +
                "(`username`,`password`,`last modified`,`logged in`)" +
                "VALUES(?,?,CURRENT_TIMESTAMP,?)");
        pstmt.setString(1, username);
        pstmt.setString(2, password);
        pstmt.setString(3, "false");
        pstmt.executeUpdate();
        pstmt = conn.prepareStatement("INSERT INTO `users`" +
                "(`username`,`name`,`last name`,`email`,`phone number`,`country`,`birthdate`,`created date`) " +
                "VALUES(?,?,?,?,?,?,?,CURRENT_TIMESTAMP)");
        pstmt.setString(1, username);
        pstmt.setString(2, newUser.getName());
        pstmt.setString(3, newUser.getLastName());
        pstmt.setString(4, newUser.getEmail());
        pstmt.setString(5, newUser.getPhoneNumber());
        pstmt.setString(6, newUser.getCountry());
        pstmt.setDate(7, new java.sql.Date(newUser.getBirthDate().getTime()));
        pstmt.executeUpdate();
        if (httpResponse != null) {
            httpResponse.writeResponse(201, "Welcome to twitter");
            httpResponse.setGotResponse(true);
        }
    }

    //this method is used in login where we have to check if the user has entered the right password or not
    public boolean checkPassword(String username, String enteredPassword) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement("SELECT password FROM profiles WHERE `username`  = ?");
        pstmt.setString(1, username);
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            String password = rs.getString("password");
            if (!password.equals(enteredPassword))
                return false;
            else
                return true;
        }
        //there isn't any profiles to check ...
        return false;
    }

    public void block(String username, String blockedUsername, HttpResponse httpResponse) throws SQLException {
        //if the user wants to block herself/himself
        if(!usernameExists(username, httpResponse)){
            return;
        }
        if(!usernameExists(blockedUsername, httpResponse)){
            return;
        }
        if (username.equals(blockedUsername)){
            if (httpResponse != null) {
                httpResponse.writeResponse(400, "Self-blocking is not allowed.");
                httpResponse.setGotResponse(true);
            }
            return;
        } else if (isBlocked(username, blockedUsername)) {
            if (httpResponse != null) {
                httpResponse.writeResponse(400,  blockedUsername + " is already blocked");
                httpResponse.setGotResponse(true);
            }
            return;
        }
        unFollow(username, blockedUsername, null);
        unFollow(blockedUsername, username, null);
        PreparedStatement pstmt = conn.prepareStatement("INSERT INTO `block`" +
                "(`username`,`blocked username`) " +
                "VALUES(?,?)");
        pstmt.setString(1, username);
        pstmt.setString(2, blockedUsername);
        pstmt.executeUpdate();
        if (httpResponse != null) {
            httpResponse.writeResponse(201, blockedUsername + " has been blocked successfully.");
            httpResponse.setGotResponse(true);
        }
    }

    public void unBlock(String username, String blockedUsername, HttpResponse httpResponse) throws SQLException {
        if(!usernameExists(username, httpResponse)){
            return;
        }
        if(!usernameExists(blockedUsername, httpResponse)){
            return;
        }
        if (username.equals(blockedUsername)){
            if (httpResponse != null) {
                httpResponse.writeResponse(400, "Self-unblocking is not allowed.");
                httpResponse.setGotResponse(true);
            }
            return;
        } else if (!isBlocked(username, blockedUsername)) {
            if (httpResponse != null) {
                httpResponse.writeResponse(400,  blockedUsername + " has not been blocked. There is no need to unblock");
                httpResponse.setGotResponse(true);
            }
            return;
        }
        PreparedStatement pstmt = conn.prepareStatement("DELETE FROM `block`" +
                "WHERE `username` = ? AND `blocked username` = ?");
        pstmt.setString(1, username);
        pstmt.setString(2, blockedUsername);
        pstmt.executeUpdate();
        if (httpResponse != null) {
            httpResponse.writeResponse(200, blockedUsername + " has been unblocked successfully.");
            httpResponse.setGotResponse(true);
        }
    }

    public boolean isBlocked(String username, String blockedUsername) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) FROM block WHERE `username`  = ? AND `blocked username` = ?");
        pstmt.setString(1, username);
        pstmt.setString(2, blockedUsername);
        ResultSet rs = pstmt.executeQuery();
        rs.next();
        int counter = rs.getInt("count(*)");
        if (counter == 0)
            return false;
        else
            return true;
    }

    public void follow(String followerUsername, String followedUsername, HttpResponse httpResponse) throws SQLException {
        //if the user wants to follow herself/himself
        if(!usernameExists(followerUsername, httpResponse)){
            return;
        }
        if(!usernameExists(followedUsername, httpResponse)){
            return;
        }
        if (followerUsername.equals(followedUsername)){
            if (httpResponse != null) {
                httpResponse.writeResponse(400, "Self-following is not allowed.");
                httpResponse.setGotResponse(true);
            }
            return;
        } else if (isFollowedBy(followerUsername, followedUsername, null)) {
            if (httpResponse != null) {
                httpResponse.writeResponse(400,  followedUsername + " is already followed");
                httpResponse.setGotResponse(true);
            }
            return;
        }
        PreparedStatement pstmt = conn.prepareStatement("INSERT INTO `follow`" +
                "(`follower username`,`followed username`) " +
                "VALUES(?,?)");
        pstmt.setString(1, followerUsername);
        pstmt.setString(2, followedUsername);
        pstmt.executeUpdate();
        if (httpResponse != null) {
            httpResponse.writeResponse(201, followedUsername + " has been followed successfully.");
            httpResponse.setGotResponse(true);
        }
    }

    public void unFollow(String followerUsername, String followedUsername, HttpResponse httpResponse) throws SQLException {
        if(!usernameExists(followedUsername, httpResponse)){
            return;
        }
        if(!usernameExists(followerUsername, httpResponse)){
            return;
        }
        if (followerUsername.equals(followedUsername)){
            if (httpResponse != null) {
                httpResponse.writeResponse(400, "Self-unfollowing is not allowed.");
                httpResponse.setGotResponse(true);
            }
            return;
        } else if (!isFollowedBy(followerUsername, followedUsername, null)) {
            if (httpResponse != null) {
                httpResponse.writeResponse(400,  followedUsername + " has not been followed. There is no need to unfollow");
                httpResponse.setGotResponse(true);
            }
            return;
        }
        PreparedStatement pstmt = conn.prepareStatement("DELETE FROM `follow`" +
                "WHERE `follower username` = ? AND `followed username` = ?");
        pstmt.setString(1, followerUsername);
        pstmt.setString(2, followedUsername);
        pstmt.executeUpdate();
        if (httpResponse != null) {
            httpResponse.writeResponse(200, followedUsername + " has been unfollowed successfully.");
            httpResponse.setGotResponse(true);
        }
    }

    public boolean isFollowedBy(String followerUsername, String followedUsername, HttpResponse httpResponse) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) FROM follow WHERE `follower username`  = ? AND `followed username` = ?");
        pstmt.setString(1, followerUsername);
        pstmt.setString(2, followedUsername);
        ResultSet rs = pstmt.executeQuery();
        rs.next();
        int counter = rs.getInt("count(*)");
        if(httpResponse != null){
            if(counter == 0) {
                httpResponse.writeResponse(200, "false");
            } else {
                httpResponse.writeResponse(200, "true");
            }
            httpResponse.setGotResponse(true);
        }
        if (counter == 0)
            return false;
        else
            return true;
    }

    public int countFollowers(String username) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) FROM `follow` WHERE `followed username`  = ?");
        pstmt.setString(1, username);
        ResultSet rs = pstmt.executeQuery();
        rs.next();
        int counter = rs.getInt("count(*)");
        return counter;
    }

    public int countFollowing(String username) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) FROM `follow` WHERE `follower username`  = ?");
        pstmt.setString(1, username);
        ResultSet rs = pstmt.executeQuery();
        rs.next();
        int counter = rs.getInt("count(*)");
        return counter;
    }

    public void updateBio(String username, String newBio, HttpResponse httpResponse) throws SQLException {
        if(!usernameExists(username, httpResponse)){
            return;
        }
        PreparedStatement pstmt = conn.prepareStatement("UPDATE profiles " +
                "SET `bio` = ? WHERE `username` = ?");
        pstmt.setString(1, newBio);
        pstmt.setString(2, username);
        pstmt.executeUpdate();
        if (httpResponse != null) {
            httpResponse.writeResponse(200, "Bio added successfully");
            httpResponse.setGotResponse(true);
        }
        updateLastModified(username);
    }

    /**
     * This method can be used to add/update/remove(by passing null to newHeaderUrl) a profile avatar
     */
    public void updateAvatar(String username, String newAvatarUrl, HttpResponse httpResponse) throws SQLException {
        if(!usernameExists(username, httpResponse)){
            return;
        }
        PreparedStatement pstmt = conn.prepareStatement("UPDATE profiles " +
                "SET `avatar URL` = ? WHERE `username` = ?");
        pstmt.setString(1, newAvatarUrl);
        pstmt.setString(2, username);
        pstmt.executeUpdate();
        if (httpResponse != null) {
            httpResponse.writeResponse(200, "Avatar added successfully");
            httpResponse.setGotResponse(true);
        }
        updateLastModified(username);
    }

    /**
     * This method can be used to add/update/remove(by passing null to newHeaderUrl) a profile header
     */
    public void updateHeader(String username, String newHeaderUrl, HttpResponse httpResponse) throws SQLException {
        if(!usernameExists(username, httpResponse)){
            return;
        }
        PreparedStatement pstmt = conn.prepareStatement("UPDATE profiles " +
                "SET `header URL` = ? WHERE `username` = ?");
        pstmt.setString(1, newHeaderUrl);
        pstmt.setString(2, username);
        pstmt.executeUpdate();
        if (httpResponse != null) {
            httpResponse.writeResponse(200, "Header added successfully");
            httpResponse.setGotResponse(true);
        }
        updateLastModified(username);
    }

    public void updatePassword(String username, String newPass) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement("UPDATE profiles " +
                "SET `password` = ? WHERE `username` = ?");
        pstmt.setString(1, newPass);
        pstmt.setString(2, username);
        pstmt.executeUpdate();
        updateLastModified(username);
    }

    public void updateLocation(String username, String newLocation, HttpResponse httpResponse) throws SQLException {
        if(!usernameExists(username, httpResponse)){
            return;
        }
        PreparedStatement pstmt = conn.prepareStatement("UPDATE profiles " +
                "SET `location` = ? WHERE `username` = ?");
        pstmt.setString(1, newLocation);
        pstmt.setString(2, username);
        pstmt.executeUpdate();
        if (httpResponse != null) {
            httpResponse.writeResponse(200, "Location added successfully");
            httpResponse.setGotResponse(true);
        }
        updateLastModified(username);
    }

    public void updateWebAddress(String username, String newWebAddress, HttpResponse httpResponse) throws SQLException {
        if(!usernameExists(username, httpResponse)){
            return;
        }
        PreparedStatement pstmt = conn.prepareStatement("UPDATE profiles " +
                "SET `website address` = ? WHERE `username` = ?");
        pstmt.setString(1, newWebAddress);
        pstmt.setString(2, username);
        pstmt.executeUpdate();
        if (httpResponse != null) {
            httpResponse.writeResponse(200, "Website address added successfully");
            httpResponse.setGotResponse(true);
        }
        updateLastModified(username);
    }

    public void updateLastModified(String username) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement("UPDATE profiles " +
                "SET `last modified` = CURRENT_TIMESTAMP WHERE `username` = ?");
        pstmt.setString(1, username);
        pstmt.executeUpdate();
    }

    /**
     * This method returns username of profiles that their username/name/last name contains string str(without considering blocked profiles)
     */
    public ArrayList<Profile> searchProfile(String str, HttpResponse httpResponse) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement("SELECT profiles.username, profiles.`avatar URL` FROM profiles JOIN " +
                "users ON profiles.username = users.username WHERE profiles.username " +
                "LIKE ? OR users.name LIKE ? OR users.`last name` LIKE ?");
        pstmt.setString(1, "%" + str + "%");
        pstmt.setString(2, "%" + str + "%");
        pstmt.setString(3, "%" + str + "%");
        ResultSet rs = pstmt.executeQuery();
        ArrayList<Profile> profiles = toProfileObject(rs, false);
        if(profiles.isEmpty() && httpResponse != null){
            httpResponse.writeResponse(200, "No profiles found ...");
            httpResponse.setGotResponse(true);
        } else if (!profiles.isEmpty() && httpResponse != null) {
            PrintWriter writer = httpResponse.getWriter();
            httpResponse.writeJsonResponse(200, null);
            writer.println(JsonHelper.toJsonWithAdapter(profiles));
            httpResponse.setGotResponse(true);
        }
        return profiles;
    }

    public Profile getProfile(String username, HttpResponse httpResponse) throws SQLException{
        PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM profiles WHERE `username` = ?");
        pstmt.setString(1, username);
        ResultSet rs = pstmt.executeQuery();
        ArrayList<Profile> profiles = toProfileObject(rs, true);
        if(profiles.isEmpty()){
            httpResponse.writeResponse(200, "Profile not found ...");
            httpResponse.setGotResponse(true);
            return null;
        }
        else{
            PrintWriter writer = httpResponse.getWriter();
            httpResponse.writeJsonResponse(200, null);
            writer.println(JsonHelper.toJsonWithAdapter(profiles));
            httpResponse.setGotResponse(true);
            return profiles.get(0);
        }

    }

    /**
     * This method returns username of profiles that their username/name/last name contains string str(this method won't show blocked profiles)
     */
    public ArrayList<Profile> searchProfile(String username, String str, HttpResponse httpResponse) throws SQLException{
        PreparedStatement pstmt = conn.prepareStatement("SELECT profiles.username, profiles.`avatar URL` FROM profiles JOIN " +
                "users ON profiles.username = users.username WHERE profiles.username " +
                "LIKE ? OR users.name LIKE ? OR users.`last name` LIKE ?");
        pstmt.setString(1, "%" + str + "%");
        pstmt.setString(2, "%" + str + "%");
        pstmt.setString(3, "%" + str + "%");
        ResultSet rs = pstmt.executeQuery();
        ArrayList<Profile> profiles = toProfileObject(rs, false);
        Iterator<Profile> iterator = profiles.iterator();
        while (iterator.hasNext()) {
            Profile profile = iterator.next();
            if (isBlocked(username, profile.getUsername()) || isBlocked(profile.getUsername(), username)) {
                iterator.remove();
            }
        }
        if(profiles.isEmpty() && httpResponse != null){
            httpResponse.writeResponse(200, "No profiles found ...");
            httpResponse.setGotResponse(true);
        } else if (!profiles.isEmpty() && httpResponse != null) {
            PrintWriter writer = httpResponse.getWriter();
            httpResponse.writeJsonResponse(200, null);
            writer.println(JsonHelper.toJsonWithAdapter(profiles));
            httpResponse.setGotResponse(true);
        }
        return profiles;
    }

    public Profile getProfile(String username, boolean complete) throws SQLException{
        PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM profiles WHERE `username` = ?");
        pstmt.setString(1, username);
        ResultSet rs = pstmt.executeQuery();
        ArrayList<Profile> profiles = toProfileObject(rs, complete);
        if(profiles.isEmpty())
            return null;
        else
            return profiles.get(0);
    }

    public ArrayList<Profile> toProfileObject(ResultSet rs, boolean complete) throws SQLException {
        ArrayList<Profile> profiles = new ArrayList<>();
        Profile profile;
        while (rs.next()) {
            String username = rs.getString("username");
            HashMap<String, String> nameAndLastName = getNameAndLastName(username);
            String name = nameAndLastName.get("name");
            String lastName = nameAndLastName.get("last name");
            String createdDateString = nameAndLastName.get("created date");
            String avatar = rs.getString("avatar URL");
            if(complete){
                java.util.Date createdDate = null;
                if(createdDateString != null)
                    createdDate = User.toDate(createdDateString);
                String bio = rs.getString("bio");
                String location = rs.getString("location");
                String header = rs.getString("header URL");
                String websiteAddress = rs.getString("website address");
                int numberOfFollowers = countFollowers(username);
                int numberOfFollowing = countFollowing(username);
                profile = new Profile(name , lastName, createdDate, username, bio, location, websiteAddress,
                        avatar, header, numberOfFollowers, numberOfFollowing);
            }else{
                profile = new Profile(name, lastName, username, avatar);
            }
            profiles.add(profile);
        }
        return profiles;
    }
}
