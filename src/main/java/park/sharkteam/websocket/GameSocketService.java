package park.sharkteam.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import park.sharkteam.game.GameSessionOrganizer;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GameSocketService {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameSocketService.class);
    private Map<Integer, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;
    private GameSessionOrganizer gameservice;

    @Autowired
    public GameSocketService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.gameservice = gameservice;
    }

    public void registerUser(Integer userId, WebSocketSession webSocketSession) {
        sessions.put(userId, webSocketSession);
        gameservice.addUser(userId);
    }

    public boolean isConnected(Integer userId) {
        return sessions.containsKey(userId) && sessions.get(userId).isOpen();
    }

    public void removeUser(Integer userId) {
        sessions.remove(userId);
    }

    public void closeConnection(Integer userId, CloseStatus closeStatus) {
        final WebSocketSession webSocketSession = sessions.get(userId);
        if (webSocketSession != null && webSocketSession.isOpen()) {
            try {
                webSocketSession.close(closeStatus);
            } catch (IOException e) {
                LOGGER.error("Can't close WebSocketSession");
            }
        }
    }

    @SuppressWarnings("OverlyBroadCatchBlock")
    public void sendMessageToUser(Integer userId, Message message) throws IOException {
        final WebSocketSession webSocketSession = sessions.get(userId);
        if (webSocketSession == null) {
            LOGGER.error("No websocket for user " + userId);
            throw new IOException("no websocket for user " + userId);
        }
        if (!webSocketSession.isOpen()) {
            LOGGER.error("Session with userId " + userId + " is closed or not exist");
            throw new IOException("session with userId " + userId + " is closed or not exist");
        }
        try {
            webSocketSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
        } catch (IOException e) {
            LOGGER.error("Can't send message ", e);
            throw new IOException("Can't send message ", e);
        }
    }

    @SuppressWarnings("OverlyBroadCatchBlock")
    public void sendMessageToUser(Integer userId, ObjectNode message) throws IOException {
        final WebSocketSession webSocketSession = sessions.get(userId);
        if (webSocketSession == null) {
            LOGGER.error("No websocket for user " + userId);
            throw new IOException("no websocket for user " + userId);
        }
        if (!webSocketSession.isOpen()) {
            LOGGER.error("Session with userId " + userId + " is closed or not exist");
            throw new IOException("session with userId " + userId + " is closed or not exist");
        }
        try {
            webSocketSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
        } catch (IOException e) {
            LOGGER.error("Can't send message ", e);
            throw new IOException("Can't send message ", e);
        }
    }
}
