package network.message;

import enumerations.MessageContent;
import enumerations.RoomColor;

public class TerminatorSpawnRequest extends Message {
    private static final long serialVersionUID = 1272585248979532293L;

    private final RoomColor spawnColor;

    public TerminatorSpawnRequest(String username, String token, RoomColor spawnColor) {
        super(username, token, MessageContent.BOT_SPAWN);

        this.spawnColor = spawnColor;
    }

    public RoomColor getSpawnColor() {
        return this.spawnColor;
    }

    @Override
    public String toString() {
        return "TerminatorSpawnRequest{" +
                "senderUsername=" + getSenderUsername() +
                ", content=" + getContent() +
                ", spawnColor=" + spawnColor +
                '}';
    }
}
