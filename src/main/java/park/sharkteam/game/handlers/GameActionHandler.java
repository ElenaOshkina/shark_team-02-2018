package park.sharkteam.game.handlers;

import org.springframework.stereotype.Component;
import park.sharkteam.game.GameSessionOrganizer;
import park.sharkteam.game.messages.GameAction;
import park.sharkteam.websocket.MessageHandler;
import park.sharkteam.websocket.MessageHandlerContainer;


import javax.validation.constraints.NotNull;


@Component
public class GameActionHandler extends MessageHandler<GameAction> {
    @NotNull
    private GameSessionOrganizer gameService;


    public GameActionHandler(@NotNull GameSessionOrganizer gameService,
                              @NotNull MessageHandlerContainer messageHandlerContainer) {
        super(GameAction.class);
        this.gameService = gameService;

        messageHandlerContainer.registerHandler(GameAction.class, this);
    }


    @Override
    public void handle(@NotNull GameAction message, @NotNull Integer userId) {
        gameService.handleMessage(userId, message);
    }
}