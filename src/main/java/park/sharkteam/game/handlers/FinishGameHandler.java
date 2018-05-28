package park.sharkteam.game.handlers;

import org.springframework.stereotype.Component;
import park.sharkteam.game.GameSessionOrganizer;
import park.sharkteam.game.messages.FinishGameMessage;
import park.sharkteam.websocket.MessageHandler;
import park.sharkteam.websocket.MessageHandlerContainer;
import javax.validation.constraints.NotNull;

@Component
public class FinishGameHandler extends MessageHandler<FinishGameMessage> {

    @NotNull
    private GameSessionOrganizer gameSessionService;


    public FinishGameHandler(@NotNull GameSessionOrganizer gameSessionService,
                             @NotNull MessageHandlerContainer messageHandlerContainer
    ) {
        super(FinishGameMessage.class);
        this.gameSessionService = gameSessionService;

        messageHandlerContainer.registerHandler(FinishGameMessage.class, this);
    }

    @Override
    public void handle(@NotNull FinishGameMessage message, @NotNull Integer userId) {
        gameSessionService.handleUnexpectedEnding(userId);
    }
}