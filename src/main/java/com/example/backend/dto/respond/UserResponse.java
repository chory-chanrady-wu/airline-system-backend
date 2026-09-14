package com.example.backend.dto.respond;

import java.time.LocalDateTime;

public record UserResponse(
        String id,
        String name,
        String email,
        String roleId,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
