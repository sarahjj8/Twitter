package view;

import com.jfoenix.controls.JFXButton;
import controller.ViewOthersProfileController;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import model.Client;
import model.entity.*;

import java.io.File;
import java.net.URI;
import java.util.Map;

public class CompleteProfileComponent extends AnchorPane{
    private Profile profile;
    private Image avatarImage = new Image(String.valueOf(TweetComponent.class.getResource("icons/profile_image.png")));
    private Image headerImage = new Image(String.valueOf(TweetComponent.class.getResource("icons/blank_header.png")));
    private static Image websiteImage = new Image(String.valueOf(TweetComponent.class.getResource("icons/website.png")));
    private static Image joinedImage = new Image(String.valueOf(TweetComponent.class.getResource("icons/joined.png")));
    private static Image locationImage = new Image(String.valueOf(TweetComponent.class.getResource("icons/grey_location.png")));
    private static Image followImage = new Image(String.valueOf(TweetComponent.class.getResource("icons/follow.png")));
    private static Image followingImage = new Image(String.valueOf(TweetComponent.class.getResource("icons/following.png")));
    private static Image moreImage = new Image(String.valueOf(TweetComponent.class.getResource("icons/more2.png")));
    private static Image setUpProfileImage = new Image(String.valueOf(TweetComponent.class.getResource("icons/Set_up_profile.png")));
    private ImageView avatarImageView = new ImageView(avatarImage);
    private ImageView headerImageView = new ImageView(headerImage);
    private static ImageView websiteImageView = new ImageView(websiteImage);
    private static ImageView joinedImageView = new ImageView(joinedImage);
    private static ImageView locationImageView = new ImageView(locationImage);
    private ImageView followStatusImageView = new ImageView(followImage);
    private static ImageView moreImageView = new ImageView(moreImage);
    private MenuComponent menuComponent;
    private Label usernameLabel;
    private Label userIdLabel;
    private Circle circleClipProfile;
    private Rectangle headerRectangle;
    private Label joinedDateLabel;
    private Label numberOfFollowersLabel;
    private Label numberOfFollowingLabel;
    private Label websiteLabel;
    private Text bioText;
    private StackPane bioStackPane;
    private Label locationLabel;
    private JFXButton moreButton;
    private JFXButton followButton;
    private StackPane headerPane;
    private VBox info;
    private ViewOthersProfileController viewOthersProfileController;
    private boolean isFollowed;
    private boolean isOwner;

    public CompleteProfileComponent(Profile profile, ViewOthersProfileController viewOthersProfileController){
        this.profile = profile;
        this.isFollowed = isFollowed;
        this.isOwner = isOwner;
        this.viewOthersProfileController = viewOthersProfileController;
        usernameLabel = new Label(profile.getUser().getName() + " " + profile.getUser().getLastName());
        userIdLabel = new Label("@" + profile.getUsername());
        if (profile.getUser().getDateCreated() != null)
            joinedDateLabel = new Label("Joined " + User.getMonthYearString(profile.getUser().getDateCreated()));
        else
            joinedDateLabel = new Label("");
        numberOfFollowersLabel = new Label(profile.getNumberOfFollowers() + " Followers");
        numberOfFollowingLabel = new Label(profile.getNumberOfFollowing() + " Following");
        if(profile.getWebsiteAddress() == null) {
            websiteLabel = new Label("");
            websiteLabel.setVisible(false);
        } else
            websiteLabel = new Label(profile.getWebsiteAddress());
        //to grow according to the bio
        bioText = new Text(profile.getBio());
        bioText.setFont(Font.font("Franklin Gothic", FontWeight.THIN,12));
        bioText.setWrappingWidth(280);
        bioStackPane = new StackPane(bioText);
        bioStackPane.setMaxWidth(Region.USE_PREF_SIZE);
        if(profile.getLocation() == null) {
            locationLabel = new Label("");
            locationLabel.setVisible(false);
        } else
            locationLabel = new Label(profile.getLocation());
        moreButton = new JFXButton();
        followButton = new JFXButton();
        circleClipProfile = new Circle(35);
        circleClipProfile.setStroke(Color.GRAY);
        circleClipProfile.setFill(Color.SNOW);
        circleClipProfile.setFill(new ImagePattern(avatarImage));

        updateAvatarImage(profile.getAvatarPath());
        updateHeaderImage(profile.getHeaderPath());

        headerRectangle = new Rectangle();
        HBox websiteBox = new HBox(5);
        websiteBox.setAlignment(Pos.CENTER_LEFT);
        websiteBox.setPadding(new Insets(5));
        websiteBox.getChildren().addAll(websiteImageView, websiteLabel);

        HBox locationBox = new HBox(5);
        locationBox.setAlignment(Pos.CENTER_LEFT);
        locationBox.setPadding(new Insets(5));
        locationBox.getChildren().addAll(locationImageView, locationLabel);

        HBox joinedBox = new HBox(5);
        joinedBox.setAlignment(Pos.CENTER_LEFT);
        joinedBox.setPadding(new Insets(5));
        joinedBox.getChildren().addAll(joinedImageView, joinedDateLabel);

        setIsOwner();
        setIsFollowed();
        if(isOwner)
            followStatusImageView.setImage(setUpProfileImage);
        else if(isFollowed)
            followStatusImageView.setImage(followingImage);

        HBox followBox = new HBox(15);
        followBox.setAlignment(Pos.CENTER_LEFT);
        followBox.setPadding(new Insets(5));
        followBox.getChildren().addAll(numberOfFollowingLabel, numberOfFollowersLabel);

        info = new VBox(0);
        info.setAlignment(Pos.CENTER_LEFT);
        info.setPadding(new Insets(5));
        info.getChildren().addAll(bioStackPane, locationBox, websiteBox, joinedBox, followBox);

        menuComponent = new MenuComponent(profile, this, isFollowed);
        moreButton.setContextMenu(menuComponent.getContextMenu());

        this.getChildren().addAll(usernameLabel,userIdLabel,circleClipProfile,info,moreButton,followButton,headerImageView);
        setConfig();
        setLocation(info);
        setActions();
    }

