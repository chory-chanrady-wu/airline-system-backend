package com.example.backend.dto.respond;

import java.util.List;

public record RouteOptimizationResponse(
        String from,
        String to,
        String type,
        List<RouteSegment> path
) {
    public record RouteSegment(
            String from,
            String to,
            String airlineCode,
            Integer durationMinutes,
            java.math.BigDecimal price
    ) {
    }
}
