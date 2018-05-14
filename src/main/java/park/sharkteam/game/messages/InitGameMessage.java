package park.sharkteam.game.messages;

import park.sharkteam.websocket.Message;

public class InitGameMessage extends Message {
    private String enemy;
    private String message;
    private Integer enemy_id;

    public InitGameMessage(Integer user_id, String enemy){
        this.message = "P_START";
        this.enemy = enemy;
        this.enemy_id = user_id;
    }

    public String getEnemy() {
        return enemy;
    }

    public void setEnemy(String enemy) {
        this.enemy = enemy;
    }
}
