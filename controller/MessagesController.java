package controller;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import model.Client;
import model.MessagesClient;
import model.entity.*;
import view.*;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

public class MessagesController {
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private ScrollPane conversationScrollPane;
    @FXML
    private VBox messagesContent;
    private Profile profile;
    private Profile withProfile;
    private ToolbarController toolbar;
    private ArrayList<Direct> conversations = new ArrayList<>();
    private ArrayList<Profile> profiles;
    private TextField sendMessageField;
    private static Image sendImage = new Image(String.valueOf(ToolbarController.class.getResource("icons/send_message.png")));
    private static ImageView sendImageView = new ImageView(sendImage);
    private static JFXButton sendButton;
    private static Image backImage = new Image(String.valueOf(ToolbarController.class.getResource("icons/back.png")));
    private static ImageView backImageView = new ImageView(backImage);
    private static JFXButton backButton;
    private static Image exploreImage = new Image(String.valueOf(ToolbarController.class.getResource("icons/explore.png")));
    private ImageView exploreImageView = new ImageView(exploreImage);
    private JFXButton exploreButton;
    private TextField searchField;
    private HBox searchBox;
    private static Label notFoundLabel;
    private HBox info;
    private ScrollPane menuScrollPane;
    private ContextMenu contextMenu;

    public void initialize(){
        sendButton = new JFXButton();
        backButton = new JFXButton();
        sendMessageField = new TextField();
        info = new HBox();

        profile = getProfile(Client.getUsername());

        messagesContent = new VBox(5);
        setHasDirectWith();

        toolbar = new ToolbarController();
        toolbar.goToMessagesPage();

        contextMenu = new ContextMenu();

        searchField = new TextField();
        exploreButton = new JFXButton();
        // Set the remaining space of searchBox to be occupied by searchField
        HBox.setHgrow(searchField, Priority.ALWAYS);
        searchBox = new HBox(exploreButton, searchField);
        VBox.setVgrow(searchBox, Priority.NEVER);

        conversationScrollPane.setContent(messagesContent);
        menuScrollPane = new ScrollPane();

        notFoundLabel = new Label("No results found ...");
        anchorPane.getChildren().addAll(sendMessageField, sendButton, searchBox, toolbar);

        searchField.setContextMenu(contextMenu);

        setConfigure();
        setLocation();
        setAction();
    }

    private void setConfigure(){
        sendMessageField.setPromptText("Start a new message");
        info.setAlignment(Pos.CENTER_LEFT);

        sendImageView.setFitWidth(20);
        sendImageView.setFitHeight(20);
        sendButton.setGraphic(sendImageView);
        sendButton.setGraphic(sendImageView);
        sendButton.setPrefSize(20,20);
        sendButton.setRipplerFill(Paint.valueOf("#858080"));
        sendButton.setStyle("-fx-background-radius: 50");

        backImageView.setFitWidth(15);
        backImageView.setFitHeight(15);
        backButton.setGraphic(backImageView);
        backButton.setGraphic(backImageView);
        backButton.setPrefSize(15,15);
        backButton.setRipplerFill(Paint.valueOf("#858080"));
        backButton.setStyle("-fx-background-radius: 50");

        sendMessageField.setPrefSize(260,30);
        sendMessageField.setMinSize(260,30);
        sendMessageField.setMaxSize(260,30);
        sendMessageField.setStyle("-fx-background-color:#f3f3f3;" +
                "-fx-background-radius:15;" +
                "-fx-border-color: rgba(243,243,243,0);" +
                "-fx-border-radius:15");

        sendMessageField.setVisible(false);
        sendButton.setVisible(false);

        searchField.setStyle("-fx-background-color:#eef2f3;" +
                "-fx-background-radius:15;" +
                "-fx-border-radius:15");
        searchField.setPrefSize(280,30);
        searchField.setMinSize(280,30);
        searchField.setMaxSize(280,30);

        searchBox.setStyle("-fx-background-color:#eef2f3;" +
                "-fx-background-radius:15;" +
                "-fx-border-radius:15");
        searchField.setPromptText("Search");

        exploreImageView.setFitWidth(20);
        exploreImageView.setFitHeight(20);
        exploreButton.setGraphic(exploreImageView);
        exploreButton.setPrefSize(20,20);
        exploreButton.setRipplerFill(Paint.valueOf("#858080"));
        exploreButton.setStyle("-fx-background-radius: 50");

        messagesContent.setStyle("-fx-background-color:#f3f3f3;" +
                "-fx-background-radius:10;" +
                "-fx-border-radius:10;" +
                "-fx-border-color:rgba(243,243,243,0)");

        anchorPane.setStyle("-fx-background-color:#ffffff;" +
                "-fx-background-radius:10;" +
                "-fx-border-radius:10;" +
                "-fx-border-color:rgba(243,243,243,0)");

        conversationScrollPane.setStyle("-fx-focus-color: transparent; " +
                "-fx-faint-focus-color: transparent;" +
                "-fx-background-color:#fdfdfe;" +
                "-fx-border-color: rgba(255,255,255,0)");

        info.setStyle("-fx-background-color:#f3f3f3;" +
                "-fx-border-color: rgba(243,243,243,0);");
    }

