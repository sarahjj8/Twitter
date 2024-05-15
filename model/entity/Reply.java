package model.entity;

import java.time.LocalDateTime;

public class Reply extends Tweet{
    private int aboutTweet;

    public Reply(int id, Profile profile, String text, String type, LocalDateTime date, int aboutTweet) {
        super(id, profile, text, type, date);
        this.aboutTweet = aboutTweet;
    }

    public Reply(int id, Profile profile, String text, String type, LocalDateTime date, int numberOfLikes, int numberOfRetweets, int numberOfQuotes, int numberOfReplies, int aboutTweet) {
        super(id, profile, text, type, date, numberOfLikes, numberOfRetweets, numberOfQuotes, numberOfReplies);
        this.aboutTweet = aboutTweet;
    }

    public Reply() {
    }

    public int getAboutTweet() {
        return aboutTweet;
    }

    public void setAboutTweet(int aboutTweet) {
        this.aboutTweet = aboutTweet;
    }
}
