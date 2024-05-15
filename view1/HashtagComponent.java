package view;

import controller.HashtagController;
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
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import model.entity.Hashtag;
import model.entity.Profile;
import view.TweetComponent;

import java.io.IOException;

public class HashtagComponent extends AnchorPane {
    private Hashtag hashtag;
    private Label hashtagNameLabel;
    private Image hashtagImage = new Image(String.valueOf(TweetComponent.class.getResource("icons/black_explore.png")));
    private ImageView hashtagImageView = new ImageView(hashtagImage);
    private HBox hashtagBox;

    public HashtagComponent(Hashtag hashtag) {
        this.hashtag = hashtag;
        hashtagNameLabel = new Label(hashtag.getHashtagName());

        hashtagBox = new HBox(5);
        hashtagBox.getChildren().addAll(hashtagImageView, hashtagNameLabel);

        this.getChildren().add(hashtagBox);

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
        hashtagImageView.setPreserveRatio(true);
        hashtagImageView.setFitHeight(25);
        hashtagNameLabel.setFont(Font.font("Franklin Gothic", FontWeight.BLACK,15));
        hashtagNameLabel.setStyle("-fx-text-fill:#0f1419;");
    }

    private void setLocation() {
        AnchorPane.setTopAnchor(hashtagBox,23.0);
        AnchorPane.setLeftAnchor(hashtagBox,20.0);
    }

    private void setActions() {
        this.setOnMouseClicked(event1 -> {
            try {
                HashtagController hashtagController = new HashtagController(hashtag);
                FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/hashtag.fxml"));
                loader.setController(hashtagController);
                Parent profilePage = loader.load();
                Scene profileScene = new Scene(profilePage);
                Stage primaryStage = (Stage) hashtagNameLabel.getScene().getWindow();
                primaryStage.setScene(profileScene);
                primaryStage.show();
            } catch (IOException e) {
                Message.showTemporaryMessage(hashtagNameLabel, "an error occurred while loading " + hashtagNameLabel.textProperty() + " hashtag");
                e.printStackTrace();
            }
        });
        hashtagNameLabel.setOnMouseEntered(event -> {
        hashtagNameLabel.setCursor(Cursor.HAND);
        });
        DropShadow shadow = new DropShadow();
        shadow.setRadius(3);
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
