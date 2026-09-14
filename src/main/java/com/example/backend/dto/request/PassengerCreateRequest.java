package com.example.backend.dto.request;

import java.time.LocalDate;

public record PassengerCreateRequest(
        String userId,
        String passportNumber,
        String nationality,
        String phone,
        LocalDate dateOfBirth,
        String emergencyContact
) {
}