    private void setIsFollowed(){
        if(isOwner){
            return;
        }
        Map<String, String> params = Map.of("followed username", profile.getUsername(), "follower username", Client.getUsername());
        HttpClientResponse response = HttpClientResponse.sendRequest(HttpClientResponse.createURL("isFollowedBy"), null, params, "GET");
        if(response.getResponseCode() != 200){
            Message.showTemporaryMessage(followButton, response.getResponse());
            isFollowed = false;
        } else {
            if(response.getResponse().equals("true"))
                isFollowed = true;
            else
                isFollowed = false;
        }
    }

    private void setIsOwner(){
        if(profile.getUsername().equals(Client.getUsername()))
            isOwner = true;
        else
            isOwner = false;
    }

    private void setConfig(){
        this.setPrefWidth(320);
        this.setMinWidth(320);
        this.setMaxWidth(320);
        this.prefHeightProperty().bind(Bindings.createDoubleBinding(
                () -> this.getChildren().stream()
                        .mapToDouble(node -> node.getBoundsInParent().getMaxY() + node.getLayoutY())
                        .max()
                        .orElse(0),
                this.getChildren()
        ));

        this.setStyle("-fx-background-color:#ffffff;" +
                "-fx-background-radius:10;" +
                "-fx-border-radius:10;" +
                "-fx-border-color:rgba(192,192,192,0)");
        usernameLabel.setFont(Font.font("Franklin Gothic", FontWeight.BLACK,20));
        usernameLabel.setStyle("-fx-text-fill:#0f1419;");
        userIdLabel.setFont(Font.font("Franklin Gothic", FontWeight.THIN,15));
        userIdLabel.setStyle("-fx-text-fill:#536572;");
        bioStackPane.setStyle("-fx-text-fill:#0f1419;");
        websiteLabel.setFont(Font.font("Franklin Gothic", FontWeight.BLACK,12));
        websiteLabel.setStyle("-fx-text-fill:#0e9ff1;" +
                "-fx-underline: true;");
        locationLabel.setFont(Font.font("Franklin Gothic", FontWeight.BLACK,12));
        locationLabel.setStyle("-fx-text-fill:#536572;");
        numberOfFollowersLabel.setFont(Font.font("Franklin Gothic", FontWeight.BLACK,12));
        numberOfFollowersLabel.setStyle("-fx-text-fill:#536572;");
        numberOfFollowingLabel.setFont(Font.font("Franklin Gothic", FontWeight.BLACK,12));
        numberOfFollowingLabel.setStyle("-fx-text-fill:#536572;");
        joinedDateLabel.setFont(Font.font("Franklin Gothic", FontWeight.BLACK,12));
        joinedDateLabel.setStyle("-fx-text-fill:#536572;");

        headerImageView.setFitHeight(200);
        headerImageView.setPreserveRatio(true);
        headerRectangle.setArcHeight(10.0);
        headerRectangle.setArcWidth(10.0);
        headerImageView.fitWidthProperty().bind(this.widthProperty());
        headerImageView.setClip(null);
        headerPane = new StackPane();
        headerPane.getChildren().addAll(headerRectangle, headerImageView);
        this.getChildren().add(headerPane);

        locationImageView.setPreserveRatio(true);
        locationImageView.setFitWidth(15);
        websiteImageView.setPreserveRatio(true);
        websiteImageView.setFitWidth(12);
        joinedImageView.setPreserveRatio(true);
        joinedImageView.setFitWidth(15);

        joinedImageView.setFitHeight(15);
        joinedImageView.setFitWidth(15);
        locationImageView.setFitHeight(15);
        locationImageView.setFitWidth(15);
        websiteImageView.setFitHeight(15);
        websiteImageView.setFitWidth(15);
        locationImageView.setFitHeight(15);
        locationImageView.setFitWidth(15);

        followButton.setGraphic(followStatusImageView);
        followButton.setPrefSize(60,60);
        followButton.setRipplerFill(Paint.valueOf("#FFFFFF00"));
        followButton.setStyle("-fx-background-radius: 50");
        followStatusImageView.setPreserveRatio(true);
        followStatusImageView.setFitWidth(60);
        if(isOwner)
            followStatusImageView.setFitWidth(80);

        moreButton.setGraphic(moreImageView);
        moreButton.setPrefSize(20,20);
        moreButton.setRipplerFill(Paint.valueOf("#858080"));
        moreButton.setStyle("-fx-background-radius: 50");
        followStatusImageView.setPreserveRatio(true);
        moreImageView.setFitWidth(20);
        moreImageView.setFitHeight(20);

        if(isOwner){
            moreButton.setVisible(false);
        }
    }

