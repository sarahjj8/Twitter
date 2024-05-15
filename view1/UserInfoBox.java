package view;

import controller.ViewOthersProfileController;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import model.entity.Profile;

import java.io.File;
import java.io.IOException;
import java.net.URI;

public class UserInfoBox {
    private Profile profile;
    private AnchorPane info;
    private Image profileImage = new Image(String.valueOf(TweetComponent.class.getResource("icons/profile_image.png")));
    private ImageView profileImageView = new ImageView(profileImage);
    private Label usernameLabel;
    private Label userIdLabel;
    private Circle circleClipProfile;

    public UserInfoBox(Profile profile){
        this.profile = profile;
        info = new AnchorPane();
        usernameLabel = new Label(profile.getUser().getName() + " " + profile.getUser().getLastName());
        userIdLabel = new Label("@" + profile.getUsername());
        circleClipProfile = new Circle(20);
        circleClipProfile.setStroke(Color.GRAY);
        circleClipProfile.setFill(Color.SNOW);
        circleClipProfile.setFill(new ImagePattern(profileImage));
        updateAvatarImage(profile.getAvatarPath());
        info.getChildren().addAll(usernameLabel, userIdLabel, circleClipProfile);
        setConfig();
        setLocation();
        setActions();
    }

    public AnchorPane getInfo() {
        return info;
    }

    private void setConfig()
    {
        info.setPrefSize(320,70);
        info.setMinSize(320,70);
        info.setMaxSize(320,70);
        info.setStyle("-fx-background-color:rgba(255,255,255,0);" +
                "-fx-background-radius:10;" +
                "-fx-border-radius:10;" +
                "-fx-border-color:rgba(255,255,255,0)");

        usernameLabel.setFont(Font.font("Franklin Gothic", FontWeight.BLACK,15));
        usernameLabel.setStyle("-fx-text-fill:#0f1419;");
        userIdLabel.setFont(Font.font("Franklin Gothic", FontWeight.THIN,12));
        userIdLabel.setStyle("-fx-text-fill:#536572;");
    }

    public void setLocation()
    {
        info.setTopAnchor(circleClipProfile,10.0);
        info.setLeftAnchor(circleClipProfile,10.0);
        info.setTopAnchor(usernameLabel,12.0);
        info.setLeftAnchor(usernameLabel,60.0);
        info.setTopAnchor(userIdLabel,34.0);
        info.setLeftAnchor(userIdLabel,60.0);
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
        usernameLabel.setOnMouseEntered(event -> {
            usernameLabel.setCursor(Cursor.HAND);
        });
        usernameLabel.setOnMouseClicked(event -> {
            try {
                ViewOthersProfileController viewOthersProfileController = new ViewOthersProfileController();
                viewOthersProfileController.setProfile(userIdLabel.getText().substring(1));
                FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/viewOthersProfile.fxml"));
                loader.setController(viewOthersProfileController);
                Parent profilePage = loader.load();
                Scene profileScene = new Scene(profilePage);
                Stage primaryStage = (Stage) usernameLabel.getScene().getWindow();
                primaryStage.setScene(profileScene);
                primaryStage.show();
            } catch (IOException e) {
                Message.showTemporaryMessage(usernameLabel, "an error occurred while loading " + usernameLabel.textProperty() + " profile");
                e.printStackTrace();
            }
        });
    }
}
