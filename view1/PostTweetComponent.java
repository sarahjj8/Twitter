package view;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import controller.HomeController;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.entity.JsonHelper;
import model.entity.Poll;
import model.entity.Profile;
import model.entity.Tweet;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;

public class PostTweetComponent extends AnchorPane {
    private Profile profile;
    private Tweet aboutTweet;
    private static Image mediaImage = new Image(String.valueOf(TweetComponent.class.getResource("icons/media.png")));
    private static Image GIFImage = new Image(String.valueOf(TweetComponent.class.getResource("icons/GIF.png")));
    private static Image pollImage = new Image(String.valueOf(TweetComponent.class.getResource("icons/poll.png")));
    private static Image emojiImage = new Image(String.valueOf(TweetComponent.class.getResource("icons/happy_emoji.png")));
    private static Image scheduleImage = new Image(String.valueOf(TweetComponent.class.getResource("icons/schedule.png")));
    private static Image locationImage = new Image(String.valueOf(TweetComponent.class.getResource("icons/blue_location.png")));
    private static Image tweetImage = new Image(String.valueOf(TweetComponent.class.getResource("icons/tweet.png")));
    private Image avatarImage = new Image(String.valueOf(TweetComponent.class.getResource("icons/profile_image.png")));
    private static ImageView mediaImageView = new ImageView(mediaImage);
    private static ImageView GIFImageView = new ImageView(GIFImage);
    private static ImageView pollImageView = new ImageView(pollImage);
    private static ImageView emojiImageView = new ImageView(emojiImage);
    private static ImageView scheduleImageView = new ImageView(scheduleImage);
    private static ImageView locationImageView = new ImageView(locationImage);
    private static ImageView tweetImageView = new ImageView(tweetImage);
    private ImageView avatarImageView = new ImageView(avatarImage);
    private Circle circleClipProfile;
    private HBox buttonBox;
    private VBox tweetContent;
    private static JFXTextArea tweetTextArea;
    private static JFXButton mediaButton;
    private static JFXButton GIFButton;
    private static JFXButton pollButton;
    private static JFXButton emojiButton;
    private static JFXButton scheduleButton;
    private static JFXButton locationButton;
    private JFXButton tweetButton;
    private String type;
    private ObservableList<File> uploadedFiles = FXCollections.observableArrayList();
    private ScrollPane scrollPane;
    private HBox mediaBox;
    private VBox content;
    private Label notFoundLabel;

    private ScrollPane pollScrollPane;
    private VBox pollChoices;
    private HBox addChoiceBox;
    private TextField choice1;
    private TextField choice2;
    private static Image addChoiceImage = new Image(String.valueOf(TweetComponent.class.getResource("icons/add_option.png")));
    private ImageView addChoiceImageView = new ImageView(addChoiceImage);
    private JFXButton addChoiceButton;
    private JFXButton removePollButton;
    private boolean isPoll = false;
    public PostTweetComponent(Profile profile, Tweet aboutTweet, String type, VBox content, Label notFoundLabel){
        this.profile = profile;
        this.aboutTweet = aboutTweet;
        this.type = type;
        this.notFoundLabel = notFoundLabel;
        this.content = content;
        tweetTextArea = new JFXTextArea();
        tweetTextArea.setTextFormatter(new TextFormatter<String>(change -> {
            String newText = change.getControlNewText();
            if (newText.length() > 280) {
                return null;
            } else {
                return change;
            }
        }));

//        setTextAreaPrompt();

        mediaButton = new JFXButton();
        GIFButton = new JFXButton();
        pollButton = new JFXButton();
        scheduleButton = new JFXButton();
        locationButton = new JFXButton();
        emojiButton = new JFXButton();
        tweetButton = new JFXButton();
        buttonBox = new HBox(0.0);

        tweetContent = new VBox(5);
        tweetContent.getChildren().add(tweetTextArea);

        mediaBox = new HBox(8);
        scrollPane = new ScrollPane(mediaBox);
        tweetContent.getChildren().add(scrollPane);

        buttonBox.setAlignment(Pos.CENTER_LEFT);
        buttonBox.setPadding(new Insets(5));
        buttonBox.getChildren().addAll(mediaButton, GIFButton, pollButton, emojiButton, scheduleButton, locationButton);
        circleClipProfile = new Circle(20);
        circleClipProfile.setStroke(Color.GRAY);
        circleClipProfile.setFill(Color.SNOW);
        circleClipProfile.setFill(new ImagePattern(avatarImage));

        updateAvatarImage(profile.getAvatarPath());

        this.getChildren().addAll(tweetContent, buttonBox, tweetButton ,circleClipProfile);

        setConfig();
        setLocation();
        setActions();
    }

