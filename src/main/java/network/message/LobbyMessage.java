package network.message;

import enumerations.MessageContent;
import enumerations.PlayerColor;

import java.util.Objects;

public class LobbyMessage extends Message {
    private static final long serialVersionUID = -8420070827032848314L;

    private final PlayerColor chosenColor;
    private final boolean disconnection;

    public LobbyMessage(String username, String token, PlayerColor chosenColor, boolean disconnection) {
        super(username, token, MessageContent.GET_IN_LOBBY);

        this.chosenColor = chosenColor;
        this.disconnection = disconnection;
    }

    public PlayerColor getChosenColor() {
        return chosenColor;
    }

    public boolean isDisconnection() {
        return this.disconnection;
    }

    // a Lobby message is equal to an other (in our case) if its the same message or if it has the same user sender name
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LobbyMessage that = (LobbyMessage) o;
        return getSenderUsername().equals(that.getSenderUsername());
    }

    @Override
    public int hashCode() {
        return Objects.hash(chosenColor);
    }

    @Override
    public String toString() {
        return "LobbyMessage{" +
                "senderUsername=" + getSenderUsername() +
                ", content=" + getContent() + ", " +
                "chosenColor=" + chosenColor +
                ", disconnection=" + disconnection +
                '}';
    }
}
