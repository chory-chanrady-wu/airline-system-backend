package com.example.backend.controller;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/flights")
public class FlightController {

    @GetMapping
    public Map<String, Object> listFlights() {
        return ApiResponse.ok("Flights fetched", new Object[]{
                Map.of("flightId", "FL-1001", "from", "JFK", "to", "LHR", "status", "scheduled")
        });
    }

    @PostMapping
    public Map<String, Object> createFlight(@RequestBody Map<String, Object> payload) {
        return ApiResponse.created("Flight created", Map.of(
                "flightId", payload.getOrDefault("flightId", "FL-NEW"),
                "from", payload.getOrDefault("from", ""),
                "to", payload.getOrDefault("to", "")
        ));
    }

    @GetMapping("/{flightId}")
    public Map<String, Object> getFlight(@PathVariable String flightId) {
        return ApiResponse.ok("Flight fetched", Map.of(
                "flightId", flightId,
                "from", "JFK",
                "to", "LHR",
                "status", "scheduled"
        ));
    }

    @PatchMapping("/{flightId}")
    public Map<String, Object> updateFlight(@PathVariable String flightId, @RequestBody Map<String, Object> payload) {
        return ApiResponse.ok("Flight updated", Map.of(
                "flightId", flightId,
                "status", payload.getOrDefault("status", "scheduled")
        ));
    }

    @DeleteMapping("/{flightId}")
    public Map<String, Object> deleteFlight(@PathVariable String flightId) {
        return ApiResponse.ok("Flight deleted", Map.of("flightId", flightId));
    }

    @GetMapping("/search")
    public Map<String, Object> searchFlights(
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(required = false) String date) {
        return ApiResponse.ok("Flight search results", Map.of(
                "from", from,
                "to", to,
                "date", date,
                "items", new Object[]{
                        Map.of("flightId", "FL-1001", "from", from, "to", to)
                }
        ));
    }

    @GetMapping("/schedule")
    public Map<String, Object> getSchedule(
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String start,
            @RequestParam(required = false) String end) {
        return ApiResponse.ok("Flight schedule", Map.of(
                "from", from,
                "to", to,
                "date", date,
                "start", start,
                "end", end,
                "items", new Object[]{
                        Map.of("flightId", "FL-1001", "departure", "08:30")
                }
        ));
    }

    @GetMapping("/lookup/{flightId}")
    public Map<String, Object> lookupFlight(@PathVariable String flightId) {
        return ApiResponse.ok("Flight lookup succeeded", Map.of(
                "flightId", flightId,
                "status", "scheduled",
                "route", Map.of("from", "JFK", "to", "LHR")
        ));
    }

    @GetMapping("/{flightId}/waitlist")
    public Map<String, Object> getWaitlist(@PathVariable String flightId) {
        return ApiResponse.ok("Waitlist fetched", Map.of(
                "flightId", flightId,
                "items", new Object[]{
                        Map.of("bookingId", "BK-9001", "passengerId", 7, "status", "waiting")
                }
        ));
    }

    @PostMapping("/{flightId}/waitlist")
    public Map<String, Object> addToWaitlist(@PathVariable String flightId, @RequestBody Map<String, Object> payload) {
        return ApiResponse.created("Added to waitlist", Map.of(
                "flightId", flightId,
                "bookingId", payload.getOrDefault("bookingId", "BK-NEW"),
                "status", "waiting"
        ));
    }

    @PostMapping("/{flightId}/waitlist/promote")
    public Map<String, Object> promoteWaitlist(@PathVariable String flightId) {
        return ApiResponse.ok("Waitlist promoted", Map.of(
                "flightId", flightId,
                "promotedBookingId", "BK-9001",
                "status", "confirmed"
        ));
    }

    @DeleteMapping("/{flightId}/waitlist/{bookingId}")
    public Map<String, Object> removeFromWaitlist(@PathVariable String flightId, @PathVariable String bookingId) {
        return ApiResponse.ok("Removed from waitlist", Map.of(
                "flightId", flightId,
                "bookingId", bookingId
        ));
    }
}
