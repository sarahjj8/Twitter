package view;

import com.jfoenix.controls.JFXButton;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import model.entity.*;

import java.util.ArrayList;
import java.util.Map;

public class QuoteComponent extends TweetComponent{
    private Retweet retweet;
    private Tweet aboutTweet;
    private TweetComponent aboutTweetComponent;
    private static Image retweetImage = new Image(String.valueOf(TweetComponent.class.getResource("icons/retweet.png")));
    private static ImageView retweetImageView = new ImageView(retweetImage);
    private static JFXButton retweetButton = new JFXButton();

    public QuoteComponent(Retweet retweet){
        super(retweet, false);
        this.retweet = retweet;

        setAboutTweetComponent();
        if(aboutTweet == null){
            return;
        }

        tweetContent.getChildren().addAll(aboutTweetComponent);

        aboutTweetComponent.getChildren().remove(aboutTweetComponent.getChildren().size() - 1);

        this.getChildren().clear();
        this.getChildren().addAll(userInfoBox.getInfo(), tweetContent, tweetButtonBox.getButtonBox());
        aboutTweetComponent.setScaleX(0.8);
        aboutTweetComponent.setScaleY(0.8);
        setLocation();
    }

    protected void setLocation(){
        aboutTweetComponent.setScaleX(0.8);
        aboutTweetComponent.setScaleY(0.8);

        AnchorPane.setTopAnchor(userInfoBox.getInfo(), 13.0);
        AnchorPane.setLeftAnchor(userInfoBox.getInfo(), 0.0);

        tweetContent.setAlignment(Pos.CENTER);
        AnchorPane.setTopAnchor(tweetContent, 83.0);
        AnchorPane.setLeftAnchor(tweetContent, 0.0);
        tweetContent.setStyle("-fx-background-color: rgba(255,255,255,0)");

        AnchorPane.setBottomAnchor(tweetButtonBox.getButtonBox(), 15.0);
        AnchorPane.setLeftAnchor(tweetButtonBox.getButtonBox(), 10.0);
    }

    private void setAboutTweetComponent(){
        ArrayList<Tweet> tweets;
        Map<String, String> params = Map.of("tweet id", String.valueOf(retweet.getAboutTweet()));
        HttpClientResponse response = HttpClientResponse.sendRequest(HttpClientResponse.createURL("tweet"), null, params, "GET");
        if(response.getResponseCode() == 200 && response.getResponse().equals("No tweets ...")){
            Message.showTemporaryMessage(aboutTweetComponent,response.getResponse());
            aboutTweet = null;
            return;
        } else if (response.getResponseCode() == 200) {
            tweets = JsonHelper.parseJsonToTweetListWithAdapter(response.getResponse());
            if(!tweets.isEmpty())
                aboutTweet = tweets.get(0);
            else{
                aboutTweet = null;
                return;
            }
        }
        aboutTweetComponent = newAboutTweetComponent(aboutTweet);
    }

    public static TweetComponent newAboutTweetComponent(Tweet tweet){
        if(tweet instanceof Poll){
            return new PollComponent((Poll) tweet);
        } else {
            return new TweetComponent(tweet, true);
        }
    }
}

