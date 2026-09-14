package com.example.backend.dto.respond;

import java.time.LocalDateTime;

public record BenchmarkResultResponse(
        String id,
        String structure,
        String operation,
        Long inputSize,
        Long runtimeMilliseconds,
        String theoreticalComplexity,
        LocalDateTime executedAt
) {
}
