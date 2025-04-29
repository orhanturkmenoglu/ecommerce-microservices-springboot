package com.example.auth_service.utils;


import java.util.UUID;

public class IdGenerator {

    public static String generateId(Object entity) {
        Prefix prefixAnnotation = entity.getClass().getAnnotation(Prefix.class);

        if (prefixAnnotation == null) {
            throw new IllegalArgumentException("Prefix annotation is missing on the entity class.");
        }

        String prefix = prefixAnnotation.value();
        return prefix + "-" + UUID.randomUUID();
    }
}
