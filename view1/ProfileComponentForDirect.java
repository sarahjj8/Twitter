package view;

import controller.ViewOthersProfileController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import model.entity.Profile;
import view.TweetComponent;

import java.io.File;
import java.io.IOException;

public class ProfileComponentForDirect extends AnchorPane {
    private Profile profile;
    private Circle circleClipProfile;
    private Label usernameLabel;
    private Label userIdLabel;
    private Image profileImage = new Image(String.valueOf(TweetComponent.class.getResource("icons/profile_image.png")));
    private ImageView profileImageView = new ImageView(profileImage);

    public ProfileComponentForDirect(Profile profile) {
        this.profile = profile;
        usernameLabel = new Label(profile.getUser().getName() + " " + profile.getUser().getLastName());
        userIdLabel = new Label("@" + profile.getUsername());
        circleClipProfile = new Circle(20);
        circleClipProfile.setStroke(Color.GRAY);
        circleClipProfile.setFill(Color.SNOW);
        circleClipProfile.setFill(new ImagePattern(profileImage));
        updateAvatarImage(profile.getAvatarPath());

        this.getChildren().addAll(usernameLabel,userIdLabel,circleClipProfile);
        setConfig();
        setLocation();
        setActions();
    }

    private void setConfig() {
        this.setPrefSize(320,70);
        this.setMinSize(320,70);
        this.setMaxSize(320,70);
        this.setStyle("-fx-background-color:#ffffff;" +
                "-fx-background-radius:10;" +
                "-fx-border-radius:10;" +
                "-fx-border-color:rgba(192,192,192,0)");
        usernameLabel.setFont(Font.font("Franklin Gothic", FontWeight.BLACK,15));
        usernameLabel.setStyle("-fx-text-fill:#0f1419;");
        userIdLabel.setFont(Font.font("Franklin Gothic", FontWeight.THIN,12));
        userIdLabel.setStyle("-fx-text-fill:#536572;");
    }

    private void setLocation() {
        AnchorPane.setTopAnchor(circleClipProfile,10.0);
        AnchorPane.setLeftAnchor(circleClipProfile,10.0);
        AnchorPane.setTopAnchor(usernameLabel,12.0);
        AnchorPane.setLeftAnchor(usernameLabel,60.0);
        AnchorPane.setTopAnchor(userIdLabel,34.0);
        AnchorPane.setLeftAnchor(userIdLabel,60.0);
    }

    public String getUserId(){
        return userIdLabel.getText();
    }

    public void updateAvatarImage(String imagePath) {
        if (imagePath != null) {
            File file = new File(imagePath);
            if (file.exists()) {
                Image image = new Image(file.toURI().toString());
                if (image != null) {
                    profileImageView.setImage(image);
                    circleClipProfile.setFill(new ImagePattern(image));
                    profileImage = image;
                }
            }
        }
    }

    private void setActions() {
        DropShadow shadow = new DropShadow();
        shadow.setRadius(5);
        shadow.setColor(Color.GRAY);
        this.setOnMouseEntered(event -> {
            this.setCursor(Cursor.HAND);
            this.setEffect(shadow);
        });
        this.setOnMouseExited(event -> {
            this.setEffect(null);
        });
    }
}

