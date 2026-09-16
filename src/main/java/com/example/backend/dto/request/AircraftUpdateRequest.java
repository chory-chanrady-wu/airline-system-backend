package com.example.backend.dto.request;

public record AircraftUpdateRequest(
        String registrationNumber,
        String model,
        Integer seatCapacity,
        Boolean active
) {
}

