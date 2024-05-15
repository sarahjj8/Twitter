package model.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Profile {
    private User user;
    private String username;
    private String password;
    private String bio;
    private String location;
    private String websiteAddress;
    private String avatarPath;
    private String headerPath;
    private Date lastModified;
    private int numberOfFollowers;
    private int numberOfFollowing;
    private HashMap<String, Direct> directs;   //the key is a username
    private ArrayList<String> blocked;  //an arrayList of blocked profiles

    public Profile(String name, String lastName, String username) {
        this.username = username;
        this.user = new User(name, lastName);
    }

    public Profile(String name, String lastName, String username, String avatarPath) {
        this.username = username;
        this.avatarPath = avatarPath;
        this.user = new User(name, lastName);
    }

    public Profile(String name, String lastName, Date createdDate, String username, String bio, String location, String websiteAddress,
                   String avatarUrl, String headerUrl, int numberOfFollowers, int numberOfFollowing) {
        this.user = new User(name, lastName, createdDate);
        this.username = username;
        this.bio = bio;
        this.location = location;
        this.websiteAddress = websiteAddress;
        this.avatarPath = avatarUrl;
        this.headerPath = headerUrl;
        this.numberOfFollowers = numberOfFollowers;
        this.numberOfFollowing = numberOfFollowing;
    }

    public String getUsername() {
        return username;
    }

    public String getBio() {
        return bio;
    }

    public String getLocation() {
        return location;
    }

    public String getWebsiteAddress() {
        return websiteAddress;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public String getHeaderPath() {
        return headerPath;
    }

    public User getUser() {
        return user;
    }

    public int getNumberOfFollowers() {
        return numberOfFollowers;
    }

    public int getNumberOfFollowing() {
        return numberOfFollowing;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setWebsiteAddress(String websiteAddress) {
        this.websiteAddress = websiteAddress;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }

    public void setHeaderPath(String headerPath) {
        this.headerPath = headerPath;
    }

    public void setNumberOfFollowers(int numberOfFollowers) {
        this.numberOfFollowers = numberOfFollowers;
    }

    public void setNumberOfFollowing(int numberOfFollowing) {
        this.numberOfFollowing = numberOfFollowing;
    }

    private static boolean checkPassword(String password){
        String regex = "^(?=.*[a-z])(?=.*[A-Z]).{8,}$";
        //Compile regular expression to get the pattern
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }
}
