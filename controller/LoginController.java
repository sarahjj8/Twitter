package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.entity.User;

import java.io.IOException;

public class LoginController {

    @FXML
    private ImageView twitterImage;

    @FXML
    private Label SignInLabel;

    @FXML
    private Separator separator1;

    @FXML
    private Label usernameLabel;

    @FXML
    private TextField usernameTextField;

    @FXML
    private Label passLabel;

    @FXML
    private TextField passTextField;

    @FXML
    private Button SignInButton;

    @FXML
    private Label exceptionLabel;

    @FXML
    void signInMethod(ActionEvent event) {
//the main page of the twitter should be shown to the person who has logged in!
        String username = usernameTextField.getText();
        String password = passTextField.getText();
        //not sure whether this new user is necessary or not!
        //   User newUser = new User(username,password);
        String url = "http://localhost:8081/signin";
        String requestBody = "{\"username\": \"" + username + "\",\"password\": \"" + password + "\"}";
        model.entity.HttpClientResponse response = model.entity.HttpClientResponse.sendRequest(url, requestBody, null, "POST");
        if (response != null){
            if (response.getResponseCode() == 200){
                //setting the username of the user who has signed in:
                model.Client.setUsername(username);

                exceptionLabel.setText("welcome back to twitter!");
                Scene currentScene = ((Node) event.getSource()).getScene();
                Stage currentStage = (Stage) currentScene.getWindow();

                // Load the FXML file of the timeline page
                FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/fxml/timeline.fxml"));
                Parent timelinePageRoot;
                try {
                    timelinePageRoot = loader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }

                // Create a new scene
                Scene timelineScene = new Scene(timelinePageRoot,430,600);

                // Create a new window (stage) for the timeLine page
                Stage timelineStage = new Stage();
                timelineStage.setTitle("Twitter");
                timelineStage.setScene(timelineScene);
                timelineStage.setResizable(false);

                // Hide the window of the SignIn page
                currentStage.hide();

                // Show the window
                timelineStage.show();
            }
            else {
                //here we should write down the exception message inside the exception label
                exceptionLabel.setText(response.getResponse());
                exceptionLabel.setWrapText(true);
                exceptionLabel.setMaxWidth(200);
                exceptionLabel.setVisible(true);
            }
        }
    }
}
