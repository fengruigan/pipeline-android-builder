package com.fengruigan.androidbuilder;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class HandlerInput {

    @JsonProperty
    private Map<String, String> env;
    @JsonProperty
    private Map<String, String> data;

    public Map<String, String> getEnv() {
        return env;
    }

    public void setEnv(Map<String, String> env) {
        this.env = env;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }
}