    private void setLocation(){
        AnchorPane.setBottomAnchor(sendMessageField, 5.0);
        AnchorPane.setRightAnchor(sendMessageField, 38.0);
        AnchorPane.setLeftAnchor(sendMessageField, 75.0);

        AnchorPane.setBottomAnchor(sendButton, 5.0);
        AnchorPane.setRightAnchor(sendButton, 5.0);

        AnchorPane.setTopAnchor(toolbar, 30.0);
        AnchorPane.setLeftAnchor(toolbar, 0.0);
        AnchorPane.setBottomAnchor(toolbar, 0.0);

        AnchorPane.setTopAnchor(searchBox, 30.0);
        AnchorPane.setLeftAnchor(searchBox, 75.0);
        AnchorPane.setRightAnchor(searchBox, 5.0);
    }

    private void setLocationForShowingMessages(){
        AnchorPane.setTopAnchor(info, 30.0);
        AnchorPane.setLeftAnchor(info, 75.0);
        AnchorPane.setRightAnchor(info, 0.);

        AnchorPane.setTopAnchor(conversationScrollPane, 90.0);
    }

    private void setAction(){
        sendButton.setOnMouseEntered(event -> {
            sendButton.setCursor(Cursor.HAND);
        });
        backButton.setOnMouseEntered(event -> {
            backButton.setCursor(Cursor.HAND);
        });
        sendButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                sendDirect();
            }
        });
        sendMessageField.setOnAction(event -> sendDirect());
        backButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                toolbar.goBackToMessages();
            }
        });
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

    private void sendDirect(){
        String text = sendMessageField.getText();
        Direct newDirect = new Direct(profile.getUsername(), withProfile.getUsername(), text, LocalDateTime.now());
        String url = "http://localhost:8081/direct";
        String requestBody = "{\"senderUsername\": \"" + profile.getUsername() + "\",\"receiverUsername\": \"" + withProfile.getUsername()
                + "\",\"text\": \"" + text + "\"}";
        model.entity.HttpClientResponse response = model.entity.HttpClientResponse.sendRequest(url, requestBody, null, "POST");
        if (response != null) {
            if (response.getResponseCode() == 201) {
                addDirect(newDirect);
                sendMessageField.clear();
            } else {
                Message.showTemporaryMessage(conversationScrollPane, response.getResponse());
            }
        } else {
            Message.showTemporaryMessage(conversationScrollPane, "Oops! Something went wrong.");
        }
    }
