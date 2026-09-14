package com.example.backend.dto.request;

import java.util.List;

public record RoleUpdateRequest(
        String name,
        String description,
        List<String> permissions
) {
}
