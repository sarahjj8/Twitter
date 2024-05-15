package view;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import model.Client;
import model.entity.HttpClientResponse;
import model.entity.Poll;

import java.util.HashMap;
import java.util.Map;

public class PollComponent extends TweetComponent {
    private Poll poll;
    private VBox choiceBox;
    private Map<String, Integer> voteCounts;
    private ToggleGroup toggleGroup;
    private boolean hasVotedBefore;
    public PollComponent(Poll poll) {
        super(poll, true);
        this.poll = poll;
        voteCounts = new HashMap<>();
        choiceBox = new VBox();

        toggleGroup = new ToggleGroup();
        for (String choice : poll.getChoices()) {
            RadioButton radioButton = new RadioButton(choice);
            radioButton.setToggleGroup(toggleGroup);
            radioButton.setOnAction(event -> vote(choice));
            choiceBox.getChildren().add(radioButton);
            voteCounts.put(choice, poll.getNumberOfVotes(choice));

            Label resultLineLabel = new Label();
            choiceBox.getChildren().add(resultLineLabel);
        }

        choiceBox.setAlignment(Pos.CENTER_LEFT);
        choiceBox.setSpacing(10);

        tweetContent.getChildren().add(1, choiceBox);
        setPollConfig();

        setHasVotedBefore();
        if(hasVotedBefore){
            toggleGroup.getToggles().forEach(toggle -> ((RadioButton) toggle).setDisable(true));
            updateVoteCounts();
        }
    }

    public void setHasVotedBefore() {
        Map<String, String> params = Map.of("username", Client.getUsername(),"poll id", String.valueOf(poll.getId()));
        HttpClientResponse response = HttpClientResponse.sendRequest(HttpClientResponse.createURL("hasVotedBefore"), null, params, "GET");
        if(response.getResponseCode() != 200){
            Message.showTemporaryMessage(choiceBox, response.getResponse());
            hasVotedBefore = false;
        } else {
            if(response.getResponse().equals("true"))
                hasVotedBefore = true;
            else
                hasVotedBefore = false;
        }
    }

    private void setPollConfig() {
        choiceBox.setStyle("-fx-background-color:#ffffff;" +
                "-fx-background-radius:10;" +
                "-fx-border-radius:10;" +
                "-fx-border-color:#ffffff");
        for(Node node : choiceBox.getChildren()){
            if(node instanceof Label){
                Label resultLineLabel = (Label) node;
                resultLineLabel.setStyle("-fx-background-color:#0e9ff1;" +
                        "-fx-background-radius:10;" +
                        "-fx-border-radius:10;" +
                        "-fx-border-color:#0e9ff1");
                resultLineLabel.setMaxHeight(6);
                resultLineLabel.setMinHeight(6);
                resultLineLabel.setPrefHeight(6);
                resultLineLabel.setVisible(false);
            }
        }
    }

    public void vote(String choice) {
        RadioButton selectedRadioButton = getRadioButtonByText(choice);
        saveVote(selectedRadioButton.getText());
        if (selectedRadioButton != null) {
            voteCounts.put(choice, voteCounts.get(choice) + 1);
            updateVoteCounts();
            selectedRadioButton.setSelected(true);
            toggleGroup.getToggles().forEach(toggle -> ((RadioButton) toggle).setDisable(true));
        }
    }

    private void updateVoteCounts() {
        int totalVotes = this.voteCounts.values().stream().mapToInt(Integer::intValue).sum();
        int i = 0;
        for (String choice : poll.getChoices()) {
            int choiceVotes = voteCounts.getOrDefault(choice, 0);
            double choicePercentage = (double) choiceVotes / totalVotes * 100.0;
            String choiceResult = String.format("%s (%.1f%%)", choice, choicePercentage);
            RadioButton radioButton = getRadioButtonByText(choice);
            radioButton.setText(choiceResult);
            i++;
            Label resultLineLabel = (Label) choiceBox.getChildren().get(i);
            resultLineLabel.setVisible(true);
            resultLineLabel.setMaxWidth(choicePercentage * 2.6);
            resultLineLabel.setMinWidth(choicePercentage * 2.6);
            resultLineLabel.setPrefWidth(choicePercentage * 2.6);
            i++;
        }
    }

    private RadioButton getRadioButtonByText(String text) {
        for (int i = 0; i < choiceBox.getChildren().size(); i++) {
            if (choiceBox.getChildren().get(i) instanceof RadioButton) {
                RadioButton radioButton = (RadioButton) choiceBox.getChildren().get(i);
                if (radioButton.getText().equalsIgnoreCase(text)) {
                    return radioButton;
                }
            }
        }
        return null;
    }

    private boolean saveVote(String choice){
        String url = "http://localhost:8081/voteWithChoiceText";
        String requestBody = "{\"pollId\": \"" + poll.getId() + "\",\"choice text\": \"" + choice + "\",\"username\": \"" + Client.getUsername() + "\"}";
        model.entity.HttpClientResponse response = model.entity.HttpClientResponse.sendRequest(url, requestBody, null, "POST");
        if(response != null && response.getResponseCode() != 201){
            Message.showTemporaryMessage(textArea, response.getResponse());
            return false;
        } else if (response == null) {
            Message.showTemporaryMessage(textArea, "Oops! Something went wrong.");
            return false;
        }
        return true;
    }
}