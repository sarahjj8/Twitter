package model.entity;

import java.time.LocalDateTime;

public class Retweet extends Tweet{
    private int aboutTweet;

    public Retweet(int id, Profile profile, String text, String type, LocalDateTime date, int aboutTweet) {
        super(id, profile, text, type, date);
        this.aboutTweet = aboutTweet;
    }

    public Retweet(int id, Profile profile, String text, String type, LocalDateTime date, int numberOfLikes, int numberOfRetweets, int numberOfQuotes, int numberOfReplies, int aboutTweet) {
        super(id, profile, text, type, date, numberOfLikes, numberOfRetweets, numberOfQuotes, numberOfReplies);
        this.aboutTweet = aboutTweet;
    }

    public Retweet(){}

    public int getAboutTweet() {
        return aboutTweet;
    }

    public void setAboutTweet(int aboutTweet) {
        this.aboutTweet = aboutTweet;
    }
}
