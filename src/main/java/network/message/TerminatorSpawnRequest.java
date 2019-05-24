package network.message;

import enumerations.MessageContent;
import enumerations.RoomColor;

public class TerminatorSpawnRequest extends Message {
    private final RoomColor spawnColor;

    public TerminatorSpawnRequest(String username, String token, RoomColor spawnColor) {
        super(username, token, MessageContent.TERMINATOR_SPAWN);

        this.spawnColor = spawnColor;
    }

    public RoomColor getSpawnColor() {
        return this.spawnColor;
    }
}
