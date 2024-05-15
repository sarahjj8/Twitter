package model.entity;

import java.io.PrintWriter;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class TweetDatabase extends DatabaseTables {
    public TweetDatabase(Statement stmt, Connection conn) {
        super(stmt, conn);
    }

    public void createTableTweet() throws SQLException {
        String sql = "CREATE TABLE Tweets " +
                "(id INTEGER PRIMARY KEY AUTO_INCREMENT," +
                "username VARCHAR(20)," +
                "type VARCHAR(30)," +     //type: 1.normal  2.retweet   3.reply    4.poll   5.quote
                "text VARCHAR(2056)," +
                "date DATETIME," +
                "`about tweet` INTEGER," +
                "FOREIGN KEY (`about tweet`) REFERENCES Tweets(id)," +
                "FOREIGN KEY (username) REFERENCES profiles(username))";
        stmt.executeUpdate(sql);
        System.out.println("Created table Tweets in given database...");
    }

    public void createTableMedia() throws SQLException {
        String sql = "CREATE TABLE `Media` " +
                "(`id` INTEGER PRIMARY KEY AUTO_INCREMENT," +
                "`tweet id` INTEGER," +
                "`type` VARCHAR(6)," +   //type can be 'image' or 'video'
                "`url` VARCHAR(256)," +
                "FOREIGN KEY (`tweet id`) REFERENCES Tweets(id))";
        stmt.executeUpdate(sql);
        System.out.println("Created table Media in given database...");
    }

    public void createTablelikes() throws SQLException {
        String sql = "CREATE TABLE Likes " +
                "(`tweet id` INTEGER ," +
                "username VARCHAR(20)," +
                "date DATETIME," +
                "FOREIGN KEY (username) REFERENCES profiles(username)," +
                "FOREIGN KEY (`tweet id`) REFERENCES Tweets(id))";
        stmt.executeUpdate(sql);
        System.out.println("Created table Likes in given database...");
    }

    public void createTablePollOptions() throws SQLException {
        String sql = "CREATE TABLE `Poll options` " +
                "(`option id` INTEGER PRIMARY KEY AUTO_INCREMENT," +
                "`poll id` INTEGER," +
                "`option text` VARCHAR(256)," +
                "FOREIGN KEY (`poll id`) REFERENCES Tweets(id))";
        stmt.executeUpdate(sql);
        System.out.println("Created table Poll options in given database...");
    }

    public void createTableHashtags() throws SQLException {
        String sql = "CREATE TABLE Hashtags " +
                "(`tweet id` INTEGER," +
                "`hashtag name` VARCHAR(50)," +
                " date DATETIME," +
                "FOREIGN KEY (`tweet id`) REFERENCES Tweets(id))";
        stmt.executeUpdate(sql);
        System.out.println("Created table Hashtags in given database...");
    }

    public void createTablePollResults() throws SQLException {
        String sql = "CREATE TABLE `Poll results` " +
                "(`choice id` INTEGER," +
                "`poll id` INTEGER," +
                "username VARCHAR(20)," +
                "FOREIGN KEY (username) REFERENCES Profiles(username)," +
                "FOREIGN KEY (`choice id`) REFERENCES `Poll options`(`option id`)," +
                "FOREIGN KEY (`poll id`) REFERENCES Tweets(id))";
        stmt.executeUpdate(sql);
        System.out.println("Created table Poll results in given database...");
    }

    /**
     * This method is used to store images and videos of a tweet
     */
    public void addMedia(int tweetId, String type, String url, HttpResponse httpResponse) throws SQLException {
        if (!tweetExists(tweetId, httpResponse)){
            return;
        }
        PreparedStatement pstmt = conn.prepareStatement("INSERT INTO `media`" +
                "(`tweet id`, `type`, `url`)" +
                "VALUES(?,?,?)");
        pstmt.setInt(1, tweetId);
        pstmt.setString(2, type);
        pstmt.setString(3, url);
        pstmt.executeUpdate();
        if (httpResponse != null) {
            httpResponse.writeResponse(201, "Media added successfully");
            httpResponse.setGotResponse(true);
        }
    }

    /**
     * This method returns url of images of a tweet by getting tweet id
     */
    public ArrayList<String> getTweetImages(int tweetId, HttpResponse httpResponse) throws SQLException {
        if (!tweetExists(tweetId, httpResponse)){
            return null;
        }
        ArrayList<String> urlOfImages = new ArrayList<>();
        PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM media WHERE `tweet id` = ? AND `type` = ?");
        pstmt.setInt(1, tweetId);
        pstmt.setString(2, "image");
        ResultSet rs = pstmt.executeQuery();
        String url;
        while (rs.next()) {
            url = rs.getString("url");
            urlOfImages.add(url);
        }
        if (!urlOfImages.isEmpty() && httpResponse != null) {
            PrintWriter writer = httpResponse.getWriter();
            httpResponse.writeJsonResponse(200, null);
            writer.println(JsonHelper.toJson(urlOfImages));
            httpResponse.setGotResponse(true);
        } else if (urlOfImages.isEmpty() && httpResponse != null) {
            httpResponse.writeResponse(200, "No images ...");
            httpResponse.setGotResponse(true);
        }
        return urlOfImages;
    }

    /**
     * This method returns url of images of a tweet by getting tweet id
     */
    public ArrayList<String> getTweetVideos(int tweetId, HttpResponse httpResponse) throws SQLException {
        if (!tweetExists(tweetId, httpResponse)){
            return null;
        }
        ArrayList<String> urlOfVideos = new ArrayList<>();
        PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM media WHERE `tweet id` = ? AND `type` = ?");
        pstmt.setInt(1, tweetId);
        pstmt.setString(2, "video");
        ResultSet rs = pstmt.executeQuery();
        String url;
        while (rs.next()) {
            url = rs.getString("url");
            urlOfVideos.add(url);
        }
        if (!urlOfVideos.isEmpty() && httpResponse != null) {
            httpResponse.writeJsonResponse(200, null);
            httpResponse.setGotResponse(true);
        } else if (urlOfVideos.isEmpty() && httpResponse != null) {
            httpResponse.writeResponse(200, "No videos ...");
            httpResponse.setGotResponse(true);
        }
        return urlOfVideos;
    }

    /**
     * This method is used for posting only default/default polls tweets
     */
    public int addTweet(Tweet newTweet, String type, HttpResponse httpResponse) throws SQLException {
        if(httpResponse != null && !Database.getProfileDatabaseInstance().usernameExists(newTweet.getProfile().getUsername(), httpResponse)){
            return 0;
        }
        PreparedStatement pstmt = conn.prepareStatement("INSERT INTO `tweets`" +
                "(`username`,`type`,`text`,`date`)" +
                "VALUES(?,?,?,CURRENT_TIMESTAMP)");
        pstmt.setString(1, newTweet.getProfile().getUsername());
        pstmt.setString(2, type);
        pstmt.setString(3, newTweet.getText());
        int id;
        synchronized (this){
            pstmt.executeUpdate();
            id = lastInsertedTweetId();
        }
        if(newTweet.getText() != null)
            addHashtag(id, Hashtag.findHashtags(newTweet.getText()), null);
        if (httpResponse != null) {
            if(type.equals("default"))
                type = "tweet";
            httpResponse.writeResponse(201, type + " " + id + " posted successfully");
            httpResponse.setGotResponse(true);
        }
        return id;
    }

    public ArrayList<Hashtag> searchHashtag(String str, HttpResponse httpResponse) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM hashtags WHERE `hashtag name` LIKE ?");
        pstmt.setString(1, "%" + str + "%");
        ResultSet rs = pstmt.executeQuery();
        ArrayList<Hashtag> hashtags = toHashtagObject(rs);
        if(hashtags.isEmpty() && httpResponse != null){
            httpResponse.writeResponse(200, "No hashtags found ...");
            httpResponse.setGotResponse(true);
        } else if (!hashtags.isEmpty() && httpResponse != null) {
            PrintWriter writer = httpResponse.getWriter();
            httpResponse.writeJsonResponse(200, null);
            writer.println(JsonHelper.toJsonWithAdapter(hashtags));
            httpResponse.setGotResponse(true);
        }
        return hashtags;
    }

    public HashMap<Hashtag, Integer> countHashtags(HttpResponse httpResponse) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM hashtags");
        ResultSet rs = pstmt.executeQuery();
        HashMap<Hashtag, Integer> hashtags = new HashMap<>();
        Hashtag hashtag;
        while (rs.next()) {
            int tweetId = rs.getInt("tweet id");
            String hashtagName = rs.getString("hashtag name");
            LocalDateTime date = rs.getTimestamp("date").toLocalDateTime();
            hashtag = new Hashtag(tweetId, hashtagName, date);
            if(hashtags.containsKey(hashtag)) {
                hashtags.put(hashtag, hashtags.get(hashtag) + 1);
            } else {
                hashtags.put(hashtag, 1);
            }
        }
        if(hashtags.isEmpty() && httpResponse != null){
            httpResponse.writeResponse(200, "No hashtags found ...");
            httpResponse.setGotResponse(true);
        } else if (!hashtags.isEmpty() && httpResponse != null) {
            PrintWriter writer = httpResponse.getWriter();
            httpResponse.writeJsonResponse(200, null);
            writer.println(JsonHelper.toJsonWithAdapter(hashtags));
            httpResponse.setGotResponse(true);
        }
        return hashtags;
    }

    public HashMap<LocalDate, Integer> countTotalHashtags(HttpResponse httpResponse) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement("SELECT DATE(date) AS day, COUNT(*) AS count FROM Hashtags GROUP BY day");
        ResultSet rs = pstmt.executeQuery();
        HashMap<LocalDate, Integer> totalHashtags = new HashMap<>();
        while (rs.next()) {
            String dayString = rs.getString("day");
            int count = rs.getInt("count");
            LocalDate day = LocalDate.parse(dayString);
            totalHashtags.put(day, count);
        }
        if (httpResponse != null) {
            PrintWriter writer = httpResponse.getWriter();
            httpResponse.writeJsonResponse(200, null);
            writer.println(JsonHelper.toJsonWithAdapter(totalHashtags));
            httpResponse.setGotResponse(true);
        }
        return totalHashtags;
    }

    public HashMap<LocalDate, Integer> countHashtags(String hashtagName, HttpResponse httpResponse) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement("SELECT DATE(date) AS day, COUNT(*) AS count FROM Hashtags WHERE `hashtag name` = ? GROUP BY day");
        pstmt.setString(1, hashtagName);
        ResultSet rs = pstmt.executeQuery();
        HashMap<LocalDate, Integer> totalHashtags = new HashMap<>();
        while (rs.next()) {
            String dayString = rs.getString("day");
            int count = rs.getInt("count");
            LocalDate day = LocalDate.parse(dayString);
            totalHashtags.put(day, count);
        }
        if (httpResponse != null) {
            PrintWriter writer = httpResponse.getWriter();
            httpResponse.writeJsonResponse(200, null);
            writer.println(JsonHelper.toJsonWithAdapter(totalHashtags));
            httpResponse.setGotResponse(true);
        }
        return totalHashtags;
    }

    /**
     * This method is used for posting retweet/non default poll/reply tweets
     */
    public void addTweet(Tweet newTweet, String type, int aboutTweet, HttpResponse httpResponse) throws SQLException {
        if(!Database.getProfileDatabaseInstance().usernameExists(newTweet.getProfile().getUsername(), httpResponse)){
            return;
        }
        if (!tweetExists(aboutTweet, httpResponse)){
            return;
        }
        if(type.equals("retweet") && newTweet.getText() != null && !newTweet.getText().trim().isEmpty())
            type = "quote";
        PreparedStatement pstmt = conn.prepareStatement("INSERT INTO `tweets`" +
                "(`username`,`type`,`text`,`date`,`about tweet`)" +
                "VALUES(?,?,?,CURRENT_TIMESTAMP,?)");
        pstmt.setString(1, newTweet.getProfile().getUsername());
        pstmt.setString(2, type);
        pstmt.setString(3, newTweet.getText());
        pstmt.setInt(4, aboutTweet);
        int id;
        synchronized (this){
            pstmt.executeUpdate();
            id = lastInsertedTweetId();
        }
        if(newTweet.getText() != null)
            addHashtag(id, Hashtag.findHashtags(newTweet.getText()), null);
        if (httpResponse != null) {
            httpResponse.writeResponse(201, type + " " + id + " posted successfully");
            httpResponse.setGotResponse(true);
        }
    }

    public int countVotes(int pollId, int choiceId) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) FROM `poll results` WHERE `choice id`  = ? AND `poll id` = ?");
        pstmt.setInt(1, choiceId);
        pstmt.setInt(2, pollId);
        ResultSet rs = pstmt.executeQuery();
        rs.next();
        int counter = rs.getInt("count(*)");
        return counter;
    }

    /**
     * This method returns all options and number of votes for each option of a poll
     */
    public HashMap<String, Integer> getPollResults(int pollId) throws SQLException{
        HashMap<String, Integer> choices = new HashMap<>();
        PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM `poll options` WHERE `poll id` = ?");
        pstmt.setInt(1, pollId);
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            int choiceId = rs.getInt("option id");
            String choice = rs.getString("option text");
            int numberOfVotes = countVotes(pollId, choiceId);
            choices.put(choice, numberOfVotes);
        }
        if (choices.isEmpty()) {
            //there isn't any choices for this poll
        }
        return choices;
    }

    public boolean isLikedBy(int tweetId, String username, HttpResponse httpResponse) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) FROM likes WHERE `username`  = ? AND `tweet id` = ?");
        pstmt.setString(1, username);
        pstmt.setInt(2, tweetId);
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

    public void like(int tweetId, String username, HttpResponse httpResponse) throws SQLException {
        if(!Database.getProfileDatabaseInstance().usernameExists(username, httpResponse)){
            return;
        }
        if (!tweetExists(tweetId, httpResponse)){
            return;
        }
        if(isLikedBy(tweetId, username, null)){
            if (httpResponse != null) {
                httpResponse.writeResponse(400, "Cannot like a message that has been liked.");
                httpResponse.setGotResponse(true);
            }
            return;
        }
        PreparedStatement pstmt = conn.prepareStatement("INSERT INTO `likes`" +
                "(`tweet id`,`username`,`date`) " +
                "VALUES(?,?,CURRENT_TIMESTAMP)");
        pstmt.setInt(1, tweetId);
        pstmt.setString(2, username);
        pstmt.executeUpdate();
        if (httpResponse != null) {
            httpResponse.writeResponse(201, "Like successful.");
            httpResponse.setGotResponse(true);
        }
    }

    public void unLike(int tweetId, String username, HttpResponse httpResponse) throws SQLException {
        if(!Database.getProfileDatabaseInstance().usernameExists(username, httpResponse)){
            return;
        }
        if (!tweetExists(tweetId, httpResponse)){
            return;
        }
        if(!isLikedBy(tweetId, username, null)){
            if (httpResponse != null) {
                httpResponse.writeResponse(400, "Cannot unlike a message that hasn't been liked.");
                httpResponse.setGotResponse(true);
            }
            return;
        }
        PreparedStatement pstmt = conn.prepareStatement("DELETE FROM `likes`" +
                "WHERE username = ? AND `tweet id` = ?");
        pstmt.setString(1, username);
        pstmt.setInt(2, tweetId);
        pstmt.executeUpdate();
        if (httpResponse != null) {
            httpResponse.writeResponse(200, "Unlike successful.");
            httpResponse.setGotResponse(true);
        }
    }


    public int countLike(int tweetId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM likes WHERE `tweet id`  = " + tweetId;
        ResultSet rs = stmt.executeQuery(sql);
        rs.next();
        int counter = rs.getInt("count(*)");
        return counter;
    }

    /**
     * This method returns the profiles that likes a specific tweet
     */
    public ArrayList<Profile> getLike(int tweetId, HttpResponse httpResponse) throws SQLException{
        if (!tweetExists(tweetId, httpResponse)){
            return null;
        }
        ArrayList<Profile> likes = new ArrayList<>();
        PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM likes WHERE `tweet id`  = ? ORDER BY date DESC");
        pstmt.setInt(1, tweetId);
        ResultSet rs = pstmt.executeQuery();
        Profile profile;
        while (rs.next()) {
            String username = rs.getString("username");
            profile = Database.getProfileDatabaseInstance().getProfile(username, false);
            likes.add(profile);
        }
        if(likes.isEmpty() && httpResponse != null){
            httpResponse.writeResponse(200, "No likes ...");
            httpResponse.setGotResponse(true);
        } else if (!likes.isEmpty() && httpResponse != null) {
            PrintWriter writer = httpResponse.getWriter();
            httpResponse.writeJsonResponse(200, null);
            writer.println(JsonHelper.toJsonWithAdapter(likes));
            httpResponse.setGotResponse(true);
        }
        return likes;
    }

    /**
     *  This method check if a specific tweet is favstar or not
     */
    public boolean isFavstar(int tweetId) throws SQLException {
        int likes = countLike(tweetId);
        if (likes >= 10)
            return true;
        else
            return false;
    }

    public boolean tweetExists(int tweetId, HttpResponse httpResponse) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) FROM tweets WHERE `id` = ?");
        pstmt.setInt(1, tweetId);
        ResultSet rs = pstmt.executeQuery();
        rs.next();
        int counter = rs.getInt("count(*)");
        if (counter == 0){
            if (httpResponse != null) {
                httpResponse.writeResponse(404, "Tweet not found.");
                httpResponse.setGotResponse(true);
            }
            return false;
        }
        else
            return true;
    }

    public boolean isPoll(int pollId, HttpResponse httpResponse) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) FROM `poll options` WHERE `poll id` = ?");
        pstmt.setInt(1, pollId);
        ResultSet rs = pstmt.executeQuery();
        rs.next();
        int counter = rs.getInt("count(*)");
        if (counter == 0){
            if (httpResponse != null) {
                httpResponse.writeResponse(404, "Poll not found.");
                httpResponse.setGotResponse(true);
            }
            return false;
        }
        else
            return true;
    }

    public boolean choiceExists(int pollId, int choiceId, HttpResponse httpResponse) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) FROM `poll options` WHERE `poll id` = ? AND `option id` = ?");
        pstmt.setInt(1, pollId);
        pstmt.setInt(2, choiceId);
        ResultSet rs = pstmt.executeQuery();
        rs.next();
        int counter = rs.getInt("count(*)");
        if (counter == 0){
            if (httpResponse != null) {
                httpResponse.writeResponse(404, "Choice not found.");
                httpResponse.setGotResponse(true);
            }
            return false;
        }
        else
            return true;
    }

    /**
     * this method can be used to add multiple hashtags of a tweet
     */
    public void addHashtag(int tweetId, String hashtagName, HttpResponse httpResponse) throws SQLException {
        if (!tweetExists(tweetId, httpResponse)){
            return;
        }
        PreparedStatement pstmt = conn.prepareStatement("INSERT INTO `hashtags`" +
                "(`tweet id`,`hashtag name`,`date`) " +
                "VALUES(?,?,CURRENT_TIMESTAMP)");
        pstmt.setInt(1, tweetId);
        pstmt.setString(2, hashtagName);
        pstmt.executeUpdate();
        if (httpResponse != null) {
            httpResponse.writeResponse(201, "Hashtag added successfully");
            httpResponse.setGotResponse(true);
        }
    }

    private void addHashtag(int tweetId, ArrayList<String> hashtags, HttpResponse httpResponse) throws SQLException {
        if (!tweetExists(tweetId, httpResponse)){
            return;
        }
        for (String hashtag : hashtags){
            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO `hashtags`" +
                    "(`tweet id`,`hashtag name`,`date`) " +
                    "VALUES(?,?,CURRENT_TIMESTAMP)");
            pstmt.setInt(1, tweetId);
            pstmt.setString(2, hashtag);
            pstmt.executeUpdate();
        }
        if (httpResponse != null) {
            httpResponse.writeResponse(201, "Hashtags added successfully");
            httpResponse.setGotResponse(true);
        }
    }

    public void addPoll(Poll newPoll, HttpResponse httpResponse) throws SQLException {
        if(!Database.getProfileDatabaseInstance().usernameExists(newPoll.getProfile().getUsername(), httpResponse)){
            return;
        }
        if(newPoll.getResults() == null || newPoll.getChoices().isEmpty()){
            httpResponse.writeResponse(400, "Invalid request: Poll options are required.");
            return;
        }
        int pollId = addTweet(newPoll, "poll", null);
        if(httpResponse.getGotResponse())
            return;
        for (String choice : newPoll.getChoices()) {
            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO `poll options`" +
                    "(`poll id`,`option text`)" +
                    "VALUES(?,?)");
            pstmt.setInt(1, pollId);
            pstmt.setString(2, choice);
            pstmt.executeUpdate();
        }
        if (httpResponse != null) {
            httpResponse.writeResponse(201, "Poll " + pollId + " posted successfully");
            httpResponse.setGotResponse(true);
        }
    }

    /**
     * This method will return the id of the last tweet that has been inserted
     */
    private int lastInsertedTweetId() throws SQLException{
        String sql = "SELECT MAX(id) FROM tweets";
        ResultSet rs = stmt.executeQuery(sql);
        rs.next();
        return rs.getInt("MAX(id)");
    }

    public void vote(int pollId, int choiceId, String username, HttpResponse httpResponse) throws SQLException {
        if(!Database.getProfileDatabaseInstance().usernameExists(username, httpResponse)){
            return;
        }
        if(!Database.getProfileDatabaseInstance().usernameExists(username, httpResponse)){
            return;
        }
        if (!isPoll(pollId, httpResponse) || !choiceExists(pollId, choiceId, httpResponse)){
            return;
        }
        if(hasVotedBefore(username, pollId, null)){
            if (httpResponse != null) {
                httpResponse.writeResponse(403, "You are not allowed to vote again.");
                httpResponse.setGotResponse(true);
            }
            return;
        }
        PreparedStatement pstmt = conn.prepareStatement("INSERT INTO `poll results`" +
                "(`choice id`,`poll id`,`username`)" +
                "VALUES(?,?,?)");
        pstmt.setInt(1, choiceId);
        pstmt.setInt(2, pollId);
        pstmt.setString(3, username);
        pstmt.executeUpdate();
        if (httpResponse != null) {
            httpResponse.writeResponse(201, "your vote has been recorded successfully.");
            httpResponse.setGotResponse(true);
        }
    }

    public void voteWithChoiceText(int pollId, String choiceText, String username, HttpResponse httpResponse) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM `poll options` WHERE `poll id` = ? AND `option text` = ?");
        pstmt.setInt(1, pollId);
        pstmt.setString(2,choiceText);
        ResultSet rs = pstmt.executeQuery();
        int choiceId = 0;
        while (rs.next()) {
            choiceId = rs.getInt("option id");
            break;
        }
        vote(pollId, choiceId, username, httpResponse);
    }

    //this method is to check if the user has voted before or not
    public boolean hasVotedBefore(String username, int pollId, HttpResponse httpResponse) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) FROM `poll results` WHERE `username`  = ? AND `poll id` = ?");
        pstmt.setString(1, username);
        pstmt.setInt(2, pollId);
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

    public ArrayList<Tweet> timeLine(String username, HttpResponse httpResponse) throws SQLException {
        if(!Database.getProfileDatabaseInstance().usernameExists(username, httpResponse)){
            return null;
        }
        if(!Database.getProfileDatabaseInstance().usernameExists(username, httpResponse)){
            return null;
        }
        ArrayList<Tweet> tweets = new ArrayList<>();
        String senderUsername;
        int tweetId;
        PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM tweets ORDER BY date DESC");
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            senderUsername = rs.getString("username");
            tweetId = rs.getInt("id");
            if (!Database.getProfileDatabaseInstance().isBlocked(username, senderUsername) &&
                    (Database.getProfileDatabaseInstance().isFollowedBy(username, senderUsername, null) || isFavstar(tweetId)))
                tweets.add(getTweet(tweetId));
        }
        if(tweets.isEmpty() && httpResponse != null){
            httpResponse.writeResponse(200, "No tweets ...");
            httpResponse.setGotResponse(true);
        } else if (!tweets.isEmpty() && httpResponse != null) {
            PrintWriter writer = httpResponse.getWriter();
            httpResponse.writeJsonResponse(200, null);
            writer.println(JsonHelper.toJsonWithAdapter(tweets));
            httpResponse.setGotResponse(true);
        }
        return tweets;
    }


    public ArrayList<Tweet> getProfileTweets(String username, HttpResponse httpResponse) throws SQLException {
        if(!Database.getProfileDatabaseInstance().usernameExists(username, httpResponse)){
            return null;
        }
        if(!Database.getProfileDatabaseInstance().usernameExists(username, httpResponse)){
            return null;
        }
        ArrayList<Tweet> tweets = new ArrayList<>();
        int tweetId;
        PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM tweets where `username` = ? ORDER BY date DESC");
        pstmt.setString(1, username);
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            tweetId = rs.getInt("id");
            tweets.add(getTweet(tweetId));
        }
        if(tweets.isEmpty() && httpResponse != null){
            httpResponse.writeResponse(200, "No tweets ...");
            httpResponse.setGotResponse(true);
        } else if (!tweets.isEmpty() && httpResponse != null) {
            PrintWriter writer = httpResponse.getWriter();
            httpResponse.writeJsonResponse(200, null);
            writer.println(JsonHelper.toJsonWithAdapter(tweets));
            httpResponse.setGotResponse(true);
        }
        return tweets;
    }

    /**
     * This method can be used to count number of retweets/quotes/replies
     */
    public int countTweetAttributes(int aboutTweet, String type) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) FROM tweets WHERE `about tweet` = ? AND `type` = ?");
        pstmt.setInt(1, aboutTweet);
        pstmt.setString(2, type);
        ResultSet rs = pstmt.executeQuery();
        rs.next();
        int counter = rs.getInt("count(*)");
        return counter;
    }

    /**
     * This method is used to get replies of a tweet by getting its id(sorted)
     */
    public ArrayList<Tweet> getReplies(int tweetId, HttpResponse httpResponse) throws SQLException {
        if (!tweetExists(tweetId, httpResponse)){
            return null;
        }
        ArrayList<Tweet> replies;
        PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM tweets WHERE `about tweet` = ? AND `type` = ? ORDER BY date DESC");
        pstmt.setInt(1, tweetId);
        pstmt.setString(2, "reply");
        ResultSet rs = pstmt.executeQuery();
        replies = toTweetObject(rs, true);
        if(replies.isEmpty() && httpResponse != null){
            httpResponse.writeResponse(200, "No replies ...");
            httpResponse.setGotResponse(true);
        } else if (!replies.isEmpty() && httpResponse != null) {
            PrintWriter writer = httpResponse.getWriter();
            httpResponse.writeJsonResponse(200, null);
            writer.println(JsonHelper.toJsonWithAdapter(replies));
            httpResponse.setGotResponse(true);
        }
        return replies;
    }

    /**
     * This method is used to get replies of a tweet by getting its id(sorted) with considering blocked profiles
     */
    public ArrayList<Tweet> getReplies(int tweetId, String username, HttpResponse httpResponse) throws SQLException {
        if (!tweetExists(tweetId, httpResponse)){
            return null;
        }
        ArrayList<Tweet> replies;
        PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM tweets WHERE `about tweet` = ? AND `type` = ? ORDER BY date DESC");
        pstmt.setInt(1, tweetId);
        pstmt.setString(2, "reply");
        ResultSet rs = pstmt.executeQuery();
        replies = toTweetObject(rs, true);
        removeBlockedTweets(replies, username);
        if(replies.isEmpty() && httpResponse != null){
            httpResponse.writeResponse(200, "No replies ...");
            httpResponse.setGotResponse(true);
        } else if (!replies.isEmpty() && httpResponse != null) {
            PrintWriter writer = httpResponse.getWriter();
            httpResponse.writeJsonResponse(200, null);
            writer.println(JsonHelper.toJsonWithAdapter(replies));
            httpResponse.setGotResponse(true);
        }
        return replies;
    }

    /**
     * This method is used to get retweet or quotes of a tweet by getting its id(sorted)
     */
    public ArrayList<Tweet> getRetweets(int tweetId, String type, HttpResponse httpResponse) throws SQLException {
        if (!tweetExists(tweetId, httpResponse)){
            return null;
        }
        ArrayList<Tweet> retweets;
        PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM tweets WHERE `about tweet` = ? AND `type` = ? ORDER BY date DESC");
        pstmt.setInt(1, tweetId);
        pstmt.setString(2, type);
        ResultSet rs = pstmt.executeQuery();
        retweets = toTweetObject(rs, true);
        if(retweets.isEmpty() && httpResponse != null){
            httpResponse.writeResponse(200, "No " + type + "s ...");
            httpResponse.setGotResponse(true);
        } else if (!retweets.isEmpty() && httpResponse != null) {
            PrintWriter writer = httpResponse.getWriter();
            httpResponse.writeJsonResponse(200, null);
            writer.println(JsonHelper.toJsonWithAdapter(retweets));
            httpResponse.setGotResponse(true);
        }
        return retweets;
    }

    /**
     * This method is used to get retweet or quotes of a tweet by getting its id(sorted) with considering blocked profiles
     */
    public ArrayList<Tweet> getRetweets(int tweetId, String username, String type, HttpResponse httpResponse) throws SQLException {
        if (!tweetExists(tweetId, httpResponse)){
            return null;
        }
        ArrayList<Tweet> retweets;
        PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM tweets WHERE `about tweet` = ? AND `type` = ? ORDER BY date DESC");
        pstmt.setInt(1, tweetId);
        pstmt.setString(2, type);
        ResultSet rs = pstmt.executeQuery();
        retweets = toTweetObject(rs, true);
        removeBlockedTweets(retweets, username);
        if(retweets.isEmpty() && httpResponse != null){
            httpResponse.writeResponse(200, "No " + type + "s ...");
            httpResponse.setGotResponse(true);
        } else if (!retweets.isEmpty() && httpResponse != null) {
            PrintWriter writer = httpResponse.getWriter();
            httpResponse.writeJsonResponse(200, null);
            writer.println(JsonHelper.toJsonWithAdapter(retweets));
            httpResponse.setGotResponse(true);
        }
        return retweets;
    }

    /**
     * This method is used to get retweet and quotes of a tweet by getting its id(sorted) with considering blocked profiles
     */
    public ArrayList<Tweet> getRetweetsAndQuotes(int tweetId, String username, HttpResponse httpResponse) throws SQLException {
        if (!tweetExists(tweetId, httpResponse)){
            return null;
        }
        ArrayList<Tweet> retweets;
        PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM tweets WHERE `about tweet` = ? AND (`type` = ? OR `type` = ?) ORDER BY date DESC");
        pstmt.setInt(1, tweetId);
        pstmt.setString(2, "quote");
        pstmt.setString(3, "retweet");
        ResultSet rs = pstmt.executeQuery();
        retweets = toTweetObject(rs, true);
        removeBlockedTweets(retweets, username);
        if(retweets.isEmpty() && httpResponse != null){
            httpResponse.writeResponse(200, "No " + "retweets or quotes" + " ...");
            httpResponse.setGotResponse(true);
        } else if (!retweets.isEmpty() && httpResponse != null) {
            PrintWriter writer = httpResponse.getWriter();
            httpResponse.writeJsonResponse(200, null);
            writer.println(JsonHelper.toJsonWithAdapter(retweets));
            httpResponse.setGotResponse(true);
        }
        return retweets;
    }

    public boolean hashtagExists(String hashtagName) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) FROM hashtags WHERE `hashtag name` = ?");
        pstmt.setString(1, hashtagName);
        ResultSet rs = pstmt.executeQuery();
        rs.next();
        int counter = rs.getInt("count(*)");
        if (counter == 0)
            return false;
        else
            return true;
    }

    /**
     * This method returns an arraylist of tweetIds that contains a specific hashtag(sorted) with considering blocked profiles
     */
    public ArrayList<Tweet> findTweetOfHashtag(String username, String hashtagName, HttpResponse httpResponse) throws SQLException {
        ArrayList<Tweet> tweets = new ArrayList<>();
        PreparedStatement pstmt = conn.prepareStatement("SELECT tweets.id, tweets.username, tweets.text," +
                " tweets.date from tweets JOIN hashtags ON tweets.id = hashtags.`tweet id` WHERE" +
                " hashtags.`hashtag name` = ? ORDER BY date DESC");
        pstmt.setString(1, hashtagName);
        ResultSet rs = pstmt.executeQuery();
        String senderUsername;
        int tweetId;
        while (rs.next()) {
            senderUsername = rs.getString("username");
            tweetId = rs.getInt("id");
            if (!Database.getProfileDatabaseInstance().isBlocked(username, senderUsername))
                tweets.add(getTweet(tweetId));
        }
        if(tweets.isEmpty() && httpResponse != null){
            httpResponse.writeResponse(200, "No tweets ...");
            httpResponse.setGotResponse(true);
        } else if (!tweets.isEmpty() && httpResponse != null) {
            PrintWriter writer = httpResponse.getWriter();
            httpResponse.writeJsonResponse(200, null);
            writer.println(JsonHelper.toJsonWithAdapter(tweets));
            httpResponse.setGotResponse(true);
        }
        return tweets;
    }

    /**
     * This method returns an arraylist of tweetIds that contains a specific hashtag(sorted)
     */
    public ArrayList<Tweet> findTweetOfHashtag(String hashtagName, HttpResponse httpResponse) throws SQLException {
        ArrayList<Tweet> tweets = new ArrayList<>();
        PreparedStatement pstmt = conn.prepareStatement("SELECT tweets.id, tweets.username, tweets.text," +
                " tweets.date from tweets JOIN hashtags ON tweets.id = hashtags.`tweet id` WHERE" +
                " hashtags.`hashtag name` = ? ORDER BY date DESC");
        pstmt.setString(1, hashtagName);
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            int id = rs.getInt("id");
            tweets.add(getTweet(id));
        }
        if(tweets.isEmpty() && httpResponse != null){
            httpResponse.writeResponse(200, "No tweets ...");
            httpResponse.setGotResponse(true);
        } else if (!tweets.isEmpty() && httpResponse != null) {
            PrintWriter writer = httpResponse.getWriter();
            httpResponse.writeJsonResponse(200, null);
            writer.println(JsonHelper.toJsonWithAdapter(tweets));
            httpResponse.setGotResponse(true);
        }
        return tweets;
    }

    public ArrayList<String> getChoices(int pollId) throws SQLException {
        ArrayList<String> choices = new ArrayList<>();
        PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM `poll options` WHERE `poll id` = ?");
        pstmt.setInt(1, pollId);
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            String choice = rs.getString("option text");
            choices.add(choice);
        }
        if (choices.isEmpty()) {
            //there isn't any choices for this poll
        }
        return choices;
    }

    /**
     * This method can be used to get any kind of tweet
     */
    public void getTweet(int id, HttpResponse httpResponse) throws SQLException{
        PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM tweets WHERE `id` = ?");
        pstmt.setInt(1, id);
        ResultSet rs = pstmt.executeQuery();
        ArrayList<Tweet> tweets = toTweetObject(rs, true);
        if(tweets.isEmpty() && httpResponse != null){
            httpResponse.writeResponse(200, "Tweet not found ...");
            httpResponse.setGotResponse(true);
        } else if (!tweets.isEmpty() && httpResponse != null) {
            PrintWriter writer = httpResponse.getWriter();
            httpResponse.writeJsonResponse(200, null);
            writer.println(JsonHelper.toJsonWithAdapter(tweets));
            httpResponse.setGotResponse(true);
        }
    }

    /**
     * This method can be used to get any kind of tweet
     */
    public Tweet getTweet(int id) throws SQLException{
        PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM tweets WHERE `id` = ?");
        pstmt.setInt(1, id);
        ResultSet rs = pstmt.executeQuery();
        ArrayList<Tweet> tweets = toTweetObject(rs, true);
        if(tweets.isEmpty())
            return null;
        else
            return tweets.get(0);
    }

    public ArrayList<Tweet> toTweetObject(ResultSet rs, boolean complete) throws SQLException {
        ArrayList<Tweet> tweets = new ArrayList<>();
        while (rs.next()) {
            int id = rs.getInt("id");
            String username = rs.getString("username");
            int aboutTweet = rs.getInt("about tweet");
            String text = rs.getString("text");
            String type = rs.getString("type");
            LocalDateTime date = rs.getTimestamp("date").toLocalDateTime();
            Profile profile = Database.getProfileDatabaseInstance().getProfile(username, false);
            Tweet tweet;
            if(complete){
                int numberOfLikes = countLike(id);
                int numberOfRetweets = countTweetAttributes(id, "retweet");
                int numberOfQuotes = countTweetAttributes(id, "quote");
                int numberOfReplies = countTweetAttributes(id, "reply");
                switch (type) {
                    case "default":
                        tweet = new Tweet(id, profile, text, type, date, numberOfLikes, numberOfRetweets, numberOfQuotes, numberOfReplies);
                        break;
                    case "quote":
                    case "retweet":
                        tweet = new Retweet(id, profile, text, type, date, numberOfLikes, numberOfRetweets, numberOfQuotes, numberOfReplies, aboutTweet);
                        break;
                    case "reply":
                        tweet = new Reply(id, profile, text, type, date, numberOfLikes, numberOfRetweets, numberOfQuotes, numberOfReplies, aboutTweet);
                        break;
                    case "poll":
                        HashMap<String, Integer> choices = getPollResults(id);
                        tweet = new Poll(id, profile, text, type, date, numberOfLikes, numberOfRetweets, numberOfQuotes, numberOfReplies, choices);
                        break;
                    default:
                        throw new SQLException();
                }
            }
            else {
                switch (type) {
                    case "default":
                        tweet = new Tweet(id, profile, text, type, date);
                        break;
                    case "quote":
                    case "retweet":
                        tweet = new Retweet(id, profile, text, type, date, aboutTweet);
                        break;
                    case "reply":
                        tweet = new Reply(id, profile, text, type, date, aboutTweet);
                        break;
                    case "poll":
                        HashMap<String, Integer> choices = getPollResults(id);
                        tweet = new Poll(id, profile, text, type, date, choices);
                        break;
                    default:
                        throw new SQLException();
                }
            }
            tweets.add(tweet);
        }
        return tweets;
    }

    public ArrayList<Hashtag> toHashtagObject(ResultSet rs) throws SQLException {
        ArrayList<Hashtag> hashtags = new ArrayList<>();
        ArrayList<String> hashtagNames = new ArrayList<>();
        Hashtag hashtag;
        while (rs.next()) {
            int tweetId = rs.getInt("tweet id");
            String hashtagName = rs.getString("hashtag name");
            LocalDateTime date = rs.getTimestamp("date").toLocalDateTime();
            hashtag = new Hashtag(tweetId, hashtagName, date);
            if(!hashtagNames.contains(hashtagName)) {
                hashtagNames.add(hashtagName);
                hashtags.add(hashtag);
            }
        }
        return hashtags;
    }

    public void removeBlockedTweets(ArrayList<Tweet> tweets, String username) throws SQLException {
        Iterator<Tweet> iterator = tweets.iterator();
        while (iterator.hasNext()) {
            Tweet tweet = iterator.next();
            if (Database.getProfileDatabaseInstance().isBlocked(username, tweet.getProfile().getUsername()) ||
                    Database.getProfileDatabaseInstance().isBlocked(tweet.getProfile().getUsername(), username)) {
                iterator.remove();
            }
        }
    }
}
