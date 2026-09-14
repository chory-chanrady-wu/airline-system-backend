package com.example.backend.service;

import java.util.Locale;

public final class EntityLookupSupport {

    private EntityLookupSupport() {
    }

    public static Integer parseInteger(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Integer.valueOf(value.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    public static String safe(String value) {
        return value == null ? "" : value;
    }

    public static String normalized(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    public static <E extends Enum<E>> E parseEnum(Class<E> enumType, String value, E fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        String normalized = value.trim().replace('-', '_').replace(' ', '_');
        for (E candidate : enumType.getEnumConstants()) {
            if (candidate.name().equalsIgnoreCase(normalized)) {
                return candidate;
            }
        }
        return fallback;
    }
}

