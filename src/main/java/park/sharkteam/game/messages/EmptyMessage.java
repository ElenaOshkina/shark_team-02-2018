package park.sharkteam.game.messages;

import park.sharkteam.websocket.Message;

public class EmptyMessage  extends Message  {
    private String ping;

    public String getPing() {
        return ping;
    }
}
