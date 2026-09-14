package com.example.backend.dto.respond;

import java.util.List;

public record FlightSearchResponse(
        String from,
        String to,
        String date,
        List<FlightResponse> items
) {
}
