package view;

import com.jfoenix.controls.JFXButton;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Client;
import model.entity.Profile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ProfileEditor extends Stage {
    private static final String destinationAvatarFolderPath = "media/avatar";
    private static final String destinationHeaderFolderPath = "media/header";
    private Profile profile;
    private Image avatarImage = new Image(String.valueOf(TweetComponent.class.getResource("icons/profile_image.png")));
    private Image headerImage = new Image(String.valueOf(TweetComponent.class.getResource("icons/blank_header.png")));
    private ImageView avatarImageView = new ImageView(avatarImage);
    private ImageView headerImageView = new ImageView(headerImage);
    private CompleteProfileComponent completeProfileComponent;
    private Circle avatarCircleClipProfile;
    private Rectangle headerRectangle;
    private StackPane headerPane;
    private TextField websiteTextField;
    private TextArea bioTextArea;

    private TextField locationTextField;
    private JFXButton saveButton;

    public ProfileEditor(Profile profile, CompleteProfileComponent completeProfileComponent) {
        this.profile = profile;
        this.completeProfileComponent = completeProfileComponent;
        avatarCircleClipProfile = new Circle(35);
        avatarCircleClipProfile.setStroke(javafx.scene.paint.Color.GRAY);
        avatarCircleClipProfile.setFill(Color.SNOW);
        avatarCircleClipProfile.setFill(new ImagePattern(avatarImage));

        headerRectangle = new Rectangle();
        headerImageView.setFitHeight(200);
        headerImageView.setPreserveRatio(true);
        headerRectangle.setArcHeight(10.0);
        headerRectangle.setArcWidth(10.0);
        headerImageView.fitWidthProperty().bind(this.widthProperty());
        headerImageView.setClip(null);

        updateAvatarImage(profile.getAvatarPath());
        updateHeaderImage(profile.getHeaderPath());

        headerPane = new StackPane();
        headerPane.getChildren().addAll(headerRectangle, headerImageView);
        initialize();
    }


    private void initialize() {
        VBox vbox = new VBox();
        vbox.setPrefWidth(320);
        vbox.setMinWidth(320);
        vbox.setMaxWidth(320);

        vbox.setStyle("-fx-background-color:#ffffff;" +
                "-fx-background-radius:10;" +
                "-fx-border-radius:10;" +
                "-fx-border-color:#ffffff");

        AnchorPane anchorPane = new AnchorPane();
        anchorPane.getChildren().addAll(avatarCircleClipProfile, headerImageView);
        anchorPane.setStyle("-fx-background-color:#ffffff;" +
                "-fx-background-radius:10;" +
                "-fx-border-radius:10;" +
                "-fx-border-color:#ffffff");

        initModality(Modality.APPLICATION_MODAL);
        setTitle("Edit Profile");

        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10));
        gridPane.setStyle("-fx-background-color:rgba(255,255,255,0);" +
                "-fx-background-radius:10;" +
                "-fx-border-radius:10;" +
                "-fx-border-color:#ffffff");

        Label bioLabel = new Label("Bio:");
        bioTextArea = new TextArea(completeProfileComponent.getProfile().getBio());
        gridPane.addRow(0, bioLabel, bioTextArea);

        bioTextArea.setStyle("-fx-border-color: transparent;" +
                "-fx-background-radius:10;" +
                "-fx-border-radius:10;" +
                "-fx-border-color:#c0c0c0");
        bioTextArea.setWrapText(true);
        bioTextArea.setTextFormatter(new TextFormatter<String>(change -> {
            String newText = change.getControlNewText();
            if (newText.length() > 160) {
                return null;
            } else {
                return change;
            }
        }));

        Label websiteLabel = new Label("Website:");
        websiteTextField = new TextField(completeProfileComponent.getProfile().getWebsiteAddress());
        gridPane.addRow(1, websiteLabel, websiteTextField);

        websiteTextField.setStyle("-fx-background-radius:10;" +
                "-fx-border-radius:10;" +
                "-fx-border-color:#c0c0c0");

        Label locationLabel = new Label("Location:");
        locationTextField = new TextField(completeProfileComponent.getProfile().getLocation());
        gridPane.addRow(2, locationLabel, locationTextField);

        locationTextField.setStyle("-fx-background-radius:10;" +
                "-fx-border-radius:10;" +
                "-fx-border-color:#c0c0c0");

        ColumnConstraints col1Constraints = new ColumnConstraints();
        // Set the width as a percentage
        col1Constraints.setPercentWidth(20);
        ColumnConstraints col2Constraints = new ColumnConstraints();
        col2Constraints.setPercentWidth(80);
        gridPane.getColumnConstraints().addAll(col1Constraints, col2Constraints);

        GridPane.setHalignment(websiteLabel, HPos.CENTER);
        GridPane.setHalignment(locationLabel, HPos.CENTER);
        GridPane.setHalignment(bioLabel, HPos.CENTER);

        saveButton = new JFXButton("Save");
        saveButton.setOnAction(event -> saveChanges());
        saveButton.setStyle("-fx-background-color:#0e9ff1;" +
                "-fx-background-radius: 20;" +
                "-fx-border-radius:50;" +
                "-fx-text-fill: #ffffff;");
        vbox.getChildren().addAll(anchorPane, gridPane, saveButton);
        vbox.setAlignment(Pos.CENTER);

        Scene scene = new Scene(vbox);
        setScene(scene);
        setLocation();
        setActions();
    }

    private void setLocation() {
        AnchorPane.setTopAnchor(headerImageView, 0.0);
        AnchorPane.setLeftAnchor(headerImageView, 0.0);
        double avatarY = headerImageView.getFitHeight() - 130;
        AnchorPane.setLeftAnchor(avatarCircleClipProfile, 10.0);
        AnchorPane.setTopAnchor(avatarCircleClipProfile, avatarY);
        AnchorPane.setTopAnchor(headerPane, 0.0);
        AnchorPane.setLeftAnchor(headerPane, 0.0);
        AnchorPane.setRightAnchor(headerPane, 0.0);
        avatarCircleClipProfile.toFront();
    }

    private void setActions() {
        avatarCircleClipProfile.setOnMouseEntered(event -> {
            avatarCircleClipProfile.setCursor(Cursor.HAND);
        });

        avatarCircleClipProfile.setOnMouseClicked(event -> {
            openAvatarImageChooser(avatarImageView);
        });

        headerImageView.setOnMouseEntered(event -> {
            headerImageView.setCursor(Cursor.HAND);
        });

        headerImageView.setOnMouseClicked(event -> {
            openHeaderImageChooser(headerImageView);
        });

        saveButton.setOnMouseClicked(event -> {
            saveChanges();
        });

        saveButton.setOnMouseEntered(event -> {
            saveButton.setCursor(Cursor.HAND);
        });
    }

    private void saveChanges(){
        String website = websiteTextField.getText();
        updateWebsite(website);
        String bio = bioTextArea.getText();
        updateBio(bio);
        String location = locationTextField.getText();
        updateLocation(location);
        String imagePath = null;

        try {
            imagePath = Save.saveImageToFile(avatarImage, profile.getUsername(), "avatar");
            updateAvatar(imagePath);
        } catch (IOException e) {
            Message.showTemporaryMessageInVBox(saveButton, "an error occurred while saving the avatar image");
        }

        try {
            imagePath = Save.saveImageToFile(headerImage, profile.getUsername(), "header");
            updateHeader(imagePath);
        } catch (IOException e) {
            Message.showTemporaryMessageInVBox(saveButton, "an error occurred while saving the header image");
        }
        completeProfileComponent.update(headerImage,avatarImage);
        close();
    }

    private void openHeaderImageChooser(ImageView imageView) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Image File");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            updateHeaderImageView(imageView, selectedFile);
        }
    }

    private void openAvatarImageChooser(ImageView imageView) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Image File");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            updateAvatarImageView(imageView, selectedFile);
        }
    }

    private void updateAvatarImageView(ImageView imageView, File file) {
        try {
            Image image = new Image(file.toURI().toString());
            Image resizedImage = Save.resizeImage(image, 400, 400);
            imageView.setImage(resizedImage);
            avatarImage = resizedImage;
            avatarCircleClipProfile.setFill(new ImagePattern(resizedImage));
        } catch (Exception e) {
            Message.showTemporaryMessageInVBox(saveButton, "an error occurred while loading the avatar image");
        }
    }

    private void updateHeaderImageView(ImageView imageView, File file) {
        try {
            Image image = new Image(file.toURI().toString());
            Image resizedImage = Save.resizeImage(image, 1500, 500);
            headerImage = resizedImage;
            imageView.setImage(resizedImage);
        } catch (Exception e) {
            Message.showTemporaryMessageInVBox(saveButton, "an error occurred while loading the header image");
        }
    }

    private void updateBio(String newBio){
        if((profile.getBio() != null && !profile.getBio().equals(newBio)) || (profile.getBio() == null && newBio != null)){
            String patchUrl = "http://localhost:8081/bio";
            String patchRequestBody = "{ \"username\": \"" + Client.getUsername() + "\", \"bio\": \"" + newBio + "\" }";
            model.entity.HttpClientResponse response;
            try {
                response = model.entity.HttpClientResponse.sendPatchRequest(patchUrl, patchRequestBody);
                if(response.getResponseCode() != 200){
                    Message.showTemporaryMessageInVBox(saveButton, response.getResponse());
                }
                if (response != null) {
                    profile.setBio(newBio);
                }
            } catch (IOException e) {
                Message.showTemporaryMessageInVBox(saveButton, "an error occurred while saving the bio");
            }
        }
    }

    private void updateWebsite(String newWebsite){
        if((profile.getWebsiteAddress() != null && !profile.getWebsiteAddress().equals(newWebsite)) ||
                (profile.getWebsiteAddress() == null && newWebsite != null)){
            String patchUrl = "http://localhost:8081/websiteAddress";
            String patchRequestBody = "{ \"username\": \"" + Client.getUsername() + "\", \"website address\": \"" + newWebsite + "\" }";
            model.entity.HttpClientResponse response;
            try {
                response = model.entity.HttpClientResponse.sendPatchRequest(patchUrl, patchRequestBody);
                if(response.getResponseCode() != 200){
                    Message.showTemporaryMessageInVBox(saveButton, response.getResponse());
                }
                if (response != null) {
                    profile.setWebsiteAddress(newWebsite);
                    websiteTextField.setText(newWebsite);
                }
            } catch (IOException e) {
                Message.showTemporaryMessageInVBox(saveButton, "an error occurred while saving the website address");
            }
        }
    }

    private void updateLocation(String newLocation){
        if((profile.getLocation() != null && !profile.getLocation().equals(newLocation)) || (profile.getLocation() == null && newLocation != null)){
            String patchUrl = "http://localhost:8081/location";
            String patchRequestBody = "{ \"username\": \"" + Client.getUsername() + "\", \"location\": \"" + newLocation + "\" }";
            model.entity.HttpClientResponse response;
            try {
                response = model.entity.HttpClientResponse.sendPatchRequest(patchUrl, patchRequestBody);
                if(response.getResponseCode() != 200){
                    Message.showTemporaryMessageInVBox(saveButton, response.getResponse());
                }
                if (response != null) {
                    profile.setLocation(newLocation);
                    locationTextField.setText(newLocation);
                }
            } catch (IOException e) {
                Message.showTemporaryMessageInVBox(saveButton, "an error occurred while saving the location");
            }
        }
    }

    private void updateAvatar(String avatarPath){
        String patchUrl = "http://localhost:8081/avatar";
        String patchRequestBody = "{ \"username\": \"" + Client.getUsername() + "\", \"avatar\": \"" + avatarPath + "\" }";
        model.entity.HttpClientResponse response;
        try {
            response = model.entity.HttpClientResponse.sendPatchRequest(patchUrl, patchRequestBody);
            if(response.getResponseCode() != 200){
                Message.showTemporaryMessageInVBox(saveButton, response.getResponse());
            }
            if (response != null) {
                profile.setAvatarPath(avatarPath);
            }
        } catch (IOException e) {
            Message.showTemporaryMessageInVBox(saveButton, "an error occurred while saving the avatar");
        }
    }

    private void updateHeader(String headerPath){
        String patchUrl = "http://localhost:8081/header";
        String patchRequestBody = "{ \"username\": \"" + Client.getUsername() + "\", \"header\": \"" + headerPath + "\" }";
        model.entity.HttpClientResponse response;
        try {
            response = model.entity.HttpClientResponse.sendPatchRequest(patchUrl, patchRequestBody);
            if(response.getResponseCode() != 200){
                Message.showTemporaryMessageInVBox(saveButton, response.getResponse());
            }
            if (response != null) {
                profile.setHeaderPath(headerPath);
            }
        } catch (IOException e) {
            Message.showTemporaryMessageInVBox(saveButton, "an error occurred while saving the header");
        }
    }

    public void updateAvatarImage(String imagePath) {
        if (imagePath != null) {
            File file = new File(imagePath);
            if (file.exists()) {
                Image image = new Image(file.toURI().toString());
                if (image != null) {
                    avatarImageView.setImage(image);
                    avatarCircleClipProfile.setFill(new ImagePattern(image));
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

                    newHeaderImageView.setFitHeight(200);
                    newHeaderImageView.setPreserveRatio(true);
                    newHeaderImageView.fitWidthProperty().bind(this.widthProperty());
                    newHeaderImageView.setClip(null);


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