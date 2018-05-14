package park.sharkteam.game.gameentities;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import park.sharkteam.game.Config;
import java.util.Random;

public class Line {
    private int[][] lines = new  int[Config.PLAYERS_NUM][Config.LINES_NUM];
    private long position;

    private Random rand = new Random();

    public Line(Long position, int playerNum) {

        int meteors = 0;
        for (int i = 0; i < Config.LINES_NUM; i++) {
            //Создание метеоритов
            if ((meteors < Config.LINES_NUM - 1) && (Math.random() > 0.2)) {
                meteors++;
                lines[playerNum][i] = Config.METEOR_CODE;
            } else {
                //с небольшой вероятностью создаются припасы
                if (Math.random() > 0.2) {
                    if (Math.random() > 0.5) {
                        lines[playerNum][i] = Config.HP_CODE;
                    } else {
                        lines[playerNum][i] = Config.SHELL_CODE;
                    }
                }
            }
        }

        if (meteors == 0) {
            //на тот случай, если ни одного метеорита не было создано
            lines[playerNum][rand.nextInt(Config.LINES_NUM)] = Config.METEOR_CODE;
        }

        this.position = position;
    }

    public Integer getObject(int lineIndex, int index) {
        return lines[lineIndex][index];
    }

    public Long getPosition() {
        return position;
    }

    public void setPosition(Long position) {
        this.position = position;
    }

    public void replaceObject(int lineIndex, int index, int object) {
        if (object == Config.SHELL_CODE || object == Config.HP_CODE || object == Config.METEOR_CODE || object == 0) {
            lines[lineIndex][index] = object; //  .get(lineIndex).set(index, object);
        }
    }

    public void move(long deltaTime, long speed) {
        position -= deltaTime * speed;
    }

    public ArrayNode getStateMessageForUser(ArrayNode objectsNode, int  playerIndex, boolean fullView) {
        ObjectMapper mapper = new ObjectMapper();
        for (int i = 0; i < Config.PLAYERS_NUM; i++) {
            ObjectNode objectNode = mapper.createObjectNode();
            switch (lines[playerIndex][i]) {
                case Config.METEOR_CODE:
                    objectNode.put("type", Config.METEOR_JSON_CODE);
                    objectNode.put("x", position);
                    objectNode.put("y", i * Config.LINE_LENGTH + Config.INDENTATION);
                    objectsNode.add(objectNode);
                    break;
                case Config.HP_CODE:
                    if (fullView) {
                        objectNode.put("type", Config.HP_JSON_CODE);
                        objectNode.put("x", position);
                        objectNode.put("y", i * Config.LINE_LENGTH + Config.INDENTATION);
                        objectsNode.add(objectNode);
                    }
                    break;
                case Config.SHELL_CODE:
                    objectNode.put("type", Config.SHELL_JSON_CODE);
                    objectNode.put("x", position);
                    objectNode.put("y", i * Config.LINE_LENGTH + Config.INDENTATION);
                    objectsNode.add(objectNode);
                    break;
                default:
                    break;
            }
        }

        return objectsNode;
    }

}
