package park.sharkteam.game.handlers;

import org.springframework.stereotype.Component;
import park.sharkteam.game.messages.EmptyMessage;
import park.sharkteam.websocket.MessageHandler;
import park.sharkteam.websocket.MessageHandlerContainer;

import javax.validation.constraints.NotNull;

@Component
public class EmptyMessageHandler extends MessageHandler<EmptyMessage> {

    @NotNull
    private MessageHandlerContainer messageHandlerContainer;

    public EmptyMessageHandler(@NotNull MessageHandlerContainer messageHandlerContainer) {
        super(EmptyMessage.class);
        this.messageHandlerContainer = messageHandlerContainer;
    }

    @Override
    public void handle(@NotNull EmptyMessage message, @NotNull Integer userId) {

    }
}