package com.fengruigan.androidbuilder;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HandlerOutput {

    @JsonProperty
    private int exitCode;
    @JsonProperty
    private String message;

    public HandlerOutput(int exitCode, String message) {
        this.exitCode = exitCode;
        this.message = message;
    }

    public int getExitCode() {
        return exitCode;
    }

    public String getMessage() {
        return message;
    }
}
