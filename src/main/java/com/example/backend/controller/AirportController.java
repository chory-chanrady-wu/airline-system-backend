package com.example.backend.controller;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class AirportController {

    @GetMapping("/airports")
    public Map<String, Object> listAirports() {
        return ApiResponse.ok("Airports fetched", new Object[]{
                Map.of("code", "JFK", "name", "John F. Kennedy International Airport"),
                Map.of("code", "LHR", "name", "London Heathrow Airport")
        });
    }

    @PostMapping("/airports")
    public Map<String, Object> createAirport(@RequestBody Map<String, Object> payload) {
        return ApiResponse.created("Airport created", Map.of(
                "code", payload.getOrDefault("code", ""),
                "name", payload.getOrDefault("name", "")
        ));
    }

    @GetMapping("/airports/{code}")
    public Map<String, Object> getAirport(@PathVariable String code) {
        return ApiResponse.ok("Airport fetched", Map.of(
                "code", code,
                "name", "Airport " + code,
                "city", "Unknown"
        ));
    }

    @PatchMapping("/airports/{code}")
    public Map<String, Object> updateAirport(@PathVariable String code, @RequestBody Map<String, Object> payload) {
        return ApiResponse.ok("Airport updated", Map.of(
                "code", code,
                "name", payload.getOrDefault("name", "Airport " + code)
        ));
    }

    @DeleteMapping("/airports/{code}")
    public Map<String, Object> deleteAirport(@PathVariable String code) {
        return ApiResponse.ok("Airport deleted", Map.of("code", code));
    }
}
