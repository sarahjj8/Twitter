package model.entity;

import javafx.scene.image.Image;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


// ** images and videos are not added yet **

public class Tweet {
    Profile profile;
    private int id;
    private String text;
    private LocalDateTime date;
    private int numberOfLikes;
    private int numberOfRetweets;
    private int numberOfQuotes;
    private int numberOfReplies;
    private ArrayList<String> hashtags;
    private String type;
    public Tweet() {
    }

    public Tweet(int id, Profile profile, String text, String type, LocalDateTime date) {
        this.id = id;
        this.profile = profile;
        this.text = text;
        this.type = type;
        this.date = date;
    }

    public Tweet(int id, Profile profile, String text, String type, LocalDateTime date, int numberOfLikes, int numberOfRetweets, int numberOfQuotes, int numberOfReplies) {
        this.id = id;
        this.profile = profile;
        this.text = text;
        this.type = type;
        this.date = date;
        this.numberOfLikes = numberOfLikes;
        this.numberOfRetweets = numberOfRetweets;
        this.numberOfQuotes = numberOfQuotes;
        this.numberOfReplies = numberOfReplies;
    }

    public Profile getProfile() {
        return profile;
    }

    public int getId() {
        return id;
    }

    public int getNumberOfRetweets() {
        return numberOfRetweets;
    }

    public int getNumberOfQuotes() {
        return numberOfQuotes;
    }

    public int getNumberOfReplies() {
        return numberOfReplies;
    }

    public int getNumberOfLikes() {
        return numberOfLikes;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public void setProfile(String username){
        profile = new Profile(null, null, username);
    }

    public String getText() {
        return text;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public void setNumberOfLikes(int numberOfLikes) {
        this.numberOfLikes = numberOfLikes;
    }

    public void setNumberOfRetweets(int numberOfRetweets) {
        this.numberOfRetweets = numberOfRetweets;
    }

    public void setNumberOfQuotes(int numberOfQuotes) {
        this.numberOfQuotes = numberOfQuotes;
    }

    public void setNumberOfReplies(int numberOfReplies) {
        this.numberOfReplies = numberOfReplies;
    }

    public ArrayList<String> getHashtags() {
        return hashtags;
    }

    //this method is used to show how much time passed since the tweet posted
    public static String displayTimeInTimeline(Timestamp date){
        Date currentDate = new Date();
        LocalDateTime localDateTime = date.toLocalDateTime();
        long time_difference = currentDate.getTime() - date.getTime();
        long years_difference = (time_difference / (1000l*60*60*24*365));
        long days_difference = (time_difference / (1000*60*60*24)) % 365;
        long hours_difference = (time_difference / (1000*60*60)) % 24;
        long minutes_difference = (time_difference / (1000*60)) % 60;
        long seconds_difference = (time_difference / 1000)% 60;
//        String month = localDateTime.getMonth().toString().toLowerCase();
        String month = localDateTime.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
//        month = month.substring(0, 1).toUpperCase() + month.substring(1).toLowerCase();
        if(years_difference != 0)
            return localDateTime.getDayOfMonth() + " " + month + ", " + localDateTime.getYear();
        else if(days_difference != 0)
            return localDateTime.getDayOfMonth() + " " + month;
        else if (hours_difference != 0)
            return hours_difference + "h";
        else if (minutes_difference != 0)
            return minutes_difference + "m";
        else
            return "just now";
    }
}