    private void setConfig()
    {
        this.setPrefSize(320,420);
        this.setMinSize(320,420);
        this.setMaxSize(320,420);
        this.setStyle("-fx-background-color:#ffffff;" +
                "-fx-background-radius:10;" +
                "-fx-border-radius:10;" +
                "-fx-border-color:#c0c0c0");
        tweetContent.setStyle("-fx-background-color:#ffffff;" +
                "-fx-background-radius:10;" +
                "-fx-border-radius:10;" +
                "-fx-border-color:#ffffff");
        tweetContent.setAlignment(Pos.TOP_CENTER);

        tweetTextArea.setStyle("-fx-background-color:#ffffff;" +
                "-fx-text-fill:#0f1419;");
        tweetTextArea.setMaxSize(250.0,150.0);
        tweetTextArea.setMinSize(250.0,150.0);
        tweetTextArea.setFont(Font.font("Franklin Gothic",FontWeight.THIN,14));
        tweetTextArea.setWrapText(true);

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setPrefSize(250,147);
        scrollPane.setMinSize(250,147);
        scrollPane.setMaxSize(250,147);
        scrollPane.setStyle("-fx-background-color: #ffffff;" +
                "-fx-border-color: #ffffff");
        scrollPane.setVisible(false);

        mediaImageView.setFitHeight(17);
        mediaImageView.setFitWidth(17);
        GIFImageView.setFitHeight(17);
        GIFImageView.setFitWidth(17);
        pollImageView.setFitWidth(17);
        pollImageView.setFitHeight(17);
        locationImageView.setFitWidth(17);
        locationImageView.setFitHeight(17);
        scheduleImageView.setFitWidth(17);
        scheduleImageView.setFitHeight(17);
        emojiImageView.setFitWidth(17);
        emojiImageView.setFitHeight(17);

        mediaButton.setGraphic(mediaImageView);
        mediaButton.setPrefSize(17,17);
        mediaButton.setRipplerFill(Paint.valueOf("#858080"));
        mediaButton.setStyle("-fx-background-radius: 50");

        GIFButton.setGraphic(GIFImageView);
        GIFButton.setPrefSize(17,17);
        GIFButton.setRipplerFill(Paint.valueOf("#858080"));
        GIFButton.setStyle("-fx-background-radius: 50");

        pollButton.setGraphic(pollImageView);
        pollButton.setPrefSize(17,17);
        pollButton.setRipplerFill(Paint.valueOf("#858080"));
        pollButton.setStyle("-fx-background-radius: 50");

        emojiButton.setGraphic(emojiImageView);
        emojiButton.setPrefSize(17,17);
        emojiButton.setRipplerFill(Paint.valueOf("#858080"));
        emojiButton.setStyle("-fx-background-radius: 50");

        scheduleButton.setGraphic(scheduleImageView);
        scheduleButton.setPrefSize(17,17);
        scheduleButton.setRipplerFill(Paint.valueOf("#858080"));
        scheduleButton.setStyle("-fx-background-radius: 50");

        locationButton.setGraphic(locationImageView);
        locationButton.setPrefSize(17,17);
        locationButton.setRipplerFill(Paint.valueOf("#FFFFFF00"));
        locationButton.setStyle("-fx-background-radius: 50");

        tweetButton.setGraphic(tweetImageView);
        tweetButton.setPrefSize(60,60);
        tweetButton.setRipplerFill(Paint.valueOf("#FFFFFF00"));
        tweetButton.setStyle("-fx-background-radius: 50");
        tweetImageView.setPreserveRatio(true);
        tweetImageView.setFitWidth(60);
        if(type.equals("reply")){
            tweetButton.setDisable(true);
        }
        if(!type.equals("default")){
            pollButton.setDisable(true);
        }
    }
    private void setLocation()
    {
        AnchorPane.setTopAnchor(circleClipProfile,10.0);
        AnchorPane.setLeftAnchor(circleClipProfile,10.0);

        AnchorPane.setTopAnchor(tweetContent,60.0);
        AnchorPane.setLeftAnchor(tweetContent,30.0);

        AnchorPane.setLeftAnchor(buttonBox,12.0);
        AnchorPane.setBottomAnchor(buttonBox,12.0);

        AnchorPane.setRightAnchor(tweetButton,15.0);
        AnchorPane.setBottomAnchor(tweetButton,0.0);
    }

