package network.message;

import enumerations.MessageContent;

import java.util.Objects;

public class GameSetupMessage extends Message {
    private int mapVote;
    private boolean terminatorVote;
    private int skullNumVote;

    public GameSetupMessage(String username, String token, int mapVote, boolean terminatorVote, int skullNumVote) {
        super(username, token, MessageContent.GAME_SETUP);

        this.mapVote = mapVote;
        this.terminatorVote = terminatorVote;
        this.skullNumVote = skullNumVote;
    }

    public int getMapVote() {
        return mapVote;
    }

    public boolean getTerminatorVote() {
        return terminatorVote;
    }

    public int getSkullNumVote() {
        return skullNumVote;
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
