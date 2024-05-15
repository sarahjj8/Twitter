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

public class ReplyPageController {
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private ScrollPane scrollPane;
    private VBox replyContent;
    private ToolbarController toolbar;
    private ArrayList<Tweet> replies;
    private Tweet tweet;
    private Profile profile;
    private static Label noTweetsLabel = new Label("No replies yet ...");
    private PostTweetComponent postTweetComponent;

    public void initialize()
    {
        setProfile();
        replyContent = new VBox();
        scrollPane.setContent(replyContent);
        replyContent.setSpacing(10);

        toolbar = new ToolbarController();
        postTweetComponent = new PostTweetComponent(profile, tweet, "reply", replyContent, noTweetsLabel);
        replyContent.getChildren().add(postTweetComponent);

        setReplies();
        if(replies.isEmpty()){
            noTweetsLabel.setFont(Font.font("Franklin Gothic", FontWeight.THIN,14));
            replyContent.getChildren().add(noTweetsLabel);
        }
        HomeController.addTweetsToVbox(replies, replyContent);
        scrollPane.setContent(replyContent);
        anchorPane.getChildren().addAll(toolbar);
        setConfigure();
        setLocation();
    }

    private void setConfigure(){
        replyContent.setStyle("-fx-background-color:#f3f3f3;" +
                "-fx-background-radius:10;" +
                "-fx-border-radius:10;" +
                "-fx-border-color:#f3f3f3");

        anchorPane.setStyle("-fx-background-color:#ffffff;" +
                "-fx-background-radius:10;" +
                "-fx-border-radius:10;" +
                "-fx-border-color:rgba(243,243,243,0)");

        scrollPane.setStyle("-fx-focus-color: transparent; " +
                "-fx-faint-focus-color: transparent;" +
                "-fx-background-color:#ffffff;" +
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

    private void setReplies(){
        Map<String, String> params = Map.of("username", Client.getUsername(), "tweet id", String.valueOf(tweet.getId()));
        HttpClientResponse response = HttpClientResponse.sendRequest(HttpClientResponse.createURL("replies"), null, params, "GET");
        if(response.getResponseCode() == 200 && response.getResponse().equals("No replies ...")){
            replies = new ArrayList<>();
        } else if (response.getResponseCode() == 200) {
            replies = JsonHelper.parseJsonToTweetListWithAdapter(response.getResponse());
        } else {
            replies = new ArrayList<>();
        }
    }
}
