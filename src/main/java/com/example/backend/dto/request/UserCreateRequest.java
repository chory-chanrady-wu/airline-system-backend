package com.example.backend.dto.request;

public record UserCreateRequest(
        String name,
        String email,
        String password,
        String roleId,
        String status
) {
}
