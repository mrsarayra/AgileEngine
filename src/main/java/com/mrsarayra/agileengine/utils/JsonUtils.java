package com.mrsarayra.agileengine.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

@Slf4j
public final class JsonUtils {

    private static Gson gson;

    static {
        gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public static <T> T jsonToObj(String json, Class<T> type) {
        Assert.hasText(json, "Json cannot be blank");
        Assert.notNull(type, "Type cannot be null");
        try {
            return gson.fromJson(json, type);
        } catch (JsonSyntaxException e) {
            log.error("Cannot JSONfy string: {}", e.getMessage());
        }
        return null;
    }

}
