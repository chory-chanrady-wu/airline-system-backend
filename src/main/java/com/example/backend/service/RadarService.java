package com.example.backend.service;

import java.util.Map;

public interface RadarService {
    Map<String, Object> radar();

    Map<String, Object> radarStatus();

    Map<String, Object> flightRadar(String flightId);
}

