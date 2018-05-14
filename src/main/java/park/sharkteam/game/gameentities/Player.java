package park.sharkteam.game.gameentities;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import park.sharkteam.game.Config;

import javax.validation.constraints.NotNull;

public class Player {
    @NotNull
    private Integer shells;
    @NotNull
    private Integer healthPoints;
    @NotNull
    private Integer currentLine;
    @NotNull
    private Long position;

    public Player(){
        this.shells = Config.START_HP_VALUE;
        this.healthPoints = Config.START_SHELLS_VALUE;
        this.currentLine = Config.START_LINE;
        this.position = Config.PLAYER_POSITION;
    }

    public void updatePosition(String action){
        switch (action){
            case Config.UP_ACTION:
                currentLine++;
                if (currentLine == Config.LINES_NUM) {
                    currentLine = Config.LINES_NUM - 1;
                }
                break;
            case Config.DOWN_ACTION:
                currentLine--;
                if (currentLine < 0){
                    currentLine = 0;
                }
                break;
            default:
                break;
        }
    }

    public boolean updateHealthPoints(int upd){
        this.healthPoints += upd;
        return isAlive();
    }

    public boolean updateShells(int upd){
        shells += upd ;
        if (shells > 0) {
            return true;
        }
        return false;
    }

    public Long getPosition() {
        return position;
    }

    public Integer getLine(){
        return currentLine;
    }

    public Integer getShells() {
        return shells;
    }

    public Integer getHealthPoints(){
        return healthPoints;
    }

    public boolean isAlive(){
        if (healthPoints > 0) {
            return true;
        }
        return false;
    }

    public void move(String movement){
        switch(movement){
            case Config.DOWN_ACTION:
                if (currentLine > 0){
                    currentLine--;
                }
                break;
            case Config.UP_ACTION:
                if (currentLine < Config.LINES_NUM - 1){
                    currentLine++;
                }
                break;
            default:
                break;
        }
    }


    public ObjectNode getJsonNode(boolean fullView) {
        //return super.toString();
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode playerNode = mapper.createObjectNode();

        playerNode.put("x", position);
        playerNode.put("y", currentLine * Config.LINE_LENGTH + Config.INDENTATION);

        if (fullView){
            playerNode.put("hp",healthPoints);
            playerNode.put("shells",shells);
        }

        return playerNode;
    }
}
