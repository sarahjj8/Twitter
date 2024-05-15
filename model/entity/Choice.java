package model.entity;

public class Choice {
    int id;
    int numberOfVotes;

    public Choice(int id, int numberOfVotes) {
        this.id = id;
        this.numberOfVotes = numberOfVotes;
    }

    public int getId() {
        return id;
    }

    public int getNumberOfVotes() {
        return numberOfVotes;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNumberOfVotes(int numberOfVotes) {
        this.numberOfVotes = numberOfVotes;
    }
}