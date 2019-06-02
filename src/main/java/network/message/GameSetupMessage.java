package network.message;

import enumerations.MessageContent;

import java.util.Objects;

public class GameSetupMessage extends Message {
    private int mapVote;

    public GameSetupMessage(String username, String token, int mapVote) {
        super(username, token, MessageContent.GAME_SETUP);

        this.mapVote = mapVote;
    }

    public int getMapVote() {
        return mapVote;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameSetupMessage that = (GameSetupMessage) o;
        return getSenderUsername().equals(that.getSenderUsername());
    }

    @Override
    public int hashCode() {
        return Objects.hash(mapVote);
    }
}
