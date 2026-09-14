package com.example.backend.dto.respond;

import java.time.LocalDateTime;

public record WaitlistResponse(
        String id,
        String flightId,
        String passengerId,
        String bookingId,
        Integer position,
        String status,
        LocalDateTime joinedAt,
        LocalDateTime offeredAt,
        LocalDateTime expiresAt
) {
}
