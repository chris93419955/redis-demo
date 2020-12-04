package com.auto.cyq.redis.demo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.type.TypeFactory;
import lombok.SneakyThrows;

import java.text.SimpleDateFormat;

public class JacksonHelper {

    private static ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);

        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"));
    }

    @SneakyThrows
    public static String serialize(Object o) {
        return mapper.writeValueAsString(o);
    }


    @SneakyThrows
    public static <T> T deserialize(String s, Class<T> clazz) {
        if (s == null || s.length() == 0)
            return null;


        T t = mapper.readValue(s, clazz);
        return t;

    }

    @SneakyThrows
    public static <T> T deserialize(String s, JavaType javaType) {
        if (s == null || s.length() == 0)
            return null;

        T t = mapper.readValue(s, javaType);
        return t;
    }

    @SneakyThrows
    public static <T> T deserialize(String s, TypeReference clazz) {

        if (s == null || s.length() == 0)
            return null;

        T t = (T) mapper.readValue(s, clazz);
        return t;
    }

    @SneakyThrows
    public static JsonNode deserialize(String s) {
        if (s == null || s.length() == 0)
            return null;

        JsonNode t = mapper.readTree(s);
        return t;
    }

    public static TypeFactory getTypeFactory() {
        return mapper.getTypeFactory();
    }
}