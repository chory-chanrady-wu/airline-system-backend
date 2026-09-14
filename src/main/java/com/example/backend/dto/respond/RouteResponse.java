package com.example.backend.dto.respond;

import java.time.LocalDateTime;

public record RouteResponse(
        String id,
        String fromAirportCode,
        String toAirportCode,
        Integer distanceKm,
        Integer durationMinutes,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
