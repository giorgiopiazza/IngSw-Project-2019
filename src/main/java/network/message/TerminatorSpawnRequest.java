package network.message;

import enumerations.MessageContent;
import enumerations.RoomColor;

public class TerminatorSpawnRequest extends Message {
    private final RoomColor spawnColor;

    public TerminatorSpawnRequest(String username, RoomColor spawnColor) {
        super(username, MessageContent.TERMINATOR_SPAWN);

        this.spawnColor = spawnColor;
    }

    public RoomColor getSpawnColor() {
        return this.spawnColor;
    }
}
