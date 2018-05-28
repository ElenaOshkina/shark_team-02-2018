package park.sharkteam.game;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import park.sharkteam.game.messages.*;
import park.sharkteam.services.UserService;
import park.sharkteam.websocket.GameSocketService;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class GameSessionOrganizer {

    @Autowired
    private GameSocketService gameSocketService;
    @Autowired
    private UserService userService;

    class GamesExecuter implements Runnable {
        @Override
        public void run() {
            try {
                updateGames();
            } finally {
                LOGGER.warn("Mechanic executor terminated");
            }
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(GameSessionOrganizer.class);

    private ConcurrentLinkedQueue<Integer> waiters = new ConcurrentLinkedQueue<>();
    private final CopyOnWriteArrayList<GameSession> gameSessions = new CopyOnWriteArrayList<>();

    private static final Long FRAME_TIME = 100L;

    public GameSessionOrganizer() {
        new Thread(new GamesExecuter()).start();
    }

    public boolean isPlaying(Integer userId) {
        final Optional<GameSession>  gameSession = gameSessions.stream().filter(game -> game.hasPlayer(userId)).findFirst();
        return gameSession.isPresent();
    }

    public boolean checkConnection(@NotNull GameSession gameSession) {
        return gameSocketService.isConnected(gameSession.getFirstUserId())
                && gameSocketService.isConnected(gameSession.getSecondUserId());
    }

    public void handleMessage(Integer userId, GameAction message) {
        final Optional<GameSession>  gameSession = gameSessions.stream().filter(game -> game.hasPlayer(userId)).findFirst();
        gameSession.ifPresent(game -> game.handleMessage(userId, message));
    }

    public void addUser(@NotNull Integer userId) {
        if (isPlaying(userId) || waiters.contains(userId)) {
            return;
        }
        waiters.add(userId);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("User with id " + userId + " added to the waiting list");
        }
        tryToStartGame();
    }

    public void tryToStartGame() {
        final Set<Integer> possiblePlayers = new LinkedHashSet<>();
        while (waiters.size() >= 2 || (waiters.size() >= 1 && possiblePlayers.size() >= 1)) {
            final Integer candidate = waiters.poll();
            if (!gameSocketService.isConnected(candidate)) {
                continue;
            }
            possiblePlayers.add(candidate);
            if (possiblePlayers.size() == 2) {
                final Iterator<Integer> iterator = possiblePlayers.iterator();
                startGame(iterator.next(), iterator.next());
                possiblePlayers.clear();
            }
        }
        waiters.addAll(possiblePlayers);
    }

    public void startGame(@NotNull Integer firstUserId, @NotNull Integer secondUserId) {

        final GameSession newGame = new GameSession(firstUserId, secondUserId, this);

        try {
            final InitGameMessage initMessage1 = new InitGameMessage(
                    secondUserId,
                    userService.getUserById(secondUserId).getLogin()
            );
            gameSocketService.sendMessageToUser(
                    firstUserId,
                    initMessage1
            );

            final InitGameMessage initMessage2 = new InitGameMessage(
                    firstUserId,
                    userService.getUserById(firstUserId).getLogin()
            );
            gameSocketService.sendMessageToUser(secondUserId, initMessage2);
            gameSessions.add(newGame);

            LOGGER.info("Game " + newGame.getId() + " started. Players:" + firstUserId + ", " + secondUserId);

        } catch (IOException e) {
            gameSocketService.closeConnection(firstUserId, CloseStatus.SERVER_ERROR);
            gameSocketService.closeConnection(secondUserId, CloseStatus.SERVER_ERROR);
            LOGGER.error("Can't start a game for users " + firstUserId + ", " + secondUserId, e);
        }
    }


    public void finishGame(@NotNull GameSession game) {
        final FinishGameMessage finishGameMessageMessage = new FinishGameMessage();

        Integer winnerId = game.getWinnerId();
        for (Integer id :  game.getUserIds()) {
            try {
                finishGameMessageMessage.setWon(id == winnerId);
                gameSocketService.sendMessageToUser(id, finishGameMessageMessage);
                gameSocketService.closeConnection(id, CloseStatus.NORMAL);
            } catch (IOException e) {
                LOGGER.warn("Failed to send FinishGameMessage to user " + id, e);
            }
        }

        for (Integer id :  game.getUserIds()) {
            userService.updateScore(id, id == winnerId ? 1 : -1);
        }
    }

    public void  handleUnexpectedEnding(@NotNull Integer userId) {
        final Optional<GameSession>  gameSession = gameSessions.stream().filter(game -> game.hasPlayer(userId)).findFirst();
        gameSession.ifPresent(session -> handleUnexpectedEnding(session));
    }

    public void handleUnexpectedEnding(@NotNull GameSession session) {
        final FinishGameMessage message = new FinishGameMessage();
        session.getUserIds();
        if (session == null) {
            LOGGER.info("GameSession was already closed");
            return;
        }

        gameSessions.remove(session);

        message.setWon(true);
        Integer winnerId = -1;

        for (Integer id : session.getUserIds()) {
           if (gameSocketService.isConnected(id)) {
               try {
                   gameSocketService.sendMessageToUser(id, message);
                   winnerId = id;
               } catch (IOException e) {
                   LOGGER.warn("Failed to send FinishGameMessage to user " + id, e);
               }
           }
        }

        for (Integer id : session.getUserIds()) {
            gameSocketService.closeConnection(id, CloseStatus.NORMAL);
        }

        LOGGER.info("Game session with users: " + session.getUserIds() + " was force terminated! ");

    }

    private void updateGames() {
        while (true) {
            try {
                final Long before = new Date().getTime();
                final Iterator<GameSession> gameIter = gameSessions.iterator();
                while (gameIter.hasNext()) {
                    final GameSession game = gameIter.next();
                    try {
                        game.update();
                        if (game.isFinished()) {
                            finishGame(game);
                            gameIter.remove();
                        } else {
                            ArrayList<Integer> userIds = game.getUserIds();
                            for (Integer userId : userIds) {
                                gameSocketService.sendMessageToUser(userId, game.getGameStateMessageForUser(userId));
                            }
                        }
                    } catch (RuntimeException e) {
                        LOGGER.error("The game emergency stoped", e);
                        try {
                            handleUnexpectedEnding(game);
                        } catch (RuntimeException ignored) {
                            LOGGER.error("The game emergency stoped", e);
                        }
                        gameSessions.remove(game);
                    }
                }

                final Long after = new Date().getTime();

                try {
                    Long sleepingTime = FRAME_TIME - (after - before);
                    if (sleepingTime <= 0) {
                        sleepingTime = FRAME_TIME;
                    }
                    Thread.sleep(sleepingTime);
                } catch (InterruptedException e) {
                    LOGGER.error("Mechanics thread was interrupted", e);
                }
            } catch (Exception e) {
                LOGGER.error("Mechanics executor was reseted due to exception", e);
                gameSessions.clear();
                waiters.clear();
            }
        }
    }
}