    private void setLocation(VBox info){
        AnchorPane.setTopAnchor(headerImageView,0.0);
        AnchorPane.setLeftAnchor(headerImageView,0.0);
        double avatarY = headerImageView.getFitHeight() - 130;
        AnchorPane.setLeftAnchor(circleClipProfile, 10.0);
        AnchorPane.setTopAnchor(circleClipProfile, avatarY);
        AnchorPane.setTopAnchor(headerPane, 0.0);
        AnchorPane.setLeftAnchor(headerPane, 0.0);
        AnchorPane.setRightAnchor(headerPane, 0.0);

        circleClipProfile.toFront();

        AnchorPane.setTopAnchor(usernameLabel,150.0);
        AnchorPane.setLeftAnchor(usernameLabel,10.0);
        AnchorPane.setTopAnchor(userIdLabel,175.0);
        AnchorPane.setLeftAnchor(userIdLabel,10.0);

        AnchorPane.setTopAnchor(info,200.0);
        AnchorPane.setLeftAnchor(info,10.0);

        AnchorPane.setTopAnchor(followButton,avatarY + 25);
        AnchorPane.setRightAnchor(followButton,2.0);
        AnchorPane.setTopAnchor(moreButton,avatarY + 40);
        AnchorPane.setRightAnchor(moreButton,70.0);
    }

    private void setActions() {
        websiteLabel.setOnMouseEntered(event -> {
            websiteLabel.setCursor(Cursor.HAND);
        });
        followButton.setOnMouseEntered(event -> {
            followButton.setCursor(Cursor.HAND);
        });
        moreButton.setOnMouseEntered(event -> {
            moreButton.setCursor(Cursor.HAND);
        });
        websiteLabel.setOnMouseClicked(event -> {
            String websiteUrl = websiteLabel.getText();
            if (websiteUrl != null && !websiteUrl.isEmpty()) {
                // Open the website in a browser
                try {
                    java.awt.Desktop.getDesktop().browse(new URI(websiteUrl));
                } catch (Exception e) {
                    Message.showTemporaryMessage(websiteLabel, "An exception occurred while opening the website!");
                }
            }
        });
        if (!isOwner){
            followButton.setOnMouseClicked(event -> {
                follow();
            });
        }
        if (isOwner){
            followButton.setOnMouseClicked(event -> {
                ProfileEditor profileEditor = new ProfileEditor(profile, this);
                profileEditor.showAndWait();
            });
        }
        moreButton.setOnMouseClicked(event -> {
            menuComponent.getContextMenu().show(moreButton, Side.BOTTOM, 0, 0);
        });
    }

