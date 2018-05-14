package park.sharkteam.websocket;

import park.sharkteam.game.messages.*;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "class")
@JsonSubTypes({
        @JsonSubTypes.Type(JoinGameMessage.class),
        @JsonSubTypes.Type(InitGameMessage.class),
        @JsonSubTypes.Type(FinishGameMessage.class),
        //ToDo game messages
})
public abstract class Message {
}
