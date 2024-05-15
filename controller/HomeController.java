package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import model.Client;
import model.entity.*;
import view.*;

import java.util.ArrayList;
import java.util.Map;

public class HomeController {
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox tweetContent;
    private ToolbarController toolbar;
    private ArrayList<Tweet> tweets;
    private static Label noTweetsLabel = new Label("No tweets found for your timeline ...");

    public void initialize()
    {
        tweetContent = new VBox();
        scrollPane.setContent(tweetContent);
        tweetContent.setSpacing(10);

        toolbar = new ToolbarController();

        setTweets();
        if(tweets.isEmpty()){
            noTweetsLabel.setFont(Font.font("Franklin Gothic", FontWeight.THIN,14));
            tweetContent.getChildren().add(noTweetsLabel);
        }
        addTweetsToVbox(tweets, tweetContent);
        scrollPane.setContent(tweetContent);
        anchorPane.getChildren().addAll(toolbar);
        setConfigure();
        setLocation();
    }

    private void setConfigure(){
        tweetContent.setStyle("-fx-background-color:#f3f3f3;" +
                "-fx-background-radius:10;" +
                "-fx-border-radius:10;" +
                "-fx-border-color:#f3f3f3");

        anchorPane.setStyle("-fx-background-color:#ffffff;" +
                "-fx-background-radius:10;" +
                "-fx-border-radius:10;" +
                "-fx-border-color:rgba(243,243,243,0)");

        scrollPane.setStyle("-fx-focus-color: transparent; " +
                "-fx-faint-focus-color: transparent;" +
                "-fx-background-color:#f3f3f3;" +
                "-fx-border-color: rgba(255,255,255,0)");
    }

    private void setLocation(){
        AnchorPane.setTopAnchor(toolbar, 30.0);
        AnchorPane.setLeftAnchor(toolbar, 0.0);
        AnchorPane.setBottomAnchor(toolbar, 0.0);
    }

    private void setTweets(){
        Map<String, String> params = Map.of("username", Client.getUsername());
        HttpClientResponse response = HttpClientResponse.sendRequest(HttpClientResponse.createURL("timeline"), null, params, "GET");
        if(response.getResponseCode() == 200 && response.getResponse().equals("No tweets ...")){
            tweets = new ArrayList<>();
        } else if (response.getResponseCode() == 200) {
            tweets = JsonHelper.parseJsonToTweetListWithAdapter(response.getResponse());
        }
    }

    public static void addTweetsToVbox(ArrayList<Tweet> tweets, VBox vBox){
        for(Tweet tweet : tweets){
            if(tweet instanceof Reply){
                Reply reply = (Reply) tweet;
                vBox.getChildren().add(new ReplyComponent(reply, ReplyComponent.getAboutTweet(reply)));
            }  else if(tweet instanceof Retweet && (tweet.getText() != null && !tweet.getText().trim().isEmpty())){
                vBox.getChildren().add(new QuoteComponent((Retweet) tweet));
            } else if(tweet instanceof Retweet && tweet.getText() == null){
                Retweet retweet = (Retweet) tweet;
                vBox.getChildren().add(new RetweetComponent(retweet, RetweetComponent.getAboutTweet(retweet)));
            } else if(tweet instanceof Poll){
                vBox.getChildren().add(new PollComponent((Poll) tweet));
            } else {
                vBox.getChildren().add(new TweetComponent(tweet, true));
            }
        }
    }

    public static TweetComponent getTweetComponent(Tweet tweet){
        if(tweet instanceof Reply){
            Reply reply = (Reply) tweet;
            return new ReplyComponent(reply, ReplyComponent.getAboutTweet(reply));
        }  else if(tweet instanceof Retweet && (tweet.getText() != null && !tweet.getText().trim().isEmpty())){
            return new QuoteComponent((Retweet) tweet);
        } else if(tweet instanceof Retweet && tweet.getText() == null){
            Retweet retweet = (Retweet) tweet;
            return new RetweetComponent(retweet, RetweetComponent.getAboutTweet(retweet));
        } else if(tweet instanceof Poll){
            return new PollComponent((Poll) tweet);
        } else {
            return new TweetComponent(tweet, true);
        }
    }

    public static Tweet getTweet(int tweetId){
        ArrayList<Tweet> tweets;
        Map<String, String> params = Map.of("tweet id", String.valueOf(tweetId));
        HttpClientResponse response = HttpClientResponse.sendRequest(HttpClientResponse.createURL("tweet"), null, params, "GET");
        if(response.getResponseCode() == 200 && response.getResponse().equals("No tweets ...")){
            return null;
        } else if (response.getResponseCode() == 200) {
            tweets = JsonHelper.parseJsonToTweetListWithAdapter(response.getResponse());
            if(!tweets.isEmpty())
                return tweets.get(0);
            else{
                return null;
            }
        }
        return null;
    }
}