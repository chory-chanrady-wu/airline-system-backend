package com.example.backend.dto.request;

public record AuthLoginRequest(
        String email,
        String password
) {
}
