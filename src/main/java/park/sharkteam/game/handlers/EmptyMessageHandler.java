package park.sharkteam.game.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import park.sharkteam.game.GameSessionOrganizer;
import park.sharkteam.game.messages.EmptyMessage;
import park.sharkteam.websocket.GameSocketService;
import park.sharkteam.websocket.MessageHandler;
import park.sharkteam.websocket.MessageHandlerContainer;

import javax.validation.constraints.NotNull;
import java.io.IOException;

@Component
public class EmptyMessageHandler extends MessageHandler<EmptyMessage> {

    @NotNull
    private MessageHandlerContainer messageHandlerContainer;

    @Autowired
    private GameSocketService gameSocketService;
    @Autowired
    private GameSessionOrganizer gameSessionService;

    public EmptyMessageHandler(@NotNull MessageHandlerContainer messageHandlerContainer) {
        super(EmptyMessage.class);
        this.messageHandlerContainer = messageHandlerContainer;
    }

    @Override
    public void handle(@NotNull EmptyMessage message, @NotNull Integer userId) {
        try {
            gameSocketService.sendMessageToUser(userId, new EmptyMessage());
        } catch (IOException e) {
            gameSessionService.handleUnexpectedEnding(userId);
        }
    }
}