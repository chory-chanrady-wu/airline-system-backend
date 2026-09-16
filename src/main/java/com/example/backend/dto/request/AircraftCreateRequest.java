package com.example.backend.dto.request;

public record AircraftCreateRequest(
        String registrationNumber,
        String model,
        Integer seatCapacity,
        Boolean active
) {
}

