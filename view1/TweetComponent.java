package view;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import model.entity.*;

import java.io.File;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;

public class TweetComponent extends AnchorPane {
    protected static Image dotImage = new Image(String.valueOf(TweetComponent.class.getResource("icons/three_dot.png")));
    protected Image profileImage = new Image(String.valueOf(TweetComponent.class.getResource("icons/profile_image.png")));
    protected ImageView dotImageView = new ImageView(dotImage);
    protected Tweet tweet;
    protected Profile profile;
    protected JFXTextArea textArea;
    protected ImageView tweetImageView;
    protected Rectangle tweetRectangle;
    protected Label dateLabel;
    protected JFXButton dotsButton;
    protected VBox tweetContent;
    protected HBox mediaBox;
    protected UserInfoBox userInfoBox;
    protected TweetButtonBox tweetButtonBox;
    protected ArrayList<String> imagePaths;
    protected ArrayList<String> videoPaths;

    public TweetComponent(Tweet tweet, boolean setLocation)
    {
        this.tweet = tweet;
        textArea = new JFXTextArea(tweet.getText());
//        setHashtags();
        profile = tweet.getProfile();
        userInfoBox = new UserInfoBox(profile);

        getImages();
        getVideos();
        arrangeMedia();

        LocalDateTime dateTime = tweet.getDate();
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mma.MM/dd/yyyy.");
//        String formattedDateTime = dateTime.format(formatter);
//        dateLabel = new Label(formattedDateTime);
        dateLabel = new Label(Tweet.displayTimeInTimeline(Timestamp.valueOf(dateTime)));

        tweetContent = new VBox(10);
        tweetContent.getChildren().addAll(textArea,dateLabel);

        dotsButton = new JFXButton();
        tweetButtonBox = new TweetButtonBox(tweet);

        if(setLocation)
            this.getChildren().addAll(userInfoBox.getInfo(), tweetContent, tweetButtonBox.getButtonBox());

        setConfig();
        if(setLocation)
            setLocation();
        setActions();
    }

    private void setConfig()
    {
        if (imagePaths.size() + videoPaths.size() == 0){
            this.setPrefSize(320,300);
            this.setMinSize(320,300);
            this.setMaxSize(320,300);
        } else {
            this.setPrefSize(320,465);
            this.setMinSize(320,465);
            this.setMaxSize(320,465);
            tweetContent.getChildren().add(mediaBox);
        }
        this.setStyle("-fx-background-color:#ffffff;" +
                "-fx-background-radius:10;" +
                "-fx-border-radius:10;" +
                "-fx-border-color:#c0c0c0");
        tweetContent.setStyle("-fx-background-color:#ffffff;" +
                "-fx-background-radius:10;" +
                "-fx-border-radius:10;" +
                "-fx-border-color:#ffffff");
        textArea.setStyle("-fx-background-color:#ffffff;" +
                "-fx-text-fill:#0f1419;");
        textArea.setMaxSize(250.0,110.0);
        textArea.setMinSize(250.0,110.0);
        textArea.setFont(Font.font("Franklin Gothic",FontWeight.THIN,14));
        textArea.setEditable(false);

        dateLabel.setFont(Font.font("Franklin Gothic", FontWeight.THIN,12));
        dateLabel.setStyle("-fx-text-fill:#536572;");
    }
    protected void setLocation()
    {
        AnchorPane.setTopAnchor(userInfoBox.getInfo(), 0.0);
        AnchorPane.setLeftAnchor(userInfoBox.getInfo(), 0.0);

        tweetContent.setAlignment(Pos.CENTER);
        AnchorPane.setTopAnchor(tweetContent, 70.0);
        AnchorPane.setLeftAnchor(tweetContent, 23.0);

        AnchorPane.setTopAnchor(dateLabel,370.0);
        AnchorPane.setLeftAnchor(dateLabel,30.0);

        AnchorPane.setBottomAnchor(tweetButtonBox.getButtonBox(), 15.0);
        AnchorPane.setLeftAnchor(tweetButtonBox.getButtonBox(), 10.0);
    }

    private void setActions()
    {
        tweetContent.heightProperty().addListener((obs, oldVal, newVal) -> {
            double newHeight = newVal.doubleValue() + 120;
            this.setPrefHeight(newHeight);
            this.setMinHeight(newHeight);
            this.setMaxHeight(newHeight);
        });
    }

    private void getImages(){
        Map<String, String> params = Map.of("tweet id", String.valueOf(tweet.getId()));
        HttpClientResponse response = HttpClientResponse.sendRequest(HttpClientResponse.createURL("images"), null, params, "GET");
        if(response.getResponseCode() == 200 && response.getResponse().equals("No images ...")){
            imagePaths = new ArrayList<>();
        } else if (response.getResponseCode() == 200) {
            imagePaths = JsonHelper.getJsonArrayFromString(response.getResponse());
        } else{
            imagePaths = new ArrayList<>();
        }
    }

    private void getVideos(){
        Map<String, String> params = Map.of("tweet id", String.valueOf(tweet.getId()));
        HttpClientResponse response = HttpClientResponse.sendRequest(HttpClientResponse.createURL("videos"), null, params, "GET");
        if(response.getResponseCode() == 200 && response.getResponse().equals("No videos ...")){
            videoPaths = new ArrayList<>();
        } else if (response.getResponseCode() == 200) {
            videoPaths = JsonHelper.getJsonArrayFromString(response.getResponse());
        } else {
            videoPaths = new ArrayList<>();
        }
    }

