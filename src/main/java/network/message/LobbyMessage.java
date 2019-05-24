package network.message;

import enumerations.MessageContent;
import enumerations.PlayerColor;

import java.util.Objects;

public class LobbyMessage extends Message {
    private PlayerColor chosenColor;

    public LobbyMessage(String userName, MessageContent messageContent, PlayerColor chosenColor) {
        // message content can only be GET_IN_LOBBY or DISCONNECTION
        super(userName, messageContent);

        this.chosenColor = chosenColor;
    }

    public PlayerColor getChosenColor() {
        return chosenColor;
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
}
