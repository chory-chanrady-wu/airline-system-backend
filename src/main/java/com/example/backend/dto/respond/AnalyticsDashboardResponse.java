package com.example.backend.dto.respond;

import java.math.BigDecimal;

public record AnalyticsDashboardResponse(
        Long activeFlights,
        Long bookingsToday,
        BigDecimal revenue,
        Double averageLoadFactor
) {
}
