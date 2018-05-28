package park.sharkteam.game.messages;

import park.sharkteam.websocket.Message;

public class InitGameMessage extends Message {
    private String enemy;
    private String message;
    private Integer enemyId;

    public InitGameMessage(Integer userId, String enemy) {
        this.message = "P_START";
        this.enemy = enemy;
        this.enemyId = userId;
    }

    public String getEnemy() {
        return enemy;
    }

    public void setEnemy(String enemy) {
        this.enemy = enemy;
    }
}
