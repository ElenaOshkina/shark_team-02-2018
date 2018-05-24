package park.sharkteam.game;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import park.sharkteam.game.gameentities.Shell;
import park.sharkteam.game.gameentities.Player;
import park.sharkteam.game.gameentities.Line;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.lang.Math.abs;

public class Game {
    private static final Logger LOGGER = LoggerFactory.getLogger(Game.class);

    private List<Player> players = new ArrayList<>(Config.PLAYERS_NUM);
    private List<Line> lines = new ArrayList<>();
    private List<Shell> shells = new ArrayList<>();

    private Date lastFrameTime = new Date();
    private int nextLinePlayer = 1;

    public Game() {
        for (int i = 0; i < Config.PLAYERS_NUM; i++) {
            players.add(new Player());
        }

        lines.add(new Line(Config.CREATE_LINES_POSITION, 0));
    }

    public void update() {
        if (isFinished()) {
            return;
        }

        final Date now = new Date();
        Long frameTime = now.getTime() - lastFrameTime.getTime();
        lastFrameTime = now;
        //ToDo: откалибровать конфиги  игры
        frameTime = 2L;
        System.out.println("Game frametime:" + frameTime);
        moveObjects(frameTime);
        deletingObjectOutOfMap();
        collisionDetection();
        creatingNewLines();
    }

    public boolean isFinished() {
        int num = 0;
        for (Player player : players) {
            if (player.isAlive()) {
                num++;
            }
        }
        return num == 1 || num == 0;
    }

    public Integer getWinerIndex() {
        int num = 0;
        int survivedIndex = -1;

        for (int i = 0; i < Config.PLAYERS_NUM; i++) {
            if (players.get(i).isAlive()) {
                num++;
                survivedIndex = i;
            }
        }
        if (num == 1) {
            return survivedIndex;
        }
        return -1;
    }

    public void shoot(Integer playerNum) {
        Player player;
        try {
            player = players.get(playerNum);
        } catch (IndexOutOfBoundsException e) {
            LOGGER.warn("Uncorrect player id was requested: " + playerNum, e);
            return;
        }

        if (
             player.getShells() > 0
             && shells.stream().filter(shell -> shell.getPlayer() == playerNum).count() < Config.MAX_SHELLS_COUNT
        ) {
            shells.add(new Shell(player.getLine()));
            player.updateShells(-1);
        }
    }

    public int getAnotherPlayer(int index) {
        if (index < Config.PLAYERS_NUM - 1) {
            index++;
            return index;
        }
        return 0;
    }

    public void moveUser(Integer playerNum, String message) {
        Player player;
        try {
            player = players.get(playerNum);
        } catch (IndexOutOfBoundsException e) {
            LOGGER.warn("Uncorrect player id was requested: " + playerNum, e);
            return;
        }
        player.move(message);
    }

    private void creatingNewLines() {
        if (lines.isEmpty() || lines.get(lines.size() - 1).getPosition() < Config.RIGHT_MAP_EDGE - Config.LINES_DISTANCE) {
            lines.add(new Line(Config.CREATE_LINES_POSITION, nextLinePlayer));
            nextLinePlayer++;
            if (nextLinePlayer == players.size()) {
                nextLinePlayer = 0;
            }
        }
    }

    private void deletingObjectOutOfMap() {
        ArrayList<Line> deletingLines = new ArrayList<>();
        for (Line line : lines) {
            if (line.getPosition() < Config.LEFT_MAP_EDGE) {
                deletingLines.add(line);
            }
        }
        for (Line line : deletingLines) {
            lines.remove(line);
        }

        ArrayList<Shell> deletingShells = new ArrayList<>();
        for (Shell shell : shells) {
            if (shell.getPosition() > Config.RIGHT_MAP_EDGE) {
                deletingShells.add(shell);
            }
        }
        for (Shell shell : deletingShells) {
            shells.remove(shell);
        }

    }

    private void moveObjects(long frameTime) {
        for (Shell shell : shells) {
            shell.move(frameTime, Config.SHELL_SPEED);
        }

        for (Line line : lines) {
            line.move(frameTime, Config.METEOR_SPEED);
        }
    }

    private void collisionDetection() {
        for (Line line : lines) {
            //with player
            for (int i = 0; i < players.size(); i++) {
                Player player = players.get(i);
                if (
                      (abs(player.getPosition() - line.getPosition()) < Config.PLAYER_HITBOX)
                      && (line.getObject(i, player.getLine()) != 0)
                ) {
                    switch (line.getObject(i, player.getLine())) {
                        case Config.METEOR_CODE:
                            player.updateHealthPoints(-1);
                            if (isFinished()) {
                                return;
                            }
                            line.replaceObject(i, player.getLine(), 0);
                            break;
                        case Config.HP_CODE:
                            player.updateHealthPoints(1);
                            break;
                        case Config.SHELL_CODE:
                            player.updateShells(1);
                            break;
                        default:
                            break;
                    }
                }
            }

            //with shells
            for (Shell shell : shells) {
                if (
                    (line.getObject(shell.getPlayer(), shell.getLine()) == Config.METEOR_CODE)
                    && (abs(shell.getPosition() - line.getPosition()) < Config.SHELL_HITBOX)
                ) {
                    line.replaceObject(shell.getPlayer(), shell.getLine(), 0);
                    line.replaceObject(getAnotherPlayer(shell.getPlayer()), shell.getLine(), Config.METEOR_CODE);
                }
            }
        }
    }

    public ObjectNode getGameStateMessageForUser(int index) {
        ObjectMapper mapper = new ObjectMapper();

        ObjectNode root = mapper.createObjectNode();

        for (int i = 0; i < Config.PLAYERS_NUM; i++) {
            ArrayNode objectsNode = mapper.createArrayNode();
            for (Line line : lines) {
                objectsNode = line.getStateMessageForUser(objectsNode, i, (i == index));
            }

            ArrayNode shellsNode = mapper.createArrayNode();
            for (Shell shell : shells) {
                shellsNode = shell.getStateMessageForUser(shellsNode, index);
            }

            ObjectNode playerNode = players.get(i).getJsonNode(i == index);

            if (i == index) {
                root.putPOJO("your_player", playerNode);
                root.putPOJO("your_objects", objectsNode);
                root.putPOJO("your_shells", shellsNode);
            } else {
                root.putPOJO("enemy_player", playerNode);
                root.putPOJO("enemy_objects", objectsNode);
                root.putPOJO("enemy_shells", shellsNode);
            }
        }
        return root;
    }

    //Только для тестов:
    public void updateForTest(long frameTime) {
        if (isFinished()) {
            return;
        }

        moveObjects(frameTime);
        deletingObjectOutOfMap();
        collisionDetection();
        creatingNewLines();
    }

    public List<Line> getLines() {
        return lines;
    }

    public Player getPlayer(int index) {
        return players.get(index);
    }

    public List<Shell> getShells() {
        return shells;
    }
}
