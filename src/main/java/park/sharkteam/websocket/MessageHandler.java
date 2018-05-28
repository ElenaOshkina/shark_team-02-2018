package park.sharkteam.websocket;

import javax.validation.constraints.NotNull;

public abstract class MessageHandler<T extends Message> {
    @NotNull
    private final Class<T> messageClass;

    public MessageHandler(@NotNull Class<T> myClass) {
        this.messageClass = myClass;
    }

    public void handleMessage(@NotNull Message message, @NotNull Integer userId) throws HandleExeption {
        try {
            handle(messageClass.cast(message), userId);
        } catch (ClassCastException ex) {
            throw new HandleExeption("Can't reed incoming message of type " + message.getClass(), ex);
        }
    }

    public abstract void handle(@NotNull T message, @NotNull Integer userId) throws HandleExeption;
}
