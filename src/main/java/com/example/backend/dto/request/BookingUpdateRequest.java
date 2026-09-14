package com.example.backend.dto.request;

public record BookingUpdateRequest(
        String seatNumber,
        String status
) {
}
