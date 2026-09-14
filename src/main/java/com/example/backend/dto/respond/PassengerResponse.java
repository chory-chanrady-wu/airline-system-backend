package com.example.backend.dto.respond;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record PassengerResponse(
        String id,
        String userId,
        String passportNumber,
        String nationality,
        String phone,
        LocalDate dateOfBirth,
        String emergencyContact,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
