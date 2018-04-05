package park.sharkteam.views.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Created by Alex on 26.02.2018.
 */
public class SuccessResponse {
    @JsonProperty("success")
    private final Boolean success = true;
    @JsonProperty("msg")
    private final String message;

    @JsonCreator
    public SuccessResponse(@JsonProperty("msg") String msg) {
        this.message = msg;
    }

}
