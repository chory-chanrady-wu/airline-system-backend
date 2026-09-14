package com.example.backend.dto.respond;

public record RadarStatusResponse(
        String provider,
        String status,
        Integer refreshIntervalSeconds
) {
}
