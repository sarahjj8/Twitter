package view;

import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Popup;
import javafx.util.Duration;

public class Message {
    public static void showTemporaryMessage(Node node, String message) {
        TextField messageTextField = new TextField();
        messageTextField.setMouseTransparent(true);
        messageTextField.setFocusTraversable(false);
        messageTextField.setMaxWidth(Region.USE_PREF_SIZE);

        Text text = new Text(message);
        text.setStyle("-fx-fill: white;");
        text.setTextAlignment(TextAlignment.CENTER);
        text.setWrappingWidth(280);

        StackPane stackPane = new StackPane(text);
        stackPane.setStyle("-fx-background-color: rgba(68,83,94,0.88); -fx-text-fill: white;" +
                "-fx-padding: 10px;" +
                "-fx-background-radius: 30;");
        stackPane.setMaxWidth(300);
        stackPane.setPrefHeight(Region.USE_COMPUTED_SIZE);

        AnchorPane.setTopAnchor(stackPane, 250.0);
        AnchorPane.setLeftAnchor(stackPane, 98.0);

        AnchorPane root = (AnchorPane) node.getScene().getRoot();
        root.getChildren().add(stackPane);

        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), stackPane);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setDelay(Duration.seconds(3));
        fadeOut.setOnFinished(event -> root.getChildren().remove(stackPane));
        fadeOut.play();
    }

    public static void showTemporaryMessageInVBox(Node node, String message) {
        TextField messageTextField = new TextField();
        messageTextField.setMouseTransparent(true);
        messageTextField.setFocusTraversable(false);
        messageTextField.setMaxWidth(Region.USE_PREF_SIZE);

        Text text = new Text(message);
        text.setStyle("-fx-fill: white;");
        text.setTextAlignment(TextAlignment.CENTER);
        text.setWrappingWidth(280);

        StackPane stackPane = new StackPane(text);
        stackPane.setStyle("-fx-background-color: rgba(68,83,94,0.88); -fx-text-fill: white;" +
                "-fx-padding: 10px;" +
                "-fx-background-radius: 30;");
        stackPane.setMaxWidth(300);
        stackPane.setPrefHeight(Region.USE_COMPUTED_SIZE);

        AnchorPane.setTopAnchor(stackPane, 250.0);
        AnchorPane.setLeftAnchor(stackPane, 98.0);

        VBox root = (VBox) node.getScene().getRoot();
        root.getChildren().add(stackPane);

        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), stackPane);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setDelay(Duration.seconds(3));
        fadeOut.setOnFinished(event -> root.getChildren().remove(stackPane));
        fadeOut.play();
    }

    public static void showTemporaryMessageUsingPopup(Node node, String message) {
        Popup popup = new Popup();

        Text text = new Text(message);
        text.setStyle("-fx-fill: white;");
        text.setTextAlignment(TextAlignment.CENTER);
        text.setWrappingWidth(280);

        StackPane stackPane = new StackPane(text);
        stackPane.setStyle("-fx-background-color: rgba(68,83,94,0.88); -fx-text-fill: white;" +
                "-fx-padding: 10px;" +
                "-fx-background-radius: 30;");
        stackPane.setMaxWidth(300);

        popup.getContent().add(stackPane);
        popup.setAutoHide(true);
        popup.show(node.getScene().getWindow(), node.getScene().getWindow().getX(), node.getScene().getWindow().getY() + node.localToScene(node.getBoundsInLocal()).getMaxY());
    }
}


