package controller;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
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
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import model.Client;
import model.entity.Hashtag;
import model.entity.HttpClientResponse;
import model.entity.JsonHelper;
import model.entity.Tweet;
import view.ToolbarController;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HashtagController {
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private ScrollPane scrollPane;
    private VBox hashtagContent;
    private Hashtag hashtag;
    private ToolbarController toolbar;
    private Label hashtagNameLabel;
    private ArrayList<Tweet> tweets;
    private HBox info;
    private static Image trendImage = new Image(String.valueOf(ToolbarController.class.getResource("icons/diagram.png")));
    private static ImageView trendImageView = new ImageView(trendImage);
    private static JFXButton trendButton;
    private static Image backImage = new Image(String.valueOf(ToolbarController.class.getResource("icons/back.png")));
    private static ImageView backImageView = new ImageView(backImage);
    private static JFXButton backButton;
    private HashMap<LocalDate, Integer> messageCountPerDay;
    private HashMap<LocalDate, Integer> allMessageCountPerDay;

    public HashtagController(Hashtag hashtag) {
        this.hashtag = hashtag;
    }

    public void initialize(){
        backButton = new JFXButton();
        trendButton = new JFXButton();

        hashtagNameLabel = new Label(hashtag.getHashtagName());
        info = new HBox(5);
        info.getChildren().addAll(backButton, hashtagNameLabel);

        hashtagContent = new VBox(5);

        setTweets();
        HomeController.addTweetsToVbox(tweets, hashtagContent);

        toolbar = new ToolbarController();
        toolbar.goToExplorePage();

        scrollPane.setContent(hashtagContent);

        anchorPane.getChildren().addAll(info, toolbar, trendButton);

        setConfigure();
        setLocation();
        setAction();
    }

    private void setConfigure(){
        info.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(info, Priority.ALWAYS);

        hashtagContent.setStyle("-fx-background-color:#ffffff;" +
                "-fx-padding: 8;");

        anchorPane.setStyle("-fx-background-color:#ffffff;" +
                "-fx-background-radius:10;" +
                "-fx-border-radius:10;" +
                "-fx-border-color:rgba(243,243,243,0)");

        scrollPane.setStyle("-fx-focus-color: transparent; " +
                "-fx-faint-focus-color: transparent;" +
                "-fx-background-color:#f3f3f3;" +
                "-fx-border-color: rgba(255,255,255,0)");

        backImageView.setFitWidth(15);
        backImageView.setFitHeight(15);
        backButton.setGraphic(backImageView);
        backButton.setGraphic(backImageView);
        backButton.setPrefSize(15,15);
        backButton.setRipplerFill(Paint.valueOf("#858080"));
        backButton.setStyle("-fx-background-radius: 50");

        trendImageView.setFitWidth(15);
        trendImageView.setFitHeight(15);
        trendButton.setGraphic(trendImageView);
        trendButton.setGraphic(trendImageView);
        trendButton.setPrefSize(15,15);
        trendButton.setRipplerFill(Paint.valueOf("#858080"));
        trendButton.setStyle("-fx-background-radius: 50");

        hashtagNameLabel.setFont(Font.font("Franklin Gothic", FontWeight.BLACK,15));
        hashtagNameLabel.setStyle("-fx-text-fill:#0f1419;");
    }

    private void setLocation(){
        AnchorPane.setTopAnchor(toolbar, 30.0);
        AnchorPane.setLeftAnchor(toolbar, 0.0);
        AnchorPane.setBottomAnchor(toolbar, 0.0);

        AnchorPane.setLeftAnchor(info, 75.0);
        AnchorPane.setTopAnchor(info, 30.0);

        AnchorPane.setRightAnchor(trendButton, 20.0);
        AnchorPane.setTopAnchor(trendButton, 30.0);
    }

    private void setAction(){
        backButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                toolbar.goBackToExplore();
            }
        });
        backButton.setOnMouseEntered(event -> {
            backButton.setCursor(Cursor.HAND);
        });
        trendButton.setOnMouseEntered(event -> {
            trendButton.setCursor(Cursor.HAND);
        });
        trendButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                calculateHashtagStatistics();
            }
        });
    }

    private void setTweets(){
        Map<String, String> params = Map.of("username", Client.getUsername(), "hashtag name", hashtag.getHashtagName());
        HttpClientResponse response = HttpClientResponse.sendRequest(HttpClientResponse.createURL("hashtags"), null, params, "GET");
        if(response.getResponseCode() == 200 && response.getResponse().equals("No tweets ...")){
            tweets = new ArrayList<>();
        } else if (response.getResponseCode() == 200) {
            tweets = JsonHelper.parseJsonToTweetListWithAdapter(response.getResponse());
        }
    }

    private void setMessageCountPerDay(){
        String name = hashtag.getHashtagName();
        if (name.contains("#"))
            name =  name.replaceAll("#", "%23");
        Map<String, String> params = Map.of("hashtag name", name);
        HttpClientResponse response = HttpClientResponse.sendRequest(HttpClientResponse.createURL("countHashtags"), null, params, "GET");
        if(response.getResponseCode() != 200){
            messageCountPerDay = new HashMap<>();
        } else{
            messageCountPerDay = JsonHelper.hashmapDeserializer(response.getResponse());
        }
    }

    private void setAllMessageCountPerDay(){
        Map<String, String> params = Map.of();
        HttpClientResponse response = HttpClientResponse.sendRequest(HttpClientResponse.createURL("countTotalHashtags"), null, params, "GET");
        if(response.getResponseCode() != 200){
            allMessageCountPerDay = new HashMap<>();
        } else{
            allMessageCountPerDay = JsonHelper.hashmapDeserializer(response.getResponse());
        }
    }

    private void calculateHashtagStatistics() {
        LocalDate currentDate = LocalDate.now();
        setMessageCountPerDay();
        setAllMessageCountPerDay();
        // Calculating the ratio of messages with the hashtag to the total number of messages per day
        List<Double> hashtagRatioList = new ArrayList<>();
        for (LocalDate date : allMessageCountPerDay.keySet()) {
            int totalMessages = allMessageCountPerDay.get(date);
            int hashtagMessages;
            if(messageCountPerDay.get(date) == null)
                hashtagMessages = 0;
            else
                hashtagMessages = messageCountPerDay.get(date);
            double hashtagRatio = (double) hashtagMessages / totalMessages;
            hashtagRatioList.add(hashtagRatio);
        }
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(hashtag.getHashtagName());

        for (int i = 0; i < hashtagRatioList.size(); i++) {
            LocalDate date = currentDate.minusDays(i);
            double hashtagRatio = hashtagRatioList.get(i);
            String dateString = date.toString();
            XYChart.Data<String, Number> data = new XYChart.Data<>(dateString, hashtagRatio);
            series.getData().add(data);
        }

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Hashtag Statistics");
        lineChart.setAnimated(false);

        lineChart.getData().add(series);

        series.getNode().setStyle("-fx-text-fill: #0487e5; -fx-background-color: #ffffff00;");

        series.getNode().setStyle("-fx-stroke: #0487e5;");

        for (XYChart.Series<String, Number> s : lineChart.getData()) {
            for (XYChart.Data<String, Number> d : s.getData()) {
                Node symbol = d.getNode().lookup(".chart-line-symbol");
                symbol.setStyle("-fx-background-color: #0487e5;");
            }
        }

        Stage chartStage = new Stage();
        chartStage.setTitle("Hashtag Statistics");
        Scene chartScene = new Scene(lineChart, 500, 300);
        chartStage.setScene(chartScene);
        chartStage.show();
    }
}
