package view;

import com.jfoenix.controls.JFXButton;
import controller.ReplyPageController;
import controller.RetweetPageController;
import controller.ViewOthersProfileController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import model.Client;
import model.entity.HttpClientResponse;
import model.entity.Tweet;

import java.io.IOException;
import java.util.Map;

public class TweetButtonBox {
    private Tweet tweet;
    private static Image redHeartImage = new Image(String.valueOf(TweetComponent.class.getResource("icons/red_heart.png")));
    private static Image grayHeartImage = new Image(String.valueOf(TweetComponent.class.getResource("icons/grey_heart.png")));
    private static Image replyImage = new Image(String.valueOf(TweetComponent.class.getResource("icons/reply.png")));
    private static Image retweetImage = new Image(String.valueOf(TweetComponent.class.getResource("icons/retweet.png")));
    private static Image viewImage = new Image(String.valueOf(TweetComponent.class.getResource("icons/view.png")));
    private static Image shareImage = new Image(String.valueOf(TweetComponent.class.getResource("icons/share.png")));
    private ImageView heartImageView = new ImageView(grayHeartImage);
    private ImageView replyImageView = new ImageView(replyImage);
    private ImageView retweetImageView = new ImageView(retweetImage);
    private ImageView viewImageView = new ImageView(viewImage);
    private ImageView shareImageView = new ImageView(shareImage);
    private Label countOfRet;
    private Label countOfViews;
    private Label countOfRep;
    private Label countOfLikes;
    private JFXButton replyButton;
    private JFXButton retButton;
    private JFXButton likeButton;
    private JFXButton shareButton;
    private JFXButton viewButton;
    private HBox buttonBox;
    private boolean isLikedBefore;

    public TweetButtonBox(Tweet tweet)
    {
        this.tweet = tweet;

        countOfRet = new Label(String.valueOf(tweet.getNumberOfRetweets() + tweet.getNumberOfQuotes()));
        //this is not an actual value
        countOfViews = new Label("0");
        countOfRep = new Label(String.valueOf(tweet.getNumberOfReplies()));
        countOfLikes = new Label(String.valueOf(tweet.getNumberOfLikes()));
        replyButton = new JFXButton();
        retButton = new JFXButton();
        likeButton = new JFXButton();
        shareButton = new JFXButton();
        viewButton = new JFXButton();

        HBox likeBox = new HBox();
        HBox retweetBox = new HBox();
        HBox replyBox = new HBox();
        HBox viewBox = new HBox();
        likeBox.getChildren().addAll(likeButton, countOfLikes);
        retweetBox.getChildren().addAll(retButton, countOfRet);
        replyBox.getChildren().addAll(replyButton, countOfRep);
        viewBox.getChildren().addAll(viewButton, countOfViews);
        buttonBox = new HBox(25);

        buttonBox.getChildren().addAll(replyBox, retweetBox, likeBox, viewBox, shareButton);

        setConfig();
        setActions();
    }

    private void setIsLikedBefore(){
        Map<String, String> params = Map.of("username", Client.getUsername(),"tweet id", String.valueOf(tweet.getId()));
        HttpClientResponse response = HttpClientResponse.sendRequest(HttpClientResponse.createURL("isLikedBy"), null, params, "GET");
        if(response.getResponseCode() != 200){
            Message.showTemporaryMessage(buttonBox, response.getResponse());
            isLikedBefore = false;
        } else {
            if(response.getResponse().equals("true"))
                isLikedBefore = true;
            else
                isLikedBefore = false;
        }
    }

    public HBox getButtonBox() {
        return buttonBox;
    }

    private void setConfig() {
        countOfRet.setFont(Font.font("Franklin Gothic", FontWeight.THIN,14));
        countOfRet.setTextFill(Paint.valueOf("#536572"));

        countOfRep.setFont(Font.font("Franklin Gothic", FontWeight.THIN,14));
        countOfRep.setTextFill(Paint.valueOf("#536572"));

        countOfLikes.setFont(Font.font("Franklin Gothic", FontWeight.THIN,14));
        countOfLikes.setTextFill(Paint.valueOf("#536572"));

        countOfViews.setFont(Font.font("Franklin Gothic", FontWeight.THIN,14));
        countOfViews.setTextFill(Paint.valueOf("#536572"));

        replyImageView.setFitHeight(17);
        replyImageView.setFitWidth(17);
        retweetImageView.setFitHeight(17);
        retweetImageView.setFitWidth(17);
        heartImageView.setFitWidth(17);
        heartImageView.setFitHeight(17);
        shareImageView.setFitWidth(17);
        shareImageView.setFitHeight(17);
        viewImageView.setFitWidth(17);
        viewImageView.setFitHeight(17);

        replyButton.setGraphic(replyImageView);
        replyButton.setPrefSize(17,17);
        replyButton.setRipplerFill(Paint.valueOf("#858080"));
        replyButton.setStyle("-fx-background-radius: 50");

        retButton.setGraphic(retweetImageView);
        retButton.setPrefSize(17,17);
        retButton.setRipplerFill(Paint.valueOf("#858080"));
        retButton.setStyle("-fx-background-radius: 50");

        setIsLikedBefore();
        if(isLikedBefore)
            heartImageView.setImage(redHeartImage);

        likeButton.setGraphic(heartImageView);
        likeButton.setPrefSize(17,17);
        likeButton.setRipplerFill(Paint.valueOf("#858080"));
        likeButton.setStyle("-fx-background-radius: 50");

        shareButton.setGraphic(shareImageView);
        shareButton.setPrefSize(17,17);
        shareButton.setRipplerFill(Paint.valueOf("#858080"));
        shareButton.setStyle("-fx-background-radius: 50");

        viewButton.setGraphic(viewImageView);
        viewButton.setPrefSize(17,17);
        viewButton.setRipplerFill(Paint.valueOf("#858080"));
        viewButton.setStyle("-fx-background-radius: 50");
    }

