package controller;

import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import model.Client;
import model.entity.HttpClientResponse;
import model.entity.JsonHelper;
import model.entity.Profile;
import model.entity.Tweet;
import view.CompleteProfileComponent;
import view.ToolbarController;

import java.util.ArrayList;
import java.util.Map;

public class ProfileController {
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

    public void initialize() {
        profileContent = new VBox();
        profileContent.setFillWidth(true);
        scrollPane.setContent(profileContent);
        profileContent.setSpacing(10);

        toolbar = new ToolbarController();
        toolbar.goToProfilePage();

        setProfile();
        CompleteProfileComponent completeProfileComponent = new CompleteProfileComponent(profile, null);
        separator = new Separator();

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
        setConfigure();
        setLocation();
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
    }

    private void setLocation(){
        AnchorPane.setTopAnchor(toolbar, 30.0);
        AnchorPane.setLeftAnchor(toolbar, 0.0);
        AnchorPane.setBottomAnchor(toolbar, 0.0);
    }

    private void setProfile(){
        Map<String, String> params = Map.of("username", Client.getUsername());
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
