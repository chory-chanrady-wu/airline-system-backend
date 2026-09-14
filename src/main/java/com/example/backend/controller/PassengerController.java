package com.example.backend.controller;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class PassengerController {

    @GetMapping("/passengers")
    public Map<String, Object> listPassengers(@RequestParam(required = false) String search) {
        return ApiResponse.ok("Passengers fetched", Map.of(
                "search", search,
                "items", new Object[]{
                        Map.of("id", 1, "name", "Sophia Nguyen", "passport", "P12345")
                }
        ));
    }

    @PostMapping("/passengers")
    public Map<String, Object> createPassenger(@RequestBody Map<String, Object> payload) {
        return ApiResponse.created("Passenger created", Map.of(
                "id", 10,
                "name", payload.getOrDefault("name", ""),
                "passport", payload.getOrDefault("passport", "")
        ));
    }

    @GetMapping("/passengers/{id}")
    public Map<String, Object> getPassenger(@PathVariable long id) {
        return ApiResponse.ok("Passenger fetched", Map.of(
                "id", id,
                "name", "Sophia Nguyen",
                "passport", "P12345"
        ));
    }

    @PatchMapping("/passengers/{id}")
    public Map<String, Object> updatePassenger(@PathVariable long id, @RequestBody Map<String, Object> payload) {
        return ApiResponse.ok("Passenger updated", Map.of(
                "id", id,
                "name", payload.getOrDefault("name", "Sophia Nguyen")
        ));
    }

    @DeleteMapping("/passengers/{id}")
    public Map<String, Object> deletePassenger(@PathVariable long id) {
        return ApiResponse.ok("Passenger deleted", Map.of("id", id));
    }

    @GetMapping("/passengers/{passengerId}/bookings")
    public Map<String, Object> getPassengerBookings(@PathVariable long passengerId) {
        return ApiResponse.ok("Passenger bookings fetched", Map.of(
                "passengerId", passengerId,
                "items", new Object[]{
                        Map.of("bookingId", "BK-1001", "flightId", "FL-1001")
                }
        ));
    }

    @GetMapping("/passengers/{passengerId}/bookings/history")
    public Map<String, Object> getPassengerBookingHistory(@PathVariable long passengerId) {
        return ApiResponse.ok("Passenger booking history fetched", Map.of(
                "passengerId", passengerId,
                "items", new Object[]{
                        Map.of("bookingId", "BK-0999", "status", "completed")
                }
        ));
    }
}
