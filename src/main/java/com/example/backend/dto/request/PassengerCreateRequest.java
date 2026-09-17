package com.example.backend.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import java.time.LocalDate;

public record PassengerCreateRequest(
        String userId,
        @JsonAlias({"name", "passengerName"}) String fullName,
        String passportNumber,
        String nationality,
        String phone,
        LocalDate dateOfBirth,
        String emergencyContact
) {
}
