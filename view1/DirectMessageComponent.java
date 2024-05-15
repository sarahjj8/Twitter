package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import model.Client;
import model.entity.Direct;
import model.entity.Profile;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DirectMessageComponent extends HBox {
    private Profile profile;
    private Direct direct;
    private Label messageLabel;
    private Label dateLabel;
    private VBox messageContent;
    private boolean isCurrentUserSender;
    private Circle circleClipProfile;
    private Image profileImage = new Image(String.valueOf(TweetComponent.class.getResource("icons/profile_image.png")));
    private ImageView profileImageView = new ImageView(profileImage);

    public DirectMessageComponent(Profile profile, Direct direct, boolean isCurrentUserSender){
        this.profile = profile;
        this.direct = direct;
        this.isCurrentUserSender = isCurrentUserSender;

        setIsCurrentUserSender();

        messageLabel = new Label(direct.getText());
        messageContent = new VBox();

        LocalDateTime dateTime = direct.getDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");
        String formattedDateTime = dateTime.format(formatter);
        dateLabel = new Label(formattedDateTime);

        messageContent.getChildren().addAll(messageLabel, dateLabel);

        circleClipProfile = new Circle(13);
        circleClipProfile.setStroke(javafx.scene.paint.Color.GRAY);
        circleClipProfile.setFill(Color.SNOW);
        circleClipProfile.setFill(new ImagePattern(profileImage));
        updateAvatarImage(profile.getAvatarPath());

        setConfig();
        setLocation();
    }

    private void setIsCurrentUserSender(){
        if(direct.getSenderUsername().equals(Client.getUsername()))
            isCurrentUserSender = true;
        else
            isCurrentUserSender = false;
    }


    private void setConfig() {
        this.setPrefWidth(320);
        this.setMinWidth(320);
        this.setMaxWidth(320);

        this.setSpacing(5);
        this.setAlignment(Pos.CENTER_LEFT);
        this.setPadding(new Insets(5));
        messageLabel.setWrapText(true);
        dateLabel.setFont(Font.font("Franklin Gothic", FontWeight.THIN, 8));
        dateLabel.setStyle("-fx-text-fill: #536572;");
    }

    private void setLocation() {
        HBox.setHgrow(messageLabel, Priority.ALWAYS);
        if (isCurrentUserSender) {
            messageContent.setAlignment(Pos.CENTER_RIGHT);
            this.setAlignment(Pos.CENTER_RIGHT);
            HBox.setMargin(circleClipProfile, new Insets(0, 0, 0, 5));
            HBox.setMargin(dateLabel, new Insets(0, 0, 0, 5));
            messageLabel.setStyle("-fx-background-color: #0e9ff1; -fx-text-fill: white; -fx-padding: 5 10 5 10; -fx-background-radius: 10; -fx-border-radius: 10;");
            this.getChildren().addAll(messageContent, circleClipProfile);
        } else {
            messageContent.setAlignment(Pos.CENTER_LEFT);
            this.setAlignment(Pos.CENTER_LEFT);
            HBox.setMargin(circleClipProfile, new Insets(0, 10, 0, 0));
            HBox.setMargin(dateLabel, new Insets(0, 10, 0, 0));
            messageLabel.setStyle("-fx-background-color: #e1e8ed; -fx-padding: 5 10 5 10; -fx-background-radius: 10; -fx-border-radius: 10;");
            this.getChildren().addAll(circleClipProfile, messageContent);
        }
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
}
