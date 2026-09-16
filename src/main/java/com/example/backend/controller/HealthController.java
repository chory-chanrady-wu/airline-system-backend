package com.example.backend.controller;

import com.example.backend.service.HealthService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class HealthController {

    private final HealthService healthService;

    public HealthController(HealthService healthService) {
        this.healthService = healthService;
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        return healthService.health();
    }

    @GetMapping("/health/database")
    public Map<String, Object> databaseHealth() {
        return healthService.databaseHealth();
    }

    @GetMapping("/health/providers")
    public Map<String, Object> providerHealth() {
        return healthService.providerHealth();
    }
}