    private void setActions()
    {
        mediaButton.setOnMouseEntered(event -> {
            mediaButton.setCursor(Cursor.HAND);
        });
        GIFButton.setOnMouseEntered(event -> {
            GIFButton.setCursor(Cursor.HAND);
        });
        pollButton.setOnMouseEntered(event -> {
            pollButton.setCursor(Cursor.HAND);
        });
        scheduleButton.setOnMouseEntered(event -> {
            scheduleButton.setCursor(Cursor.HAND);
        });
        emojiButton.setOnMouseEntered(event -> {
            emojiButton.setCursor(Cursor.HAND);
        });
        tweetButton.setOnMouseEntered(event -> {
            tweetButton.setCursor(Cursor.HAND);
        });
        pollButton.setOnAction(event -> {
            isPoll = true;
            mediaButton.setDisable(true);
            makePollScrollPane();
        });
        mediaButton.setOnAction(event -> {
            isPoll = false;
            pollButton.setDisable(true);
            scrollPane.setVisible(true);
            FileChooser fileChooser = new FileChooser();
//            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Video Files", "*.mp4", "*.avi", "*.mov", "*.wmv", "*.flv"));
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.jpeg", "*.png"));
//            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Media Files", "*.mp4", "*.avi", "*.mov", "*.wmv", "*.flv", "*.jpg", "*.jpeg", "*.png"));
            File selectedFile = fileChooser.showOpenDialog(new Stage());

            getSelectedFile(selectedFile);
        });
//        tweetContent.heightProperty().addListener((obs, oldVal, newVal) -> {
//            double newHeight = newVal.doubleValue() + 255.0;
//            this.setPrefHeight(newHeight);
//            this.setMinHeight(newHeight);
//            this.setMaxHeight(newHeight);
//        });

        tweetButton.setOnAction(event -> {
            int tweetId = saveTweet(tweetTextArea.getText());
            if(tweetId == -1)
                return;
            try {
                for(File file : uploadedFiles){
                    if(Save.isImageFile(file)){
                        Image image = new Image(file.toURI().toString());
                        String imagePath = Save.saveImageToFile(image, Save.generateUniqueName(), "tweet images");
                        saveMedia(tweetId, imagePath, "image");
                    }
                }
                if(type.equals("retweet") && tweetTextArea.getText() != null && !tweetTextArea.getText().trim().isEmpty())
                    type = "quote";
                String message = type + " posted successfully";
                message = message.substring(0, 1).toUpperCase() + message.substring(1);
                cleanUp(message);
                updateContent(tweetId);
            } catch (IOException e) {
                Message.showTemporaryMessage(tweetButton, "an error occurred while saving the tweet image");
            }
        });

        if(type.equals("reply") || type.equals("default")){
            tweetTextArea.textProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue.trim().isEmpty()) {
                    tweetButton.setDisable(uploadedFiles.isEmpty());
                } else {
                    tweetButton.setDisable(false);
                }
            });

            uploadedFiles.addListener((ListChangeListener<? super File>) c -> {
                tweetButton.setDisable(tweetTextArea.getText().trim().isEmpty() && uploadedFiles.isEmpty());
            });
        }
    }

    private void getSelectedFile(File selectedFile){
        if (selectedFile != null && uploadedFiles.size() < 4) {
            try {
                uploadedFiles.add(selectedFile);
                if(uploadedFiles.size() == 4)
                    mediaButton.setDisable(true);
//                if(Save.isImageFile(selectedFile))
                    getImage(selectedFile);
//                else if(Save.isVideoFile(selectedFile))
//                    getVideo(selectedFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else{
            if (uploadedFiles.size() >= 4) {
                Message.showTemporaryMessage(tweetButton, "You can add up to 4 photos");
            }
        }
    }

    private void getImage(File selectedFile){
        Image image = new Image(selectedFile.toURI().toString());

        // Check if the image exceeds the maximum allowed size
        if (image.getWidth() > 250 || image.getHeight() > 147) {
            double scaleFactor = Math.min(250 / image.getWidth(), 147 / image.getHeight());
            image = new Image(selectedFile.toURI().toString(), image.getWidth() * scaleFactor, image.getHeight() * scaleFactor, true, true);
        }

        ImageView uploadedImageView = new ImageView(image);
        uploadedImageView.setPreserveRatio(true);

        mediaBox.getChildren().add(uploadedImageView);
    }

    private void getVideo(File selectedFile) {
        Media media = new Media(selectedFile.toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        MediaView mediaView = new MediaView(mediaPlayer);
        if(Save.checkVideoDuration(mediaPlayer, 45)){
            Message.showTemporaryMessage(tweetButton, "Your video can be a maximum of 45 seconds.");
            return;
        }

//        mediaView.setFitWidth(250);
//        mediaView.setFitHeight(147);
        Save.resizeVideoFrame(mediaView, 250, 147);
        mediaView.setPreserveRatio(true);

        VBox.setVgrow(tweetTextArea, Priority.ALWAYS);
        mediaBox.getChildren().add(mediaView);

        mediaPlayer.setCycleCount(1);
        mediaPlayer.play();
    }

    private int saveTweet(String text){
        switch (type){
            case "default":
                if(!isPoll)
                    return saveDefaultTweet(text);
                else
                    return savePoll(text);
            case "reply":
                return saveReply(text);
            case "quote":
            case "retweet":
                return saveRetweet(text);
        }
        return -1;
    }

    private int saveDefaultTweet(String text){
        String url = "http://localhost:8081/tweet";
        String requestBody = "{\"username\": \"" + profile.getUsername() + "\",\"text\": \"" + text + "\"}";
        model.entity.HttpClientResponse response = model.entity.HttpClientResponse.sendRequest(url, requestBody, null, "POST");
        if(response != null && response.getResponseCode() != 201){
            Message.showTemporaryMessage(tweetButton, response.getResponse());
            return -1;
        } else if (response == null) {
            Message.showTemporaryMessage(tweetButton, "Oops! Something went wrong.");
            return -1;
        } else {
            return Save.extractId(response.getResponse());
        }
    }

    private int savePoll(String text){
        String url = "http://localhost:8081/poll";
        String message = "Poll" + " posted successfully";
        String question = text;
        HashMap<String, Integer> choices = new HashMap<>();
        for(Node node : pollChoices.getChildren()){
            if(node instanceof TextField){
                TextField choice = (TextField) node;
                String choiceText = choice.getText();
                choices.put(choiceText, 0);
            }
        }
        Poll newPoll = new Poll(0, profile, question, "poll", LocalDateTime.now(), choices);
        String requestBody = JsonHelper.toJsonWithAdapter(newPoll);
        model.entity.HttpClientResponse response = model.entity.HttpClientResponse.sendRequest(url, requestBody, null, "POST");
        if(response != null && response.getResponseCode() != 201){
            Message.showTemporaryMessage(tweetButton, response.getResponse());
            return -1;
        } else if (response == null) {
            Message.showTemporaryMessage(tweetButton, "Oops! Something went wrong.");
            return -1;
        } else {
            return Save.extractId(response.getResponse());
        }
    }

    private int saveReply(String text){
        if(aboutTweet == null){
            Message.showTemporaryMessage(tweetButton, "Oops! Something went wrong.");
            return -1;
        }
        String url = "http://localhost:8081/reply";
        String requestBody = "{\"username\": \"" + profile.getUsername() + "\",\"text\": \"" + text + "\",\"aboutTweet\": \"" + aboutTweet.getId() + "\"}";
        model.entity.HttpClientResponse response = model.entity.HttpClientResponse.sendRequest(url, requestBody, null, "POST");
        if(response != null && response.getResponseCode() != 201){
            Message.showTemporaryMessage(tweetButton, response.getResponse());
            return -1;
        } else if (response == null) {
            Message.showTemporaryMessage(tweetButton, "Oops! Something went wrong.");
            return -1;
        } else {
            return Save.extractId(response.getResponse());
        }
    }

    /**
     * This method can be used for posting both Quotes and Retweets
     * (if text is null or text.trim().isEmpty that means that this is a retweet. otherwise it is a quote)
     */
    private int saveRetweet(String text){
        if(aboutTweet == null){
            Message.showTemporaryMessage(tweetButton, "Oops! Something went wrong.");
            return -1;
        }
        String requestBody;
        if(text == null || text.trim().isEmpty())
            requestBody = "{\"username\": \"" + profile.getUsername() + "\",\"aboutTweet\": \"" + aboutTweet.getId() + "\"}";
        else
            requestBody = "{\"username\": \"" + profile.getUsername() + "\",\"text\": \"" + text + "\",\"aboutTweet\": \"" + aboutTweet.getId() + "\"}";
        String url = "http://localhost:8081/retweet";
        model.entity.HttpClientResponse response = model.entity.HttpClientResponse.sendRequest(url, requestBody, null, "POST");
        if(response != null && response.getResponseCode() != 201){
            Message.showTemporaryMessage(tweetButton, response.getResponse());
            return -1;
        } else if (response == null) {
            Message.showTemporaryMessage(tweetButton, "Oops! Something went wrong.");
            return -1;
        } else {
            return Save.extractId(response.getResponse());
        }
    }

    private boolean saveMedia(int tweetId, String path, String type){
        String url = "http://localhost:8081/media";
        path = Save.escapeBackslashes(path);
        // because "\" is a special character for json. so it must be replaced it with "\\"
        String requestBody = "{\"tweetId\": " + tweetId + ",\"type\": \"" + type + "\",\"url\": \"" + path + "\"}";
        model.entity.HttpClientResponse response = model.entity.HttpClientResponse.sendRequest(url, requestBody, null, "POST");
        if(response != null && response.getResponseCode() != 201){
            Message.showTemporaryMessage(tweetButton, response.getResponse());
            return false;
        } else if (response == null) {
            Message.showTemporaryMessage(tweetButton, "Oops! Something went wrong.");
            return false;
        }
        return true;
    }

    public void updateAvatarImage(String imagePath) {
        if (imagePath != null) {
            File file = new File(imagePath);
            if (file.exists()) {
                Image image = new Image(file.toURI().toString());
                if (image != null) {
                    avatarImageView.setImage(image);
                    circleClipProfile.setFill(new ImagePattern(image));
                    avatarImage = image;
                }
            }
        }
    }

    public void setTextAreaPrompt(){
        tweetTextArea.setPromptText("");
        switch (type){
            case "default":
                tweetTextArea.setPromptText("What's happening?");
                break;
            case "reply":
                tweetTextArea.setPromptText("Tweet your reply");
                break;
            case "retweet":
            case "quote":
                tweetTextArea.setPromptText("Add a comment...");
                break;

        }
    }

    public void cleanUp(String message){
        uploadedFiles.clear();
        tweetContent.getChildren().remove(pollScrollPane);
        scrollPane.setVisible(false);
        mediaBox.getChildren().clear();
        tweetTextArea.clear();
        if(message != null && type.equals("default")){
            Message.showTemporaryMessage(tweetButton, "Tweet posted successfully");
        }
        tweetButton.setDisable(false);
        pollButton.setDisable(false);
        mediaButton.setDisable(false);
    }

    public void updateContent(int tweetId){
        Tweet newTweet = HomeController.getTweet(tweetId);
        TweetComponent newTweetComponent = HomeController.getTweetComponent(newTweet);
        if(content != null)
            content.getChildren().add(1, newTweetComponent);
        if(notFoundLabel != null)
            notFoundLabel.setVisible(false);
    }

    private void makePollScrollPane(){
        pollScrollPane = new ScrollPane();
        pollChoices = new VBox();

        tweetContent.getChildren().add(1, pollScrollPane);
        tweetContent.getChildren().remove(scrollPane);

        addChoiceButton = new JFXButton();

        removePollButton = new JFXButton("Remove Poll");
        removePollButton.setStyle("-fx-text-fill: #ef0909;");

        choice1 = new TextField();
        choice2 = new TextField();
        choice1.setTextFormatter(new TextFormatter<String>(change -> {
            String newText = change.getControlNewText();
            if (newText.length() > 25) {
                return null;
            } else {
                return change;
            }
        }));
        choice2.setTextFormatter(new TextFormatter<String>(change -> {
            String newText = change.getControlNewText();
            if (newText.length() > 25) {
                return null;
            } else {
                return change;
            }
        }));
        choice1.setPromptText("Choice 1");
        choice2.setPromptText("Choice 2");

        addChoiceBox = new HBox();
        addChoiceBox.setSpacing(10);
        addChoiceBox.setAlignment(Pos.CENTER_LEFT);

        addChoiceBox.getChildren().addAll(addChoiceButton);

        pollChoices.getChildren().addAll(choice1, choice2, addChoiceBox, removePollButton);

        pollScrollPane.setContent(pollChoices);

        setPollConfigure();
        setPollActions();
    }

    private void setPollConfigure(){
        pollScrollPane.setPrefSize(250,147);
        pollScrollPane.setMinSize(250,147);
        pollScrollPane.setMaxSize(250,147);

        addChoiceImageView.setFitWidth(17);
        addChoiceImageView.setFitHeight(17);

        addChoiceButton.setGraphic(addChoiceImageView);
        addChoiceButton.setPrefSize(17,17);
        addChoiceButton.setRipplerFill(Paint.valueOf("#858080"));
        addChoiceButton.setStyle("-fx-background-radius: 50");

        pollChoices.setSpacing(10);
        pollChoices.setPadding(new Insets(10));

        choice1.setMaxWidth(300);
        choice2.setMaxWidth(300);
        choice1.setPrefHeight(40);
        choice2.setPrefHeight(40);
        choice1.setStyle("-fx-background-color:#ffffff;" +
                "-fx-background-radius:15;" +
                "-fx-border-radius:15");
        choice2.setStyle("-fx-background-color:#ffffff;" +
                "-fx-background-radius:15;" +
                "-fx-border-radius:15");

        VBox.setVgrow(tweetTextArea, Priority.ALWAYS);
    }

    private void setPollActions(){
        choice1.textProperty().addListener((observable, oldValue, newValue) -> {
            tweetButton.setDisable(checkPollChoices(pollChoices.getChildren()));
        });

        choice2.textProperty().addListener((observable, oldValue, newValue) -> {
            tweetButton.setDisable(checkPollChoices(pollChoices.getChildren()));
        });

        addChoiceButton.setOnAction(e -> {
            if (pollChoices.getChildren().size() < 4 + 2) {
                TextField newChoice = new TextField();
                newChoice.setTextFormatter(new TextFormatter<String>(change -> {
                    String newText = change.getControlNewText();
                    if (newText.length() > 25) {
                        return null;
                    } else {
                        return change;
                    }
                }));
                newChoice.setPromptText("Choice " + (pollChoices.getChildren().size() + 1 - 2));
                newChoice.setMaxWidth(300);
                newChoice.setPrefHeight(40);
                newChoice.setStyle("-fx-background-color:#ffffff;" +
                        "-fx-background-radius:15;" +
                        "-fx-border-radius:15");

//                newChoice.textProperty().addListener((observable, oldValue, newValue) -> {
//                    addChoiceButton.setDisable(checkPollChoices(pollChoices.getChildren()));
//                });
                pollChoices.getChildren().add(newChoice);
                pollChoices.getChildren().remove(addChoiceBox);
                pollChoices.getChildren().add(addChoiceBox);
                pollChoices.getChildren().remove(removePollButton);
                pollChoices.getChildren().add(pollChoices.getChildren().size() , removePollButton);
                if (pollChoices.getChildren().size() == 4 + 2) {
                    addChoiceButton.setDisable(true);
                }
            }
        });

        addChoiceButton.setOnMouseEntered(event -> {
            addChoiceButton.setCursor(Cursor.HAND);
        });

        addChoiceButton.setOnMouseEntered(event -> {
            addChoiceButton.setCursor(Cursor.HAND);
        });

        removePollButton.setOnAction(e -> {
            tweetButton.setDisable(false);
            isPoll = false;
            pollButton.setDisable(false);
            mediaButton.setDisable(false);
            tweetContent.getChildren().remove(pollScrollPane);
        });
    }

    private boolean checkPollChoices(ObservableList<Node> Choices) {
        for (Node node : Choices) {
            if(node instanceof TextField){
                if (((TextField)node).getText().trim().isEmpty()) {
                    return true;
                }
            } else if(node instanceof HBox){
                HBox hBox = (HBox) node;
                for(Node node1 : hBox.getChildren()){
                    if(node instanceof TextField){
                        if (((TextField)node).getText().trim().isEmpty()) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

//    private boolean checkPollChoices(ObservableList<Node> Choices) {
//        for (Node node : Choices) {
//            if (((TextField)node).getText().trim().isEmpty()) {
//                return true;
//            }
//        }
//        return false;
//    }
}
