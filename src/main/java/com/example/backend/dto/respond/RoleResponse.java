package com.example.backend.dto.respond;

import java.time.LocalDateTime;
import java.util.List;

public record RoleResponse(
        String id,
        String name,
        String description,
        List<String> permissions,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
