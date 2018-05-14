package park.sharkteam.game.gameentities;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import park.sharkteam.game.Config;

public class Bullet {
    private long position;
    private int line;
    private int player;

    public Bullet(Long position, int line, int player) {
        this.position = position;
        this.line = line;
        this.player = player;
    }

    public Bullet(int line) {
        this.position = Config.CREATE_SHELL_POSITION;
        this.line = line;
    }

    public void move(long deltaTime, long speed) {
        position += deltaTime * speed;
    }

    public long getPosition() {
        return position;
    }

    public int getPlayer() {
        return player;
    }

    public int getLine() {
        return line;
    }

    public ArrayNode getStateMessageForUser(ArrayNode shellsNode, int curPlayer) {
        ObjectMapper mapper = new ObjectMapper();
        if (this.player == curPlayer) {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("x", position);
            objectNode.put("y", this.line * Config.LINE_LENGTH + Config.INDENTATION);

            shellsNode.add(objectNode);
        }
        return shellsNode;
    }
}
