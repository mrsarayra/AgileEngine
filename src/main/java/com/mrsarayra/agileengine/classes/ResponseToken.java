package com.mrsarayra.agileengine.classes;

import lombok.Data;

@Data
public final class ResponseToken {

    private boolean auth;
    private String token;

}
