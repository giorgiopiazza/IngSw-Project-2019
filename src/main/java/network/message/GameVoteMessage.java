package network.message;

import enumerations.MessageContent;

import java.util.Objects;

public class GameVoteMessage extends Message {
    private static final long serialVersionUID = -1592035927392350342L;

    private final int mapVote;

    public GameVoteMessage(String username, String token, int mapVote) {
        super(username, token, MessageContent.LOBBY_VOTE);

        this.mapVote = mapVote;
    }

    public int getMapVote() {
        return mapVote;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameVoteMessage that = (GameVoteMessage) o;
        return getSenderUsername().equals(that.getSenderUsername());
    }

    @Override
    public int hashCode() {
        return Objects.hash(mapVote);
    }
}
