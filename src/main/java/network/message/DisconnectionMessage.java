package network.message;

import enumerations.MessageContent;
import utility.GameCostants;

public class DisconnectionMessage extends Message {
    private static final long serialVersionUID = -8073712986535222650L;

    private final String username;

    public DisconnectionMessage(String username) {
        super(GameCostants.GOD_NAME, null, MessageContent.DISCONNECTION);
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return "DisconnectionMessage{" +
                "senderUsername=" + getSenderUsername() +
                ", content=" + getContent() + ", " +
                ", username='" + username + '\'' +
                '}';
    }
}
