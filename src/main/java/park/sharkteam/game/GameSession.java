package park.sharkteam.game;


import com.fasterxml.jackson.databind.node.ObjectNode;
import park.sharkteam.game.messages.GameAction;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class GameSession {
    private static final AtomicInteger ID_GENERATOR = new AtomicInteger(0);

    @NotNull
    private final Game game;

    @NotNull
    private final Integer id;

    @NotNull
    private final ArrayList<Integer> userIds = new ArrayList<>(Config.PLAYERS_NUM);

    public GameSession(
            @NotNull Integer firstUserId,
            @NotNull Integer secondUserId,
            @NotNull GameSessionOrganizer gameSessionService
    ) {
        this.id = ID_GENERATOR.getAndIncrement();

        userIds.add(firstUserId);
        userIds.add(secondUserId);

        this.game = new Game();
    }

    public Integer getId() {
        return id;
    }

    public Integer getFirstUserId() {
       return userIds.get(0);
    }

    public Integer getSecondUserId() {
        return userIds.get(1);
    }

    public ArrayList<Integer> getUserIds() {
        return userIds;
    }

    public boolean hasPlayer(Integer userId) {
        return userIds.contains(userId);
    }

    public Game getGame() {
        return game;
    }

    public boolean isFinished() {
        return game.isFinished();
    }

    public Integer getAnotherPlayer(Integer userId) {
        if (Objects.equals(userId, userIds.get(0))) {
            return userIds.get(1);
        }
        if (Objects.equals(userId, userIds.get(1))) {
            return userIds.get(0);
        }
        return null;
    }

    public void handleMessage(Integer userId, GameAction message) {
        userId = userIds.indexOf(userId);
        if (userId != -1) {
            System.out.println("Action " + message.getAction() + " for user " + userId);
            switch (message.getAction()) {
                case Config.FIRE_ACTION:
                    game.shoot(userId);
                    break;
                case Config.DOWN_ACTION:
                case Config.UP_ACTION:
                    game.moveUser(userId, message.getAction());
                    break;
                default:
                    break;
            }
        }
    }

    public Integer getWinnerId() {
        return userIds.get(game.getWinerIndex());
    }

    public void update() {
        game.update();
    }

    public ObjectNode getGameStateMessageForUser(Integer userId) {
        if (hasPlayer(userId)) {
            int index = userIds.indexOf(userId);
            return game.getGameStateMessageForUser(index);
        }
        return null;
    }

}
