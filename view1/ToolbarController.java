package view;

import com.jfoenix.controls.JFXButton;
import controller.HomeController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import java.io.IOException;
import controller.HomeController;
public class ToolbarController extends VBox {
    private static Image twitterImage = new Image(String.valueOf(ToolbarController.class.getResource("icons/twitter_icon.png")));
    private static Image homeImage = new Image(String.valueOf(ToolbarController.class.getResource("icons/home.png")));
    private static Image exploreImage = new Image(String.valueOf(ToolbarController.class.getResource("icons/explore.png")));
    private static Image notificationsImage = new Image(String.valueOf(ToolbarController.class.getResource("icons/notifications.png")));
    private static Image messagesImage = new Image(String.valueOf(ToolbarController.class.getResource("icons/messages.png")));
    private static Image verifiedImage = new Image(String.valueOf(ToolbarController.class.getResource("icons/verified.png")));
    private static Image profileImage = new Image(String.valueOf(ToolbarController.class.getResource("icons/profile.png")));
    private static Image moreImage = new Image(String.valueOf(ToolbarController.class.getResource("icons/more.png")));
    private static Image tweetImage = new Image(String.valueOf(ToolbarController.class.getResource("icons/post_tweet.png")));

    private static Image blackHomeImage = new Image(String.valueOf(ToolbarController.class.getResource("icons/black_home.png")));
    private static Image blackExploreImage = new Image(String.valueOf(ToolbarController.class.getResource("icons/black_explore.png")));
    private static Image blackNotificationsImage = new Image(String.valueOf(ToolbarController.class.getResource("icons/black_notifications.png")));
    private static Image blackMessagesImage = new Image(String.valueOf(ToolbarController.class.getResource("icons/black_messages.png")));
    private static Image blackProfileImage = new Image(String.valueOf(ToolbarController.class.getResource("icons/black_profile.png")));

    private ImageView twitterImageView = new ImageView(twitterImage);
    private ImageView homeImageView = new ImageView(homeImage);
    @FXML
    private ImageView exploreImageView = new ImageView(exploreImage);
    private ImageView notificationsImageView = new ImageView(notificationsImage);
    private ImageView messagesImageView = new ImageView(messagesImage);
    private ImageView verifiedImageView = new ImageView(verifiedImage);
    private ImageView profileImageView = new ImageView(profileImage);
    private ImageView moreImageView = new ImageView(moreImage);
    private ImageView tweetImageView = new ImageView(tweetImage);

    @FXML
    private JFXButton twitterButton;
    private JFXButton homeButton;
    @FXML
    private JFXButton exploreButton;
    private JFXButton notificationsButton;
    private JFXButton messagesButton;
    private JFXButton verifiedButton;
    private JFXButton profileButton;
    private JFXButton moreButton;
    private JFXButton tweetButton;

    /**
     * to track the currently selected page
     */
    private ImageView selectedImageView;
    private String selectedPage;

    public ToolbarController() {
        setSpacing(10);
        setPadding(new Insets(10));
        setAlignment(Pos.TOP_CENTER);
        setStyle("-fx-background-color: #EFEFEF;");

        twitterButton = new JFXButton();
        homeButton = new JFXButton();
        exploreButton = new JFXButton();
        notificationsButton = new JFXButton();
        profileButton = new JFXButton();
        messagesButton = new JFXButton();
        verifiedButton = new JFXButton();
        moreButton = new JFXButton();
        tweetButton = new JFXButton();

        getChildren().addAll(twitterButton, homeButton, exploreButton, notificationsButton, messagesButton, verifiedButton, profileButton, moreButton, tweetButton);
        setConfigure();
        setActions();

        // Setting the initial selected page to home
        selectedImageView = homeImageView;
        selectedPage = "Home";
        homeImageView.setImage(blackHomeImage);
        selectedImageView.setImage(blackHomeImage);
    }

