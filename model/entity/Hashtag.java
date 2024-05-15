package model.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Hashtag {
    private int tweetId;
    private String hashtagName;
    private LocalDateTime date;

    public Hashtag(int tweetId, String hashtagName, LocalDateTime date) {
        this.tweetId = tweetId;
        this.hashtagName = hashtagName;
        this.date = date;
    }

    public int getTweetId() {
        return tweetId;
    }

    public String getHashtagName() {
        return hashtagName;
    }

    public static ArrayList<String> findHashtags(String text){
        ArrayList<String> hashtags = new ArrayList<>();
        // Define the pattern to match hashtags
        Pattern pattern = Pattern.compile("#\\w+");
        // Create a matcher with the input text
        Matcher matcher = pattern.matcher(text);
        // Find and print all hashtags
        while (matcher.find()) {
            String hashtag = matcher.group();
            hashtags.add(hashtag);
        }
        return hashtags;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Hashtag hashtag = (Hashtag) o;
        return Objects.equals(hashtagName, hashtag.hashtagName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hashtagName);
    }
}
