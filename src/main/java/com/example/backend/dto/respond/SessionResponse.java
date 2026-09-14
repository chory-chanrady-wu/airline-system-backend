package com.example.backend.dto.respond;

public record SessionResponse(
        boolean authenticated,
        UserResponse user
) {
}
