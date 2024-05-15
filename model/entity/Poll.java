package model.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class Poll extends Tweet{
    //the field text in model.entity.Tweet is the question in polls
    private HashMap<String, Integer> choices = new HashMap<>();   //key: choices    value: results of the choice
    private int aboutTweet;
    public Poll(int id, Profile profile, String text, String type, LocalDateTime date, HashMap<String, Integer> choices) {
        super(id, profile, text, type, date);
        this.choices = choices;
    }

    public Poll(int id, Profile profile, String text, String type, LocalDateTime date, int numberOfLikes, int numberOfRetweets, int numberOfQuotes, int numberOfReplies, HashMap<String, Integer> choices) {
        super(id, profile, text, type, date, numberOfLikes, numberOfRetweets, numberOfQuotes, numberOfReplies);
        this.choices = choices;
    }

    public Poll() {
    }

    public HashMap<String, Integer> getResults() {
        return choices;
    }

    public int getAboutTweet() {
        return aboutTweet;
    }

    public void setChoices(HashMap<String, Integer> choices) {
        this.choices = choices;
    }

    public void setAboutTweet(int aboutTweet) {
        this.aboutTweet = aboutTweet;
    }

    public Set<String> getChoices() {
        return choices.keySet();
    }

    public int getNumberOfVotes(String choice){
        return choices.get(choice);
    }
}
