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
import view.PostTweetComponent;
import view.ToolbarController;

import java.util.ArrayList;
import java.util.Map;

public class RetweetPageController {
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private ScrollPane scrollPane;
    private VBox retweetContent;
    private ToolbarController toolbar;
    private ArrayList<Tweet> retweets;
    private Tweet tweet;
    private Profile profile;
    private static Label noTweetsLabel = new Label("No retweet or quote tweets yet ...");
    private PostTweetComponent postTweetComponent;

    public void initialize()
    {
        setProfile();
        retweetContent = new VBox();
        scrollPane.setContent(retweetContent);
        retweetContent.setSpacing(10);

        toolbar = new ToolbarController();
        postTweetComponent = new PostTweetComponent(profile, tweet, "retweet", retweetContent, noTweetsLabel);
        retweetContent.getChildren().add(postTweetComponent);

        setRetweets();
        if(retweets.isEmpty()){
            noTweetsLabel.setFont(Font.font("Franklin Gothic", FontWeight.THIN,14));
            retweetContent.getChildren().add(noTweetsLabel);
        }
        HomeController.addTweetsToVbox(retweets, retweetContent);
        scrollPane.setContent(retweetContent);
        anchorPane.getChildren().addAll(toolbar);
        setConfigure();
        setLocation();
    }

    private void setConfigure(){
        retweetContent.setStyle("-fx-background-color:#f3f3f3;" +
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

    public void setTweet(Tweet tweet) {
        this.tweet = tweet;
    }

    private void setLocation(){
        AnchorPane.setTopAnchor(toolbar, 30.0);
        AnchorPane.setLeftAnchor(toolbar, 0.0);
        AnchorPane.setBottomAnchor(toolbar, 0.0);
    }

    private void setProfile(){
        Map<String, String> params = Map.of("username", Client.getUsername());
        HttpClientResponse response = HttpClientResponse.sendRequest(HttpClientResponse.createURL("profile"), null, params, "GET");
        ArrayList<Profile> profiles = JsonHelper.parseJsonToListWithAdapter(response.getResponse(), model.entity.Profile.class);
        if (!profiles.isEmpty())
            profile = profiles.get(0);
    }

    private void setRetweets(){
        Map<String, String> params = Map.of("username", Client.getUsername(), "tweet id", String.valueOf(tweet.getId()));
        HttpClientResponse response = HttpClientResponse.sendRequest(HttpClientResponse.createURL("retweetsAndQuotes"), null, params, "GET");
        if(response.getResponseCode() == 200 && response.getResponse().equals("No retweets or quotes ...")){
            retweets = new ArrayList<>();
        } else if (response.getResponseCode() == 200) {
            retweets = JsonHelper.parseJsonToTweetListWithAdapter(response.getResponse());
        } else {
            retweets = new ArrayList<>();
        }
    }
}

