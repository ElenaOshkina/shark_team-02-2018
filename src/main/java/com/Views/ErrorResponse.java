package com.Views;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotNull;

import com.Utilities.ErrorCoder;

/**
 * Created by Alex on 25.02.2018.
 */
public class ErrorResponse {

    @JsonProperty("success")
    private final Boolean success = false;
    @JsonProperty("errorId")
    private final Integer errorId;
    @JsonProperty("description")
    private final String description;

    @JsonCreator
    public ErrorResponse(@NotNull Integer code, String msg) {
        this.errorId = code;
        this.description = msg;
    }

    public ErrorResponse(ErrorCoder error) {
        this.errorId = error.getCode();
        this.description = error.getMsg();
    }

    @SuppressWarnings("unused")
    public Integer getErrorId() {
        return errorId;
    }

    @SuppressWarnings("unused")
    public String getDescription() {
        return description;
    }
}
