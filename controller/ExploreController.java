package controller;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import model.Client;
import model.entity.Hashtag;
import model.entity.HttpClientResponse;
import model.entity.JsonHelper;
import model.entity.Profile;
import view.*;

import java.util.ArrayList;
import java.util.Map;

public class ExploreController {
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private ScrollPane scrollPane;
    private TextField searchField;
    @FXML
    private VBox exploreContent;
    private ToolbarController toolbar;
    private HBox searchBox;
    private static Label notFoundLabel;

    private static Image exploreImage = new Image(String.valueOf(ToolbarController.class.getResource("icons/explore.png")));
    private ImageView exploreImageView = new ImageView(exploreImage);
    private JFXButton exploreButton;
    private ArrayList<Profile> profiles;
    private ArrayList<Hashtag> hashtags;

    public void initialize() {
        exploreButton = new JFXButton();
        exploreContent = new VBox();
        exploreContent.setFillWidth(true); // Allow the VBox to fill the available width
        exploreContent.setSpacing(10);
        exploreContent.setStyle("-fx-background-color:#ffffff;" +
                "-fx-padding: 8;");

        searchField = new TextField();
        // Set the remaining space of searchBox to be occupied by searchField
        HBox.setHgrow(searchField, Priority.ALWAYS);
        searchField.setStyle("-fx-background-color:#eef2f3;" +
                "-fx-background-radius:15;" +
                "-fx-border-radius:15");
        searchField.setPrefSize(290,30);
        searchField.setMinSize(290,30);
        searchField.setMaxSize(290,30);
        searchBox = new HBox(exploreButton, searchField);
        searchBox.setStyle("-fx-background-color:#eef2f3;" +
                "-fx-background-radius:15;" +
                "-fx-border-radius:15");
        searchField.setPromptText("Search");

        VBox.setVgrow(searchBox, Priority.NEVER);

        toolbar = new ToolbarController();
        toolbar.goToExplorePage();

        VBox.setVgrow(exploreContent, Priority.ALWAYS); // Set exploreContent to fill available space
        scrollPane.setContent(exploreContent);
        VBox.setVgrow(exploreContent, Priority.ALWAYS); // Set mainLayout to fill available space vertically

        notFoundLabel = new Label("No results found ...");
        anchorPane.getChildren().addAll(searchBox, toolbar);
        setConfigure();
        setLocation();
        setActions();
    }

    private void setProfiles(String searchText){
        Map<String, String> params = Map.of("string to search for", searchText, "username", Client.getUsername());
        HttpClientResponse response = HttpClientResponse.sendRequest(HttpClientResponse.createURL("profiles"), null, params, "GET");
        if(response.getResponseCode() == 200 && !response.getResponse().equals("No profiles found ..."))
            profiles = JsonHelper.parseJsonToListWithAdapter(response.getResponse(), model.entity.Profile.class);
        else if (response.getResponseCode() == 200 && response.getResponse().equals("No profiles found ...")) {
            profiles = new ArrayList<>();
        } else {
            profiles = new ArrayList<>();
            Message.showTemporaryMessage(searchBox, response.getResponse());
        }
    }

    private void setHashtags(String searchText){
        Map<String, String> params = Map.of("string to search for", searchText);
        HttpClientResponse response = HttpClientResponse.sendRequest(HttpClientResponse.createURL("searchedHashtags"), null, params, "GET");
        if(response.getResponseCode() == 200 && !response.getResponse().equals("No hashtags found ..."))
            hashtags = JsonHelper.parseJsonToListWithAdapter(response.getResponse(), model.entity.Hashtag.class);
        else if (response.getResponseCode() == 200 && response.getResponse().equals("No hashtags found ...")) {
            hashtags = new ArrayList<>();
        } else {
            hashtags = new ArrayList<>();
            Message.showTemporaryMessage(searchBox, response.getResponse());
        }
    }

    private void setLocation(){
        AnchorPane.setTopAnchor(toolbar, 30.0);
        AnchorPane.setLeftAnchor(toolbar, 0.0);
        AnchorPane.setBottomAnchor(toolbar, 0.0);

        AnchorPane.setTopAnchor(searchBox, 30.0);
        AnchorPane.setLeftAnchor(searchBox, 75.0);
        AnchorPane.setRightAnchor(searchBox, 5.0);
    }

    private void setConfigure(){
        exploreImageView.setFitWidth(20);
        exploreImageView.setFitHeight(20);
        exploreButton.setGraphic(exploreImageView);
        exploreButton.setPrefSize(20,20);
        exploreButton.setRipplerFill(Paint.valueOf("#858080"));
        exploreButton.setStyle("-fx-background-radius: 50");

        anchorPane.setStyle("-fx-background-color:#ffffff;" +
                "-fx-background-radius:10;" +
                "-fx-border-radius:10;" +
                "-fx-border-color:rgba(243,243,243,0)");

        scrollPane.setStyle("-fx-focus-color: transparent; " +
                "-fx-faint-focus-color: transparent;" +
                "-fx-background-color:#f3f3f3;" +
                "-fx-border-color: rgba(255,255,255,0)");
    }

    private void setActions()
    {
        exploreButton.setOnMouseEntered(event -> {
            exploreButton.setCursor(Cursor.HAND);
        });
        exploreButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                search();
            }
        });
        searchField.setOnAction(event -> search());
    }

    @FXML
    private void search() {
        exploreContent.getChildren().clear();
        String searchText = searchField.getText();
        setProfiles(searchText);
        setHashtags(searchText);
        if(profiles.isEmpty() && hashtags.isEmpty()){
            exploreContent.getChildren().add(notFoundLabel);
        }
        else {
            for (Profile profile : profiles) {
                ProfileComponent profileComponent = new ProfileComponent(profile);
                exploreContent.getChildren().add(profileComponent);
            }
            for(Hashtag hashtag : hashtags){
                HashtagComponent hashtagComponent = new HashtagComponent(hashtag);
                exploreContent.getChildren().add(hashtagComponent);
            }
        }
    }
}

