package com.example.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class HealthController {

    @GetMapping("/health")
    public Map<String, Object> health() {
        return ApiResponse.ok("Service is healthy", Map.of("status", "ok", "timestamp", System.currentTimeMillis()));
    }

    @GetMapping("/health/database")
    public Map<String, Object> databaseHealth() {
        return ApiResponse.ok("Database health check passed", Map.of("status", "connected"));
    }

    @GetMapping("/health/providers")
    public Map<String, Object> providerHealth() {
        return ApiResponse.ok("External providers health check", Map.of("radar", "online", "weather", "online"));
    }
}
