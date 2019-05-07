package network.message;

import enumerations.MessageContent;
import model.player.PlayerPosition;

public class TerminatorSpawnRequest extends Message {
    private final PlayerPosition terminatorSpawnPosition;

    public TerminatorSpawnRequest(String username, PlayerPosition terminatorSpawnPosition) {
        super(username, MessageContent.TERMINATOR_SPAWN);

        this.terminatorSpawnPosition = terminatorSpawnPosition;
    }

    public PlayerPosition getTerminatorSpawnPosition() {
        return terminatorSpawnPosition;
    }
}
