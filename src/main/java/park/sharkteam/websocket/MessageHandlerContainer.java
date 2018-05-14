package park.sharkteam.websocket;

import javax.validation.constraints.NotNull;

public interface MessageHandlerContainer {
    void handle(@NotNull Message message, @NotNull Integer userId) throws HandleExeption;

    <T extends Message> void registerHandler(@NotNull Class<T> myClass, MessageHandler<T> handler);
}
