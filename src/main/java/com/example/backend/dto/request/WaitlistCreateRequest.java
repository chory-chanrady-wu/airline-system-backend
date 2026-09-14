package com.example.backend.dto.request;

public record WaitlistCreateRequest(
        String passengerId,
        String bookingId
) {
}
