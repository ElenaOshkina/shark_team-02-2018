package park.sharkteam.websocket;

import park.sharkteam.game.messages.*;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "class")
@JsonSubTypes({
        @JsonSubTypes.Type(value = JoinGameMessage.class, name = "JoinGameMessage"),
        @JsonSubTypes.Type(value = InitGameMessage.class, name = "InitGameMessage"),
        @JsonSubTypes.Type(value = FinishGameMessage.class, name = "FinishGameMessage"),
        @JsonSubTypes.Type(value = GameAction.class, name = "GameAction"),
        @JsonSubTypes.Type(value = EmptyMessage.class, name = "EmptyMessage"),
})
public abstract class Message {
}
