package model.entity;

import java.sql.*;

public class Database {
    private Statement stmt;
    private Connection conn;
    private static int instanceCounter = 0;
    private static Database instance;
    private static ProfileDatabase profileDatabaseInstance;
    private static TweetDatabase tweetDatabaseInstance;
    private static DirectDatabase directDatabaseInstance;
    public Database(String DB_URL, String USER, String PASS) throws SQLException{
        if(instanceCounter == 0){
            connect(DB_URL, USER, PASS);
            instance = this;
            profileDatabaseInstance = new ProfileDatabase(stmt, conn);
            directDatabaseInstance = new DirectDatabase(stmt, conn);
            tweetDatabaseInstance = new TweetDatabase(stmt, conn);
            instanceCounter++;
        }
    }

    public static Database getInstance() {
        return instance;
    }

    public static ProfileDatabase getProfileDatabaseInstance() {
        return profileDatabaseInstance;
    }

    public static TweetDatabase getTweetDatabaseInstance() {
        return tweetDatabaseInstance;
    }

    public static DirectDatabase getDirectDatabaseInstance() {
        return directDatabaseInstance;
    }

    public void connect(String DB_URL, String USER, String PASS) throws SQLException{
        conn = DriverManager.getConnection(DB_URL, USER, PASS);
        stmt = conn.createStatement();
    }

    public void makeOrUseDB(String DBname, boolean createdBefore) throws SQLException{
        if (!createdBefore) {
            String sql = "CREATE DATABASE " + DBname;
            stmt.executeUpdate(sql);
            sql = "USE " + DBname;
            stmt.executeUpdate(sql);
            profileDatabaseInstance.createTableProfile();
            profileDatabaseInstance.createTableUser();
            profileDatabaseInstance.createTableFollow();
            directDatabaseInstance.createTableDirect();
            profileDatabaseInstance.createTableBlock();
            tweetDatabaseInstance.createTableTweet();
            tweetDatabaseInstance.createTableMedia();
            tweetDatabaseInstance.createTablePollOptions();
            tweetDatabaseInstance.createTablePollResults();
            tweetDatabaseInstance.createTableHashtags();
            tweetDatabaseInstance.createTablelikes();
        }
    }

    public void closeDB() throws SQLException {
        if (conn != null)
            conn.close();
        if (stmt != null)
            stmt.close();
    }
}