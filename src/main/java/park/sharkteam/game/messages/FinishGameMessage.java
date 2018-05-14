package park.sharkteam.game.messages;

import park.sharkteam.websocket.Message;

public class FinishGameMessage extends Message {
    private Boolean isWon;

    public Boolean getWon() {
        return isWon;
    }

    public void setWon(Boolean won) {
        isWon = won;
    }
}
