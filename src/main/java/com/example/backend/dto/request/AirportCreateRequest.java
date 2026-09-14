package com.example.backend.dto.request;

import java.math.BigDecimal;

public record AirportCreateRequest(
        String code,
        String city,
        String country,
        BigDecimal latitude,
        BigDecimal longitude,
        String timezone
) {
}
