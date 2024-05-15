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

public class RetweetComponent extends TweetComponent{
    private Retweet retweet;
    private Tweet aboutTweet;
    private TweetComponent aboutTweetComponent;
    private HBox retweetBox;
    private static Image retweetImage = new Image(String.valueOf(TweetComponent.class.getResource("icons/retweet.png")));
    private static ImageView retweetImageView = new ImageView(retweetImage);
    private Label senderUsernameLabel;
    private static JFXButton retweetButton = new JFXButton();

    public RetweetComponent(Retweet retweet, Tweet aboutTweet){
        super(aboutTweet, false);
        this.retweet = retweet;
        this.aboutTweet = aboutTweet;

        if(aboutTweet == null){
            Message.showTemporaryMessage(retweetButton, "Can't load the tweet");
            return;
        }

        retweetBox = new HBox(5);
        senderUsernameLabel = new Label(retweet.getProfile().getUser().getName() + " " + retweet.getProfile().getUser().getLastName() + " Retweeted");
        retweetBox.getChildren().addAll(retweetButton, senderUsernameLabel);

        this.getChildren().clear();
        this.getChildren().addAll(retweetBox, userInfoBox.getInfo(), tweetContent, tweetButtonBox.getButtonBox());
        setConfig();
        setLocation();
    }

    protected void setLocation(){
        retweetBox.setAlignment(Pos.CENTER_LEFT);
        AnchorPane.setTopAnchor(retweetBox, 5.0);
        AnchorPane.setLeftAnchor(retweetBox, 25.0);

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
        if (imagePaths.size() + videoPaths.size() == 0){
            this.setPrefSize(320,300);
            this.setMinSize(320,300);
            this.setMaxSize(320,300);
        } else {
            this.setPrefSize(320,465);
            this.setMinSize(320,465);
            this.setMaxSize(320,465);
        }
        senderUsernameLabel.setFont(Font.font("Franklin Gothic", FontWeight.BOLD,10));
        senderUsernameLabel.setStyle("-fx-text-fill:#536572;");

        retweetImageView.setFitHeight(13);
        retweetImageView.setFitWidth(13);

        retweetButton.setGraphic(retweetImageView);
        retweetButton.setPrefSize(13,13);
        retweetButton.setRipplerFill(Paint.valueOf("#858080"));
        retweetButton.setStyle("-fx-background-radius: 50");

        retweetButton.setOpacity(1.0);
    }

    private TweetComponent setAboutTweetComponent(){
        ArrayList<Tweet> tweets;
        Map<String, String> params = Map.of("tweet id", String.valueOf(retweet.getAboutTweet()));
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

    public static Tweet getAboutTweet(Retweet retweet){
        ArrayList<Tweet> tweets;
        Map<String, String> params = Map.of("tweet id", String.valueOf(retweet.getAboutTweet()));
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
