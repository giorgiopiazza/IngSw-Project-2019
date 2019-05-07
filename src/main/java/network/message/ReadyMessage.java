package network.message;

import enumerations.MessageContent;

public class ReadyMessage extends Message {
    private final boolean ready;

    public ReadyMessage(String username, boolean ready) {
        super(username, MessageContent.READY);
        this.ready = ready;
    }

    public boolean isReady() {
        return ready;
    }
}
