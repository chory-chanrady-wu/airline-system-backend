package com.example.backend.controller;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ApiResponse {

    private ApiResponse() {
    }

    public static Map<String, Object> ok(String message, Object data) {
        return build(200, "OK", message, data);
    }

    public static Map<String, Object> created(String message, Object data) {
        return build(201, "Created", message, data);
    }

    public static Map<String, Object> badRequest(String message) {
        return build(400, "Bad Request", message, null);
    }
    public static Map<String, Object> ok(String message) {
        return build(200, "OK", message, null);
    }

    private static Map<String, Object> build(int status, String error, String message, Object data) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", status);
        body.put("error", error);
        body.put("message", message);
        if (data != null) {
            body.put("data", data);
        }
        return body;
    }
}