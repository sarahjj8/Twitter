package model.entity;

import java.time.LocalDateTime;
import java.util.Objects;

public class Direct {
    private String senderUsername;
    private String receiverUsername;
    private String text;
    private LocalDateTime date;

    public Direct(String senderUsername, String receiverUsername, String text, LocalDateTime date) {
        this.senderUsername = senderUsername;
        this.receiverUsername = receiverUsername;
        this.text = text;
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Direct direct = (Direct) o;
        return Objects.equals(senderUsername, direct.senderUsername) && Objects.equals(receiverUsername, direct.receiverUsername) && Objects.equals(text, direct.text) && Objects.equals(date, direct.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(senderUsername, receiverUsername, text, date);
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public String getReceiverUsername() {
        return receiverUsername;
    }

    public String getText() {
        return text;
    }

    public LocalDateTime getDate() {
        return date;
    }
}
