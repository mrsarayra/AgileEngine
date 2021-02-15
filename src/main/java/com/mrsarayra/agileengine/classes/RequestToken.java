package com.mrsarayra.agileengine.classes;

import lombok.Getter;

import java.io.Serializable;

@Getter
public final class RequestToken implements Serializable {

    private final String apiKey;


    public RequestToken(String apiKey) {
        this.apiKey = apiKey;
    }

}