    private void setConfigure() {
        twitterImageView.setFitWidth(23);
        twitterImageView.setFitHeight(23);
        twitterButton.setGraphic(twitterImageView);
        twitterButton.setGraphic(twitterImageView);
        twitterButton.setPrefSize(23, 23);
        twitterButton.setRipplerFill(Paint.valueOf("#858080"));
        twitterButton.setStyle("-fx-background-radius: 53");

        homeImageView.setFitWidth(24);
        homeImageView.setFitHeight(24);
        homeButton.setGraphic(homeImageView);
        homeButton.setGraphic(homeImageView);
        homeButton.setPrefSize(23, 23);
        homeButton.setRipplerFill(Paint.valueOf("#858080"));
        homeButton.setStyle("-fx-background-radius: 53");

        exploreImageView.setFitWidth(23);
        exploreImageView.setFitHeight(23);
        exploreButton.setGraphic(exploreImageView);
        exploreButton.setGraphic(exploreImageView);
        exploreButton.setPrefSize(23, 23);
        exploreButton.setRipplerFill(Paint.valueOf("#858080"));
        exploreButton.setStyle("-fx-background-radius: 53");

        notificationsImageView.setFitWidth(23);
        notificationsImageView.setFitHeight(23);
        notificationsButton.setGraphic(notificationsImageView);
        notificationsButton.setGraphic(notificationsImageView);
        notificationsButton.setPrefSize(23, 23);
        notificationsButton.setRipplerFill(Paint.valueOf("#858080"));
        notificationsButton.setStyle("-fx-background-radius: 53");

        messagesImageView.setFitWidth(23);
        messagesImageView.setFitHeight(23);
        messagesButton.setGraphic(messagesImageView);
        messagesButton.setGraphic(messagesImageView);
        messagesButton.setPrefSize(23, 23);
        messagesButton.setRipplerFill(Paint.valueOf("#858080"));
        messagesButton.setStyle("-fx-background-radius: 53");

        verifiedImageView.setFitWidth(25);
        verifiedImageView.setFitHeight(25);
        verifiedButton.setGraphic(verifiedImageView);
        verifiedButton.setGraphic(verifiedImageView);
        verifiedButton.setPrefSize(23, 23);
        verifiedButton.setRipplerFill(Paint.valueOf("#858080"));
        verifiedButton.setStyle("-fx-background-radius: 53");

        profileImageView.setFitWidth(23);
        profileImageView.setFitHeight(23);
        profileButton.setGraphic(profileImageView);
        profileButton.setGraphic(profileImageView);
        profileButton.setPrefSize(23, 23);
        profileButton.setRipplerFill(Paint.valueOf("#858080"));
        profileButton.setStyle("-fx-background-radius: 53");

        moreImageView.setFitWidth(25);
        moreImageView.setFitHeight(25);
        moreButton.setGraphic(moreImageView);
        moreButton.setGraphic(moreImageView);
        moreButton.setPrefSize(23, 23);
        moreButton.setRipplerFill(Paint.valueOf("#858080"));
        moreButton.setStyle("-fx-background-radius: 53");

        tweetImageView.setFitWidth(30);
        tweetImageView.setFitHeight(30);
        tweetButton.setGraphic(tweetImageView);
        tweetButton.setGraphic(tweetImageView);
        tweetButton.setPrefSize(25, 25);
        tweetButton.setRipplerFill(Paint.valueOf("#858080"));
        tweetButton.setStyle("-fx-background-radius: 53");
    }

    public void goToExplorePage(){
        handleButtonClick(exploreImageView, blackExploreImage);
        selectedPage = "Explore";
    }

    public void goToMessagesPage(){
        handleButtonClick(messagesImageView, blackMessagesImage);
        selectedPage = "Messages";
    }
    public void goToProfilePage(){
        handleButtonClick(profileImageView, blackProfileImage);
        selectedPage = "Profile";
    }
    public void goToTweetPage(){
        handleButtonClick(tweetImageView, tweetImage);
        selectedPage = "Post a tweet";
    }

