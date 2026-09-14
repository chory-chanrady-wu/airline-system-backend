package com.example.backend.dto.request;

public record UserUpdateRequest(
        String name,
        String email,
        String roleId,
        String status
) {
}
