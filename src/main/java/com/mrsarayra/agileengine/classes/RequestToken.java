package com.mrsarayra.agileengine.classes;

import java.io.Serializable;

public final class RequestToken implements Serializable {

    private final String apiKey;


    public RequestToken(String apiKey) {
        this.apiKey = apiKey;
    }

}
