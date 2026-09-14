package com.example.backend.dto.request;

import java.time.LocalDate;

public record PassengerUpdateRequest(
        String passportNumber,
        String nationality,
        String phone,
        LocalDate dateOfBirth,
        String emergencyContact
) {
}
