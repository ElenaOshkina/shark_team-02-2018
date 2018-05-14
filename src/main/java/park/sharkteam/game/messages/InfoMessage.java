package park.sharkteam.game.messages;

import park.sharkteam.websocket.Message;

public class InfoMessage extends Message {
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
