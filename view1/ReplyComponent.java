package view;

import com.jfoenix.controls.JFXButton;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import model.entity.*;

import java.util.ArrayList;
import java.util.Map;

public class ReplyComponent extends TweetComponent{
    private Reply reply;
    private Tweet aboutTweet;
    private HBox replyBox;
    private TweetComponent aboutTweetComponent;
    private Label replyingLabel;
    private Label usernameLabel;

    public ReplyComponent(Reply reply, Tweet aboutTweet){
        super(reply, false);
        this.reply = reply;
        this.aboutTweet = aboutTweet;

        if(aboutTweet == null){
            Message.showTemporaryMessage(replyBox, "Can't load the tweet");
            return;
        }

        replyBox = new HBox();
        replyingLabel = new Label("Replying to ");
        usernameLabel = new Label("@" + aboutTweet.getProfile().getUser().getName() + aboutTweet.getProfile().getUser().getLastName());
        replyBox.getChildren().addAll(replyingLabel, usernameLabel);

        this.getChildren().clear();
        this.getChildren().addAll(replyBox, userInfoBox.getInfo(), tweetContent, tweetButtonBox.getButtonBox());
        setConfig();
        setLocation();
    }

    protected void setLocation(){
        AnchorPane.setTopAnchor(replyBox, 5.0);
        AnchorPane.setLeftAnchor(replyBox, 25.0);

        AnchorPane.setTopAnchor(userInfoBox.getInfo(), 13.0);
        AnchorPane.setLeftAnchor(userInfoBox.getInfo(), 23.0);

        tweetContent.setAlignment(Pos.CENTER);
        AnchorPane.setTopAnchor(tweetContent, 83.0);
        AnchorPane.setLeftAnchor(tweetContent, 20.0);
        tweetContent.setStyle("-fx-background-color: rgba(255,255,255,0)");

        AnchorPane.setBottomAnchor(tweetButtonBox.getButtonBox(), 8.0);
        AnchorPane.setLeftAnchor(tweetButtonBox.getButtonBox(), 10.0);
    }

    private void setConfig(){
        replyBox.setAlignment(Pos.CENTER_LEFT);
        usernameLabel.setFont(Font.font("Franklin Gothic", FontWeight.SEMI_BOLD,13));
        usernameLabel.setStyle("-fx-text-fill:#0e9ff1;");

        replyingLabel.setFont(Font.font("Franklin Gothic", FontWeight.SEMI_BOLD,13));
        replyingLabel.setStyle("-fx-text-fill:#536572;");
    }

    private TweetComponent setAboutTweetComponent(){
        ArrayList<Tweet> tweets;
        Map<String, String> params = Map.of("tweet id", String.valueOf(reply.getAboutTweet()));
        HttpClientResponse response = HttpClientResponse.sendRequest(HttpClientResponse.createURL("tweet"), null, params, "GET");
        if(response.getResponseCode() == 200 && response.getResponse().equals("No tweets ...")){
            Message.showTemporaryMessage(aboutTweetComponent,response.getResponse());
            aboutTweet = null;
        } else if (response.getResponseCode() == 200) {
            tweets = JsonHelper.parseJsonToTweetListWithAdapter(response.getResponse());
            if(!tweets.isEmpty())
                aboutTweet = tweets.get(0);
            else{
                aboutTweet = null;
            }
        }
        aboutTweetComponent = newAboutTweetComponent(aboutTweet);
        return aboutTweetComponent;
    }

    public static Tweet getAboutTweet(Reply reply){
        ArrayList<Tweet> tweets;
        Map<String, String> params = Map.of("tweet id", String.valueOf(reply.getAboutTweet()));
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

    public static TweetComponent newAboutTweetComponent(Tweet tweet){
        if(tweet instanceof Poll){
            return new PollComponent((Poll) tweet);
        } else {
            return new TweetComponent(tweet, true);
        }
    }
}

