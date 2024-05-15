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
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class FirstPageController {

    @FXML
    private ImageView twitterImage;

    @FXML
    private Label SignUpText;

    @FXML
    private Button SignUpButton;

    @FXML
    private Separator separator1;

    @FXML
    private Label loginRecomText;

    @FXML
    private Button LoginButton;

    @FXML
    private TextField OrText;

    @FXML
    private Separator separator2;

    @FXML
    void showLoginPage(ActionEvent event) {
// Get the current scene and its window
        Scene currentScene = ((Node) event.getSource()).getScene();
        Stage currentStage = (Stage) currentScene.getWindow();

        // Load the FXML file of the sign-up page
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/fxml/Login.fxml"));
        Parent signInPageRoot;
        try {
            signInPageRoot = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // Create a new scene using the sign-up page root
        Scene signInPageScene = new Scene(signInPageRoot,500,600);

        // Create a new window (stage) for the sign-in page
        Stage signInStage = new Stage();
        signInStage.setScene(signInPageScene);
        signInStage.setResizable(false);
        signInStage.setTitle("Let's Login");

        // Hide the window of the first page
        currentStage.hide();

        // Show the window of the sign-in page
        signInStage.show();
    }

    @FXML
    public void showSignUpPage(ActionEvent event) {
        // Get the current scene and its window
        Scene currentScene = ((Node) event.getSource()).getScene();
        Stage currentStage = (Stage) currentScene.getWindow();

        // Load the FXML file of the sign-up page
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/fxml/SignUp.fxml"));
        Parent signUpPageRoot;
        try {
            signUpPageRoot = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // Create a new scene using the sign-up page root
        Scene signUpPageScene = new Scene(signUpPageRoot,850,600);

        // Create a new window (stage) for the sign-up page
        Stage signUpStage = new Stage();
        signUpStage.setTitle("Let's Sign Up!");
        signUpStage.setScene(signUpPageScene);
        signUpStage.setResizable(false);

        // Hide the window of the first page
        currentStage.hide();

        // Show the window of the sign-up page
        signUpStage.show();
    }

}
