package model.entity;

public class Vote {
    private int pollId;
    private int choiceId;
    private String username;

    public int getPollId() {
        return pollId;
    }

    public int getChoiceId() {
        return choiceId;
    }

    public String getUsername() {
        return username;
    }
}
