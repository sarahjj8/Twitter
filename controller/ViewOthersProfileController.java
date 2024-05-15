package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import model.Client;
import model.entity.HttpClientResponse;
import model.entity.JsonHelper;
import model.entity.Profile;
import model.entity.Tweet;
import view.CompleteProfileComponent;
import view.Message;
import view.ToolbarController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class ViewOthersProfileController {
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox profileContent;
    private ToolbarController toolbar;
    private Separator separator;
    private Profile profile;
    private ArrayList<Tweet> tweets;
    private CompleteProfileComponent completeProfileComponent;
    private Label blockedLabel;

    public void initialize() {
        profileContent = new VBox();
        profileContent.setFillWidth(true);
        scrollPane.setContent(profileContent);
        profileContent.setSpacing(10);
        profileContent.setStyle("-fx-background-color:#ffffff;" +
                "-fx-padding: 8;");

        separator = new Separator();
        toolbar = new ToolbarController();
        toolbar.goToProfilePage();

        completeProfileComponent = new CompleteProfileComponent(profile, this);

        blockedLabel = new Label("@" + profile.getUsername() + " blocked\nYou can unblock by clicking here");

        profileContent.getChildren().addAll(completeProfileComponent, separator);
        separator.setPrefSize(300.0,1.0);
        separator.setStyle("-fx-background-color:#3d3d3d;");
        separator.setOpacity(0.5);

        setTweets();
        HomeController.addTweetsToVbox(tweets, profileContent);

        VBox.setVgrow(profileContent, Priority.ALWAYS);
        scrollPane.setContent(profileContent);
        VBox.setVgrow(profileContent, Priority.ALWAYS);

        anchorPane.getChildren().add(toolbar);

        setLocation();
        setConfigure();
        setActions();
    }

    public void removeTweets() {
        Iterator<Node> iterator = profileContent.getChildren().iterator();
        while (iterator.hasNext()) {
            Node node = iterator.next();
            if (!(node instanceof Separator || node instanceof CompleteProfileComponent)) {
                iterator.remove();
            }
        }
        blockedLabel.setVisible(true);
        profileContent.getChildren().add(blockedLabel);
    }

    private void setActions(){
        blockedLabel.setOnMouseEntered(event -> {
            blockedLabel.setCursor(Cursor.HAND);
        });
        blockedLabel.setOnMouseClicked(event ->{
            unblock();
        });
    }

    private void unblock(){
        blockedLabel.setVisible(false);
        String url = "http://localhost:8081/unblock";
        Map<String, String> deleteParams = Map.of("username", Client.getUsername(), "blocked username", profile.getUsername());
        model.entity.HttpClientResponse response = model.entity.HttpClientResponse.sendRequest(url, null, deleteParams, "DELETE");
        if (response != null) {
            if (response.getResponseCode() == 200) {
                try {
                    ViewOthersProfileController viewOthersProfileController = new ViewOthersProfileController();
                    viewOthersProfileController.setProfile(profile.getUsername());
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/fxml/viewOthersProfile.fxml"));
                    loader.setController(viewOthersProfileController);
                    Parent profilePage = loader.load();
                    Scene profileScene = new Scene(profilePage);
                    Stage primaryStage = (Stage) blockedLabel.getScene().getWindow();
                    primaryStage.setScene(profileScene);
                    primaryStage.show();
                } catch (IOException e) {
                    Message.showTemporaryMessage(blockedLabel, "an error occurred while loading " + profile.getUsername() + " profile");
                    e.printStackTrace();
                }
            } else {
                Message.showTemporaryMessage(blockedLabel, response.getResponse());
            }
        } else {
            Message.showTemporaryMessage(blockedLabel, "Oops! Something went wrong.");
        }
    }

    private void setLocation(){
        AnchorPane.setTopAnchor(toolbar, 30.0);
        AnchorPane.setLeftAnchor(toolbar, 0.0);
        AnchorPane.setBottomAnchor(toolbar, 0.0);
    }

    private void setConfigure(){
        profileContent.setStyle("-fx-background-color:#ffffff;" +
                "-fx-padding: 8;");

        anchorPane.setStyle("-fx-background-color:#ffffff;" +
                "-fx-background-radius:10;" +
                "-fx-border-radius:10;" +
                "-fx-border-color:rgba(243,243,243,0)");

        scrollPane.setStyle("-fx-focus-color: transparent; " +
                "-fx-faint-focus-color: transparent;" +
                "-fx-background-color:#f3f3f3;" +
                "-fx-border-color: rgba(255,255,255,0)");

        blockedLabel.setVisible(false);
        blockedLabel.setAlignment(Pos.CENTER);
    }

    public void setProfile(String username){
        Map<String, String> params = Map.of("username", username);
        HttpClientResponse response = HttpClientResponse.sendRequest(HttpClientResponse.createURL("profile"), null, params, "GET");
        ArrayList<Profile> profiles = JsonHelper.parseJsonToListWithAdapter(response.getResponse(), model.entity.Profile.class);
        if (!profiles.isEmpty())
            profile = profiles.get(0);
    }

    private void setTweets(){
        Map<String, String> params = Map.of("username", profile.getUsername());
        HttpClientResponse response = HttpClientResponse.sendRequest(HttpClientResponse.createURL("profileTweets"), null, params, "GET");
        if(response.getResponseCode() == 200 && response.getResponse().equals("No tweets ...")){
            tweets = new ArrayList<>();
        } else if (response.getResponseCode() == 200) {
            tweets = JsonHelper.parseJsonToTweetListWithAdapter(response.getResponse());
        }
    }
}


