package controller;

import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import model.Client;
import model.entity.HttpClientResponse;
import model.entity.JsonHelper;
import model.entity.Profile;
import view.PostTweetComponent;
import view.ToolbarController;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PostTweetController {
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox tweetContent;
    private Profile profile;
    private ToolbarController toolbar;

    public void initialize() {
        tweetContent = new VBox();
        tweetContent.setFillWidth(true); // Allow the VBox to fill the available width
        scrollPane.setContent(tweetContent);
        tweetContent.setSpacing(10);
        tweetContent.setStyle("-fx-background-color:#ffffff;" +
                "-fx-padding: 8;");

        toolbar = new ToolbarController();
        toolbar.goToTweetPage();
        setProfile();
        PostTweetComponent postTweetComponent = new PostTweetComponent(profile, null, "default", null, null);
        tweetContent.getChildren().add(postTweetComponent);

        VBox.setVgrow(tweetContent, Priority.ALWAYS);
        scrollPane.setContent(tweetContent);
        VBox.setVgrow(tweetContent, Priority.ALWAYS);

        anchorPane.getChildren().addAll(toolbar);
        setConfigure();
        setLocation();
    }

    private void setConfigure(){
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
}
