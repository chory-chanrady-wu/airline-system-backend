package com.example.backend.dto.respond;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AirportResponse(
        String code,
        String city,
        String country,
        BigDecimal latitude,
        BigDecimal longitude,
        String timezone,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
