package com.example.backend.dto.request;

public record RouteUpdateRequest(
        String fromAirportCode,
        String toAirportCode,
        Integer distanceKm,
        Integer durationMinutes,
        Boolean active
) {
}