    private void setActions()
    {
        replyButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
//                tweet.setNumberOfQuotes(tweet.getNumberOfQuotes() + 1);
//                countOfRep.setText(String.valueOf(tweet.getNumberOfQuotes()));
                try {
                    ReplyPageController replyPageController = new ReplyPageController();
                    replyPageController.setTweet(tweet);
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/reply.fxml"));
                    loader.setController(replyPageController);
                    Parent replyPage = loader.load();
                    Scene replyScene = new Scene(replyPage);
                    Stage primaryStage = (Stage) replyButton.getScene().getWindow();
                    primaryStage.setScene(replyScene);
                    primaryStage.show();
                } catch (IOException e) {
                    Message.showTemporaryMessage(replyButton, "an error occurred while loading replies");
                    e.printStackTrace();
                }
            }
        });

        retButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
//                tweet.setNumberOfRetweets(tweet.getNumberOfRetweets() + 1);
//                countOfRet.setText(String.valueOf(tweet.getNumberOfRetweets()));
                try {
                    RetweetPageController retweetPageController = new RetweetPageController();
                    retweetPageController.setTweet(tweet);
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/retweet.fxml"));
                    loader.setController(retweetPageController);
                    Parent retweetPage = loader.load();
                    Scene retweetScene = new Scene(retweetPage);
                    Stage primaryStage = (Stage) retButton.getScene().getWindow();
                    primaryStage.setScene(retweetScene);
                    primaryStage.show();
                } catch (IOException e) {
                    Message.showTemporaryMessage(retButton, "an error occurred while loading replies");
                    e.printStackTrace();
                }
            }
        });
        likeButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(!isLikedBefore) {
                    String url = "http://localhost:8081/like";
                    String requestBody = "{\"username\": \"" + Client.getUsername() + "\",\"tweetId\": \"" + tweet.getId() + "\"}";
                    model.entity.HttpClientResponse response = model.entity.HttpClientResponse.sendRequest(url, requestBody, null, "POST");
                    if (response != null) {
                        if (response.getResponseCode() == 201) {
                            tweet.setNumberOfLikes(tweet.getNumberOfLikes() + 1);
                            countOfLikes.setText(String.valueOf(tweet.getNumberOfLikes()));
                            heartImageView.setImage(redHeartImage);
                            isLikedBefore = true;
                        } else {
                            Message.showTemporaryMessage(buttonBox, response.getResponse());
                        }
                    } else {
                        Message.showTemporaryMessage(buttonBox, "Oops! Something went wrong.");
                    }
                } else {
                        String url = "http://localhost:8081/unlike";
                        Map<String, String> deleteParams = Map.of("username", Client.getUsername(), "tweet id", String.valueOf(tweet.getId()));
                        model.entity.HttpClientResponse response = model.entity.HttpClientResponse.sendRequest(url, null, deleteParams, "DELETE");
                        if (response != null) {
                            if (response.getResponseCode() == 200) {
                                tweet.setNumberOfLikes(tweet.getNumberOfLikes() - 1);
                                countOfLikes.setText(String.valueOf(tweet.getNumberOfLikes()));
                                heartImageView.setImage(grayHeartImage);
                                isLikedBefore = false;
                            } else {
                                Message.showTemporaryMessage(buttonBox, response.getResponse());
                            }
                        } else {
                            Message.showTemporaryMessage(buttonBox, "Oops! Something went wrong.");
                        }
                }
            }
        });
        viewButton.setOnMouseEntered(event -> {
            viewButton.setCursor(Cursor.HAND);
        });
        retButton.setOnMouseEntered(event -> {
            retButton.setCursor(Cursor.HAND);
        });
        replyButton.setOnMouseEntered(event -> {
            replyButton.setCursor(Cursor.HAND);
        });
        likeButton.setOnMouseEntered(event -> {
            likeButton.setCursor(Cursor.HAND);
        });
        shareButton.setOnMouseEntered(event -> {
            shareButton.setCursor(Cursor.HAND);
        });
        viewButton.setOnMouseEntered(event -> {
            viewButton.setCursor(Cursor.HAND);
        });
    }

    public void resize(){
        buttonBox.setSpacing(22.5);
        countOfViews.setFont(Font.font("Franklin Gothic", FontWeight.THIN,12.6));
        countOfLikes.setFont(Font.font("Franklin Gothic", FontWeight.THIN,12.6));
        countOfRet.setFont(Font.font("Franklin Gothic", FontWeight.THIN,12.6));
        countOfRep.setFont(Font.font("Franklin Gothic", FontWeight.THIN,12.6));

        replyImageView.setFitHeight(15.3);
        replyImageView.setFitWidth(15.3);
        retweetImageView.setFitHeight(15.3);
        retweetImageView.setFitWidth(15.3);
        heartImageView.setFitWidth(15.3);
        heartImageView.setFitHeight(15.3);
        shareImageView.setFitWidth(15.3);
        shareImageView.setFitHeight(15.3);
        viewImageView.setFitWidth(15.3);
        viewImageView.setFitHeight(15.3);

        replyButton.setPrefSize(15.3,15.3);
        likeButton.setPrefSize(15.3,15.3);
        retButton.setPrefSize(15.3,15.3);
        shareButton.setPrefSize(15.3,15.3);
        viewButton.setPrefSize(15.3,15.3);

    }
}
