package park.sharkteam.game.handlers;

import org.springframework.stereotype.Component;
import park.sharkteam.game.GameSessionOrganizer;
import park.sharkteam.game.messages.JoinGameMessage;
import park.sharkteam.websocket.MessageHandler;
import park.sharkteam.websocket.MessageHandlerContainer;

import javax.validation.constraints.NotNull;

@Component
public class JoinGameHandler extends MessageHandler<JoinGameMessage> {
    @NotNull
    private GameSessionOrganizer gameService;

    @NotNull
    private MessageHandlerContainer messageHandlerContainer;

    public JoinGameHandler(@NotNull GameSessionOrganizer gameService,
                             @NotNull MessageHandlerContainer messageHandlerContainer) {
        super(JoinGameMessage.class);
        this.gameService = gameService;
        this.messageHandlerContainer = messageHandlerContainer;

        messageHandlerContainer.registerHandler(JoinGameMessage.class, this);
    }

    @Override
    public void handle(@NotNull JoinGameMessage message, @NotNull Integer userId) {
        gameService.addUser(userId);
    }
}