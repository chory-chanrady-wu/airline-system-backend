package com.example.backend.dto.respond;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BookingResponse(
        String id,
        String bookingReference,
        String passengerId,
        String flightId,
        String seatNumber,
        BigDecimal amount,
        String currency,
        String status,
        LocalDateTime bookedAt,
        LocalDateTime cancelledAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
