package com.example.backend.dto.respond;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record FlightResponse(
        String id,
        String flightNumber,
        String airlineId,
        String aircraftId,
        String routeId,
        String fromAirportCode,
        String toAirportCode,
        LocalDateTime departureTime,
        LocalDateTime arrivalTime,
        BigDecimal price,
        Integer seatCapacity,
        Integer seatsAvailable,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