//
//    public void startMessaging(String recipient, String sender) {
//        MessagesClient messagesClient = new MessagesClient(recipient, sender);
//        Thread clientThread = new Thread(messagesClient::startClient);
//        clientThread.start();
//
//        try {
//            // Wait for the client thread to finish (e.g., when the socket is closed)
//            clientThread.join();
//        } catch (InterruptedException e) {
//            System.out.println("Thread interrupted: " + e.getMessage());
//            Thread.currentThread().interrupt();
//        }
//    }

    public void addDirect(Direct direct) {
        conversations.add(direct);

        // If the conversation is currently displayed, add the new Direct to the conversationVBox
        if (conversationScrollPane.isVisible()) {
            DirectMessageComponent directComponent;
            boolean isCurrentUserSender = isCurrentUserSender(direct);
            if(isCurrentUserSender)
                directComponent = new DirectMessageComponent(profile, direct, isCurrentUserSender);
            else
                directComponent = new DirectMessageComponent(withProfile, direct, isCurrentUserSender);
            messagesContent.getChildren().add(directComponent);
        }
    }


    public void showConversation() {
        sendMessageField.setVisible(true);
        sendButton.setVisible(true);

        searchBox.setVisible(false);
        searchBox.setVisible(false);

        AnchorPane.setTopAnchor(conversationScrollPane, 30.0);

        messagesContent.getChildren().clear();
        ProfileComponent profileComponent = new ProfileComponent(withProfile);
        profileComponent.setScaleY(0.9);
        profileComponent.setScaleX(0.9);
        profileComponent.setStyle("-fx-background-color: #f3f3f3");
        profileComponent.setEffect(null);
        info.getChildren().addAll(backButton, profileComponent);
        anchorPane.getChildren().add(0, info);
        setLocationForShowingMessages();

        DirectMessageComponent directComponent;
        for (Direct direct : conversations) {
            boolean isCurrentUserSender = isCurrentUserSender(direct);
            if(isCurrentUserSender)
                directComponent = new DirectMessageComponent(profile, direct, isCurrentUserSender);
            else
                directComponent = new DirectMessageComponent(withProfile, direct, isCurrentUserSender);
            messagesContent.getChildren().add(directComponent);
        }
        conversationScrollPane.setContent(messagesContent);
        listenForNewMessages(profile.getUsername());
    }

    private void listenForNewMessages(String username){
        try {
            Socket socket = new Socket("127.0.0.1", 6000);
            MessagesClient client = new MessagesClient(username, socket, this);
            client.listenForMessages();
        } catch (IOException e) {
            System.out.println("IO exception occurred in client: " + e.getMessage());
        }
    }

    private void setConversations(String toUsername){
        Map<String, String> params = Map.of("username", Client.getUsername(), "to username", toUsername);
        HttpClientResponse response = HttpClientResponse.sendRequest(HttpClientResponse.createURL("directs"), null, params, "GET");
        if (response.getResponseCode() == 200 && !response.getResponse().equals("No Directs found ..."))
            conversations = JsonHelper.parseJsonToListWithAdapter(response.getResponse(), model.entity.Direct.class);
        else if(response.getResponseCode() != 200) {
            Message.showTemporaryMessage(messagesContent, response.getResponse());
            conversations = new ArrayList<>();
        } else
            conversations = new ArrayList<>();
    }

    private void setHasDirectWith(){
        ArrayList<Profile> profiles;
        Map<String, String> params = Map.of("username", Client.getUsername());
        HttpClientResponse response = HttpClientResponse.sendRequest(HttpClientResponse.createURL("hasDirectWith"), null, params, "GET");
        if (response.getResponseCode() == 200 && !response.getResponse().equals("No Directs found ..."))
            profiles = JsonHelper.parseJsonToListWithAdapter(response.getResponse(), model.entity.Profile.class);
        else if(response.getResponseCode() != 200) {
            Message.showTemporaryMessage(messagesContent, response.getResponse());
            profiles = new ArrayList<>();
        } else
            profiles = new ArrayList<>();

        messagesContent.getChildren().clear();
        for(Profile profile1 : profiles){
            ProfileComponentForDirect profileComponentForDirect = new ProfileComponentForDirect(profile1);
            messagesContent.getChildren().add(profileComponentForDirect);
        }
        setActionsForComponents();
    }

    private void setActionsForComponents(){
        for(Node node : messagesContent.getChildren()){
            if(node instanceof ProfileComponentForDirect){
                ProfileComponentForDirect profileComponentForDirect = (ProfileComponentForDirect) node;
                profileComponentForDirect.setOnMouseClicked(event -> {
                    withProfile = getProfile(profileComponentForDirect.getUserId().substring(1));
                    setConversations(profileComponentForDirect.getUserId().substring(1));
                    showConversation();
                });
            }
        }
    }

    private Profile getProfile(String username){
        Map<String, String> params = Map.of("username", username);
        HttpClientResponse response = HttpClientResponse.sendRequest(HttpClientResponse.createURL("profile"), null, params, "GET");
        ArrayList<Profile> profiles = JsonHelper.parseJsonToListWithAdapter(response.getResponse(), model.entity.Profile.class);
        if (!profiles.isEmpty())
            return profiles.get(0);
        return null;
    }

    private boolean isCurrentUserSender(Direct direct){
        if(direct.getSenderUsername().equals(Client.getUsername()))
            return true;
        else
            return false;
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

    @FXML
    private void search() {
        contextMenu.getItems().clear();
        String searchText = searchField.getText();
        setProfiles(searchText);
        if(profiles.isEmpty()){
            notFoundLabel.setMaxWidth(150);
            MenuItem item = new MenuItem("", notFoundLabel);
            item.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> item.setStyle("-fx-background-color: #e6e6e6;"));
            item.addEventHandler(MouseEvent.MOUSE_EXITED, e -> item.setStyle("-fx-background-color: transparent;"));
            contextMenu.getItems().add(item);
        }
        else {
            for (Profile profile : profiles) {
                ProfileComponentForDirect profileComponentForDirect1 = new ProfileComponentForDirect(profile);
                profileComponentForDirect1.setScaleX(0.8);
                profileComponentForDirect1.setScaleY(0.8);
                profileComponentForDirect1.setMaxSize(200,30);
                MenuItem item = new MenuItem("", profileComponentForDirect1);
                item.setOnAction(event -> {
                    withProfile = getProfile(profileComponentForDirect1.getUserId().substring(1));
                    setConversations(profileComponentForDirect1.getUserId().substring(1));
                    showConversation();
                });
                item.getGraphic().setStyle("-fx-padding: 0 0 0 -33px;");
                item.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> item.setStyle("-fx-background-color: #e6e6e6;"));
                item.addEventHandler(MouseEvent.MOUSE_EXITED, e -> item.setStyle("-fx-background-color: transparent;"));
                contextMenu.getItems().add(item);
            }
        }
        contextMenu.getScene().getRoot().setStyle("-fx-max-height: 50px;");
        contextMenu.show(searchField, Side.BOTTOM, -33, 0);
    }
}
