package com.example.backend.dto.respond;

public record AuthResponse(
        String token,
        String email,
        String name,
        boolean authenticated
) {
}
