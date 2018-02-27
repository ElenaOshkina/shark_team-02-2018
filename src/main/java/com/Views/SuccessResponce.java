package com.views;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Created by Alex on 26.02.2018.
 */
public class SuccessResponce {
    @JsonProperty("success")
    private final Boolean success = true;
    @JsonProperty("msg")
    private final String message;

    @JsonCreator
    public SuccessResponce(@JsonProperty("msg") String msg) {
        this.message = msg;
    }

}
