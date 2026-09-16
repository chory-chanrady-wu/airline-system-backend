package com.example.backend.service;

import java.util.Map;

public interface HealthService {
    Map<String, Object> health();

    Map<String, Object> databaseHealth();

    Map<String, Object> providerHealth();
}

