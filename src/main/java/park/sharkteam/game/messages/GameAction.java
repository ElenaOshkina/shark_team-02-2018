package park.sharkteam.game.messages;

import park.sharkteam.websocket.Message;

public class GameAction extends Message {
    private String action;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}