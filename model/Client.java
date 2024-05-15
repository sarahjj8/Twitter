package model;

//import controller.HomeController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.entity.*;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class Client extends Application {
    private static ArrayList<Profile> profiles;
    private static Profile profile;
    private static String username = "my name";

    @Override
    public void start(Stage primaryStage) throws Exception{
        SetUp.setUpClient();

        Parent root = FXMLLoader.load(this.getClass().getResource("../view/fxml/FirstPage.fxml"));
        primaryStage.setTitle("Twitter");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.setResizable(false);
        primaryStage.show();
//
//        Parent root = FXMLLoader.load(this.getClass().getResource("../view/fxml/timeline.fxml"));
//        primaryStage.setTitle("Twitter");
//        primaryStage.setScene(new Scene(root, 430, 600));
//        primaryStage.setResizable(false);
//        primaryStage.show();
    }

    public static Profile getProfile() {
        return profile;
    }

    public static ArrayList<Profile> getProfiles() {
        return profiles;
    }

    public static String getUsername(){
        return username;
    }

    public static void setUsername(String username) {
        Client.username = username;
    }

    public static void setProfiles(ArrayList<Profile> profiles) {
        Client.profiles = profiles;
    }

    public static void main(String[] args) {
        launch(args);
    }
}