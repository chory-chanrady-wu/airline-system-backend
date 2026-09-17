package com.example.backend.dto.request;

import java.math.BigDecimal;

public record BookingCreateRequest(
        String passengerId,
        String flightId,
        String passengerName,
        String flightNumber,
        String seatNumber,
        BigDecimal amount,
        String currency,
        String status
) {
}
