package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import model.entity.JsonHelper;
import model.entity.Profile;
import model.entity.User;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import model.Client;



public class SignUpController {

    @FXML
    private Label signUpLabel;

    @FXML
    private Label nameLabel;

    @FXML
    private Label lastnameLabel;

    @FXML
    private TextField nameTxt;

    @FXML
    private TextField lastnameTxt;

    @FXML
    private Label birthDateLabel;

    @FXML
    private Label usernameLabel;

    @FXML
    private DatePicker birthDateField;

    @FXML
    private TextField usernameTxt;

    @FXML
    private Label emailLabel;

    @FXML
    private Label phoneNumberLabel;

    @FXML
    private TextField emailTxt;

    @FXML
    private TextField phoneNumberTxt;

    @FXML
    private Label passLabel;

    @FXML
    private Label re_enteredPassLabel;

    @FXML
    private TextField passTxt;

    @FXML
    private TextField re_enterdpassTxt;

    @FXML
    private Label countryLabel;

    @FXML
    private ComboBox<String> countryComboBox;

    @FXML
    private Separator separator;

    @FXML
    private Button SignUpButton;

    @FXML
    private Label exceptionLabel;
    @FXML
    private VBox vbox;
    @FXML
    private HBox hbox;
    private Button signUpButton2;

    @FXML
    public void SignUp(ActionEvent event) {
        String name = nameTxt.getText();
        String lastname = lastnameTxt.getText();
        String email = emailTxt.getText();
        String password = passTxt.getText();
        String re_enteredPass = re_enterdpassTxt.getText();
        String phoneNumber = phoneNumberTxt.getText();
        String username = usernameTxt.getText();
        LocalDate dateSelected = birthDateField.getValue();
        Date birthDate;
        if(dateSelected == null)
            birthDate = null;
        else
            birthDate = Date.from(dateSelected.atStartOfDay(ZoneId.systemDefault()).toInstant());
        String country;
        if(countryComboBox.getValue() == null)
            country = null;
        else
            country = countryComboBox.getValue().toString();
        //User newUser =new User(name,lastname,email,phoneNumber,birthDate,country,username);
        String url = "http://localhost:8081/signup";
        String requestBody = null;
        if (email.equals(null) && !phoneNumber.equals(null)){
            requestBody = "{\"name\": \"" + name+ "\",\"lastname\": \"" + lastname + "\",\"phoneNumber\": \"" + phoneNumber + "\"" +
                    ",\"birthDate\": \""+birthDate + "\",\"country\": \""+country +"\",\"username\": \""+username+ "\",\"password\": \""+password
                    +"\",\"re_enteredPass\": \""+re_enteredPass + "\"}";
        }
        else if (phoneNumber.equals(null) && !email.equals(null)){
            requestBody = "{\"name\": \"" + name+ "\",\"lastname\": \"" + lastname + "\",\"email\": \"" + email + "\"" +
                    ",\"birthDate\": \""+birthDate + "\",\"country\": \""+country +"\",\"username\": \""+username+ "\",\"password\": \""+password
                    +"\",\"re_enteredPass\": \""+re_enteredPass + "\"}";
        }
        else if (!email.equals(null) && !phoneNumber.equals(null)){
            requestBody = "{\"name\": \"" + name+ "\",\"lastname\": \"" + lastname + "\",\"phoneNumber\": \"" + phoneNumber + "\"" +
                    ",\"birthDate\": \""+birthDate + "\",\"country\": \""+country +"\",\"username\": \""+username+ "\",\"password\": \""+password
                    +"\",\"re_enteredPass\": \""+re_enteredPass + "\",\"email\": \""+email+ "\"}";
        }

        model.entity.HttpClientResponse response = model.entity.HttpClientResponse.sendRequest(url, requestBody, null, "POST");
        if (response != null){
            if (response.getResponseCode() == 201){
                Client.setUsername(username);
                // Get the current scene and its window
                exceptionLabel.setText("Welcome to twitter!");
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

                // Create a new window (stage) for the sign-in page
                Stage timelineStage = new Stage();
                timelineStage.setScene(timelineScene);
                timelineStage.setResizable(false);
                timelineStage.setTitle("Twitter");

                // Hide the window of the SignUp page
                currentStage.hide();

                // Show the window
                timelineStage.show();
                //here I have set the username of the user who has signed up:
                model.Client.setUsername(username);

            }
            else {
                exceptionLabel.setText(response.getResponse());
            }
        }
    }

    @FXML
    public void initialize(){
        //some actions due to our comboBox:
        ObservableList<String> countryList = FXCollections.observableArrayList("Afghanistan","Albania","Algeria","Angola",
                "Bahamas","Bangladesh","Barbados","Belarus","Canada","China","Colombia","Denmark","Egypt","France","Germany",
                "Hong Kong","Iran","Japan","Kenya","Liberia","Mali","Nepal","Pakistan","Romania","Serbia","Tonga","United Kingdom"
                ,"Venezuela","Yemen");
        countryComboBox.setItems(countryList);
        signUpButton2 = new Button("SignUp");
        signUpButton2.setTextFill(Color.WHITE);
        signUpButton2.setPrefSize(228, 48);
        signUpButton2.setStyle("-fx-background-radius: 20; -fx-background-color: #B9C5C8;");
        signUpButton2.setFont(Font.font("Arial Bold", 17));
        vbox.getChildren().add(signUpButton2);
        signUpButton2.setOnAction(actionEvent -> {
            SignUp(actionEvent);
        });
    }

}