    public void follow() {
        if (isFollowed) {
            String url = "http://localhost:8081/unfollow";
            Map<String, String> deleteParams = Map.of("followed username", profile.getUsername(), "follower username", Client.getUsername());
            model.entity.HttpClientResponse response = model.entity.HttpClientResponse.sendRequest(url, null, deleteParams, "DELETE");
            if (response != null) {
                if (response.getResponseCode() == 200) {
                    followStatusImageView.setImage(followImage);
                    isFollowed = false;
                    numberOfFollowersLabel.setText((getNumber(numberOfFollowersLabel.getText()) - 1) + " Followers");
                    menuComponent.updateFollowItem();
                } else {
                    Message.showTemporaryMessage(followButton, response.getResponse());
                }
            } else {
                Message.showTemporaryMessage(followButton, "Oops! Something went wrong.");
            }
        } else {
            String url = "http://localhost:8081/follow";
            String requestBody = "[\"" + Client.getUsername() + "\", \"" + profile.getUsername() + "\"]";
            model.entity.HttpClientResponse response = model.entity.HttpClientResponse.sendRequest(url, requestBody, null, "POST");
            if (response != null) {
                if (response.getResponseCode() == 201) {
                    followStatusImageView.setImage(followingImage);
                    isFollowed = true;
                    numberOfFollowersLabel.setText((getNumber(numberOfFollowersLabel.getText()) + 1) + " Followers");
                    menuComponent.updateFollowItem();
                } else {
                    Message.showTemporaryMessage(followButton, response.getResponse());
                }
            } else {
                Message.showTemporaryMessage(followButton, "Oops! Something went wrong.");
            }
        }
    }

    public void block(){
        String url = "http://localhost:8081/block";
        String requestBody = "[\"" + Client.getUsername() + "\", \"" + profile.getUsername() + "\"]";
        model.entity.HttpClientResponse response = model.entity.HttpClientResponse.sendRequest(url, requestBody, null, "POST");    if (response != null) {
            if (response.getResponseCode() == 201) {
                followButton.setVisible(false);
                moreButton.setVisible(false);
                if(viewOthersProfileController != null)
                    viewOthersProfileController.removeTweets();
                if(!isOwner && isFollowed){
                    numberOfFollowersLabel.setText((getNumber(numberOfFollowersLabel.getText()) - 1) + " Followers");
                    isFollowed = false;
                }
            } else {
                Message.showTemporaryMessage(moreButton, response.getResponse());
            }
        } else {
            Message.showTemporaryMessage(moreButton, "Oops! Something went wrong.");
        }
    }

    private static int getNumber(String text){
        return Integer.parseInt(text.replaceAll("[^0-9]", ""));
    }

    public Profile getProfile() {
        return profile;
    }

    public void updateAvatar(Image avatarImage){
        this.avatarImage = avatarImage;
        avatarImageView = new ImageView(avatarImage);
        circleClipProfile.setFill(new ImagePattern(avatarImage));
    }

    public void updateHeader(Image headerImage){
        this.headerImage = headerImage;
        ImageView newHeaderImageView = new ImageView(headerImage);
        newHeaderImageView.setImage(headerImage);

        StackPane parentContainer = (StackPane) headerImageView.getParent();

        if (parentContainer != null) {
            int headerImageViewIndex = parentContainer.getChildren().indexOf(headerImageView);
            if (headerImageViewIndex >= 0) {
                parentContainer.getChildren().set(headerImageViewIndex, newHeaderImageView);
            }
        }
        headerImageView = newHeaderImageView;

        setConfig();
        setLocation(info);
    }

    public void update(Image headerImage, Image avatarImage){
        this.headerImage = headerImage;
        this.avatarImage = avatarImage;

        avatarImageView = new ImageView(avatarImage);
        circleClipProfile.setFill(new ImagePattern(avatarImage));

        ImageView newHeaderImageView = new ImageView(headerImage);
        newHeaderImageView.setImage(headerImage);

        StackPane parentContainer = (StackPane) headerImageView.getParent();
        if (parentContainer != null) {
            int headerImageViewIndex = parentContainer.getChildren().indexOf(headerImageView);
            if (headerImageViewIndex >= 0) {
                parentContainer.getChildren().set(headerImageViewIndex, newHeaderImageView);
            }
        }
        headerImageView = newHeaderImageView;

        websiteLabel.setText(profile.getWebsiteAddress());
        locationLabel.setText(profile.getLocation());

        bioText.setText(profile.getBio());

        setConfig();
        setLocation(info);
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

    public void updateHeaderImage(String imagePath) {
        if (imagePath != null) {
            File file = new File(imagePath);
            if (file.exists()) {
                Image image = new Image(file.toURI().toString());
                image = Save.resizeImage(image, 1500, 500);
                if (image != null) {
                    headerImage = image;
                    ImageView newHeaderImageView = new ImageView(headerImage);
                    newHeaderImageView.setImage(headerImage);
                    StackPane parentContainer = (StackPane) headerImageView.getParent();
                    if (parentContainer != null) {
                        int headerImageViewIndex = parentContainer.getChildren().indexOf(headerImageView);
                        if (headerImageViewIndex >= 0) {
                            parentContainer.getChildren().set(headerImageViewIndex, newHeaderImageView);
                        }
                    }
                    headerImageView = newHeaderImageView;
                }
            }
        }
    }
}
