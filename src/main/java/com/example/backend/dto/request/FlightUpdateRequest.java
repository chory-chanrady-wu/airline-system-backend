package com.example.backend.dto.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record FlightUpdateRequest(
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
        String status
) {
}
