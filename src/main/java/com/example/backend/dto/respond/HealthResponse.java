package com.example.backend.dto.respond;

import java.time.LocalDateTime;

public record HealthResponse(
        String status,
        LocalDateTime timestamp,
        String version
) {
}