    public void goBackToMessages(){
        handleButtonClick(messagesImageView, blackMessagesImage);
        selectedPage = "Messages";
        try {
            Parent messagesPage = FXMLLoader.load(getClass().getResource("fxml/messages.fxml"));
            Scene messagesScene = new Scene(messagesPage);
            Stage primaryStage = (Stage) messagesButton.getScene().getWindow();
            primaryStage.setScene(messagesScene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void goBackToExplore(){
        handleButtonClick(messagesImageView, blackMessagesImage);
        selectedPage = "Explore";
        try {
            Parent explorePage = FXMLLoader.load(getClass().getResource("fxml/explore.fxml"));
            Scene exploreScene = new Scene(explorePage);
            Stage primaryStage = (Stage) exploreButton.getScene().getWindow();
            primaryStage.setScene(exploreScene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setActions() {
        twitterButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                handleButtonClick(homeImageView, blackHomeImage);
                selectedPage = "Home";
                try {
                    Parent explorePage = FXMLLoader.load(getClass().getResource("fxml/timeline.fxml"));
                    Scene exploreScene = new Scene(explorePage);
                    Stage primaryStage = (Stage) twitterButton.getScene().getWindow();
                    primaryStage.setScene(exploreScene);
                    primaryStage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        homeButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                handleButtonClick(homeImageView, blackHomeImage);
                selectedPage = "Home";
                try {
                    Parent explorePage = FXMLLoader.load(getClass().getResource("fxml/timeline.fxml"));
                    Scene exploreScene = new Scene(explorePage);
                    Stage primaryStage = (Stage) homeButton.getScene().getWindow();
                    primaryStage.setScene(exploreScene);
                    primaryStage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        exploreButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                handleButtonClick(exploreImageView, blackExploreImage);
                selectedPage = "Explore";
                try {
                    Parent explorePage = FXMLLoader.load(getClass().getResource("fxml/explore.fxml"));
                    Scene exploreScene = new Scene(explorePage);
                    Stage primaryStage = (Stage) exploreButton.getScene().getWindow();
                    primaryStage.setScene(exploreScene);
                    primaryStage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        notificationsButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                handleButtonClick(notificationsImageView, blackNotificationsImage);
                selectedPage = "Notifications";
            }
        });

        messagesButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                handleButtonClick(messagesImageView, blackMessagesImage);
                selectedPage = "Messages";
                try {
                    Parent explorePage = FXMLLoader.load(getClass().getResource("fxml/messages.fxml"));
                    Scene messagesScene = new Scene(explorePage);
                    Stage primaryStage = (Stage) messagesButton.getScene().getWindow();
                    primaryStage.setScene(messagesScene);
                    primaryStage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        profileButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                handleButtonClick(profileImageView, blackProfileImage);
                selectedPage = "Profile";
                try {
                    Parent profilePage = FXMLLoader.load(getClass().getResource("fxml/profile.fxml"));
                    Scene profileScene = new Scene(profilePage);
                    Stage primaryStage = (Stage) profileButton.getScene().getWindow();
                    primaryStage.setScene(profileScene);
                    primaryStage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        tweetButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                handleButtonClick(profileImageView, blackProfileImage);
                selectedPage = "Post a tweet";
                try {
                    Parent tweetPage = FXMLLoader.load(getClass().getResource("fxml/postTweet.fxml"));
                    Scene tweetScene = new Scene(tweetPage);
                    Stage primaryStage = (Stage) tweetButton.getScene().getWindow();
                    primaryStage.setScene(tweetScene);
                    primaryStage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        exploreButton.setOnMouseEntered(event -> {
            exploreButton.setCursor(Cursor.HAND);
        });
        homeButton.setOnMouseEntered(event -> {
            homeButton.setCursor(Cursor.HAND);
        });
        twitterButton.setOnMouseEntered(event -> {
            twitterButton.setCursor(Cursor.HAND);
        });
        tweetButton.setOnMouseEntered(event -> {
            tweetButton.setCursor(Cursor.HAND);
        });
        messagesButton.setOnMouseEntered(event -> {
            messagesButton.setCursor(Cursor.HAND);
        });
        notificationsButton.setOnMouseEntered(event -> {
            notificationsButton.setCursor(Cursor.HAND);
        });
        verifiedButton.setOnMouseEntered(event -> {
            verifiedButton.setCursor(Cursor.HAND);
        });
        profileButton.setOnMouseEntered(event -> {
            profileButton.setCursor(Cursor.HAND);
        });
        moreButton.setOnMouseEntered(event -> {
            moreButton.setCursor(Cursor.HAND);
        });
    }

    private void handleButtonClick(ImageView imageView, Image blackImage) {
        if (selectedImageView != null) {
            selectedImageView.setImage(getDefaultImage(selectedImageView));
        }
        selectedImageView = imageView;
        selectedImageView.setImage(blackImage);
    }

    /**
     * This method gets the default image based on the currently selected element
     */
    private Image getDefaultImage(ImageView imageView) {
        if (imageView == homeImageView) {
            return homeImage;
        } else if (imageView == exploreImageView) {
            return exploreImage;
        } else if (imageView == notificationsImageView) {
            return notificationsImage;
        } else if (imageView == messagesImageView) {
            return messagesImage;
        } else if (imageView == profileImageView) {
            return profileImage;
        } else if (imageView == tweetImageView) {
            return tweetImage;
        }
        return null;
    }
}