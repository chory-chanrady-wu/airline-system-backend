package com.example.backend.dto.request;

public record UserPasswordUpdateRequest(
        String currentPassword,
        String newPassword
) {
}
