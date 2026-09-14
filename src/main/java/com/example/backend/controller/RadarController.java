package com.example.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/flights")
public class RadarController {

    @GetMapping("/radar")
    public Map<String, Object> radar() {
        return ApiResponse.ok("Radar data fetched", new Object[]{
                Map.of("flightId", "FL-1001", "altitude", 35000, "status", "tracking")
        });
    }

    @GetMapping("/radar/status")
    public Map<String, Object> radarStatus() {
        return ApiResponse.ok("Radar provider status", Map.of(
                "provider", "ADS-B",
                "status", "online",
                "refreshIntervalSeconds", 30
        ));
    }

    @GetMapping("/radar/{flightId}")
    public Map<String, Object> flightRadar(@PathVariable String flightId) {
        return ApiResponse.ok("Flight radar data fetched", Map.of(
                "flightId", flightId,
                "latitude", 40.6413,
                "longitude", -73.7781,
                "heading", 245,
                "speedKts", 510
        ));
    }
}
