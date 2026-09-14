package com.example.backend.dto.request;

public record RouteCreateRequest(
        String fromAirportCode,
        String toAirportCode,
        Integer distanceKm,
        Integer durationMinutes,
        Boolean active
) {
}
