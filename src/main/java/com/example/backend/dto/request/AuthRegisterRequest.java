package com.example.backend.dto.request;

public record AuthRegisterRequest(
        String name,
        String email,
        String password
) {
}