    private void arrangeMedia(){
        mediaBox = new HBox();
        mediaBox.setPrefSize(280, 165);
        mediaBox.setSpacing(10);
        mediaBox.setAlignment(Pos.CENTER);
        mediaBox.setStyle("-fx-background-radius: 10;" +
                " -fx-border-radius: 10; " +
                "-fx-border-color: #e1e8ed;" +
                " -fx-border-width: 2;");

        if (imagePaths.size() + videoPaths.size() == 0){
            return;
        } else if (imagePaths.size() + videoPaths.size() == 1) {
            ImageView imageView = loadImage(imagePaths.get(0), 165, 280);
            mediaBox.getChildren().add(imageView);
        } else if (imagePaths.size() + videoPaths.size() == 2) {
            for (int i = 0; i < imagePaths.size(); i++) {
                ImageView imageView = loadImage(imagePaths.get(i), 165, 135);
                mediaBox.getChildren().add(imageView);
            }
            for (int i = 0; i < videoPaths.size(); i++) {
                // similar to images
            }
        } else if (imagePaths.size() + videoPaths.size() == 3) {
            // add two images in a VBox on the left
            VBox leftBox = new VBox();
            leftBox.setPrefSize(135, 165);
            leftBox.setSpacing(10);
            leftBox.setAlignment(Pos.CENTER);
            for (int i = 0; i < 2; i++) {
                ImageView imageView = loadImage(imagePaths.get(i), 75, 135);
                leftBox.getChildren().add(imageView);
            }
            mediaBox.getChildren().add(leftBox);
            // add third image/video on the right
            ImageView imageView = loadImage(imagePaths.get(2), 165, 135);
            mediaBox.getChildren().add(imageView);
        } else if (imagePaths.size() + videoPaths.size() == 4) {
            for (int i = 0; i < 2; i++) {
                VBox vbox = new VBox();
                vbox.setPrefSize(135, 82.5);
                vbox.setSpacing(10);
                vbox.setAlignment(Pos.CENTER);
                for (int j = i * 2; j < i * 2 + 2; j++) {
                    ImageView imageView = loadImage(imagePaths.get(j), 75, 135);
                    vbox.getChildren().add(imageView);
                }
                mediaBox.getChildren().add(vbox);
            }
        }
    }

    public static String getModifiedPath(String filePath) {
        String modifiedPath = filePath.replace("\\\\", "/");
        int startIndex = modifiedPath.indexOf("media/tweet images/");
        modifiedPath = modifiedPath.substring(startIndex);
        modifiedPath = modifiedPath.replace("\"", "");
        return modifiedPath;
    }

//    private ImageView loadImage(String path, double fitHeight, double fitWidth) {
//        ImageView imageView;
//        Image image;
//        try {
//            path = getModifiedPath(path);
//            image = new Image(String.valueOf(TweetComponent.class.getResource(path)), true);
//            imageView = new ImageView(image);
//            imageView.setPreserveRatio(true);
//            imageView.setFitHeight(fitHeight);
////            imageView.setFitWidth(fitWidth);
//            imageView.setSmooth(true);
//            imageView.setCache(true);
//        } catch (IllegalArgumentException | StringIndexOutOfBoundsException e) {
//            imageView = new ImageView(new Image(String.valueOf(TweetComponent.class.getResource("icons/broken-image.png")), true));
//        }
//        return imageView;
//    }

//
//    private ImageView loadImage(String path, double fitHeight, double fitWidth){
//        ImageView imageView;
//        Image image;
//        try {
//            path = getModifiedPath(path);
//            image = new Image(String.valueOf(TweetComponent.class.getResource(path)), true);
//            imageView = new ImageView(image);
//        } catch (IllegalArgumentException | StringIndexOutOfBoundsException e){
//            imageView = new ImageView(new Image(String.valueOf(TweetComponent.class.getResource("icons/broken-image.png")), true));
//        }
//        imageView.setFitHeight(fitHeight);
//        imageView.setFitWidth(fitWidth);
//        return imageView;
//    }


    private ImageView loadImage(String path, double fitHeight, double fitWidth){
        ImageView imageView;
        Image image;
        try {
            path = getModifiedPath(path);
            image = new Image(String.valueOf(TweetComponent.class.getResource(path)), true);

            double imageAspectRatio = image.getWidth() / image.getHeight();

            double newWidth = fitWidth;
            double newHeight = fitHeight;

            if (fitWidth / imageAspectRatio > fitHeight) {
                newWidth = fitHeight * imageAspectRatio;
            }
            if(fitHeight * imageAspectRatio > fitWidth){
                newHeight = fitWidth / imageAspectRatio;
            }

            imageView = new ImageView(image);
            imageView.setFitWidth(newWidth);
            imageView.setFitHeight(newHeight);
            imageView.setPreserveRatio(true);

            // If the new width or height is less than the desired width or height,
            // add transparent padding to fill the remaining space
            if (newWidth < fitWidth || newHeight < fitHeight) {
                double padX = (fitWidth - newWidth) / 2;
                double padY = (fitHeight - newHeight) / 2;
                imageView.setTranslateX(padX);
                imageView.setTranslateY(padY);

                Pane pane = new Pane(imageView);
                pane.setPrefSize(fitWidth, fitHeight);
                pane.setStyle("-fx-background-color: transparent");

                imageView = new ImageView(pane.snapshot(null, null));
                imageView.setFitWidth(fitWidth);
                imageView.setFitHeight(fitHeight);
            }
        } catch (IllegalArgumentException | StringIndexOutOfBoundsException e){
            e.printStackTrace();
            imageView = new ImageView(new Image(String.valueOf(TweetComponent.class.getResource("icons/broken-image.png")), true));
            imageView.setFitWidth(fitWidth);
            imageView.setFitHeight(fitHeight);
        }

        return imageView;
    }

}

