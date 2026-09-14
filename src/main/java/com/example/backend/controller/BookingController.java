package com.example.backend.controller;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class BookingController {

    @GetMapping("/bookings")
    public Map<String, Object> listBookings() {
        return ApiResponse.ok("Bookings fetched", new Object[]{
                Map.of("bookingId", "BK-1001", "flightId", "FL-1001", "status", "confirmed")
        });
    }

    @PostMapping("/bookings")
    public Map<String, Object> createBooking(@RequestBody Map<String, Object> payload) {
        return ApiResponse.created("Booking created", Map.of(
                "bookingId", payload.getOrDefault("bookingId", "BK-NEW"),
                "flightId", payload.getOrDefault("flightId", "FL-1001"),
                "status", "confirmed"
        ));
    }

    @GetMapping("/bookings/{bookingId}")
    public Map<String, Object> getBooking(@PathVariable String bookingId) {
        return ApiResponse.ok("Booking fetched", Map.of(
                "bookingId", bookingId,
                "flightId", "FL-1001",
                "status", "confirmed"
        ));
    }

    @PatchMapping("/bookings/{bookingId}")
    public Map<String, Object> updateBooking(@PathVariable String bookingId, @RequestBody Map<String, Object> payload) {
        return ApiResponse.ok("Booking updated", Map.of(
                "bookingId", bookingId,
                "status", payload.getOrDefault("status", "confirmed")
        ));
    }

    @DeleteMapping("/bookings/{bookingId}")
    public Map<String, Object> deleteBooking(@PathVariable String bookingId) {
        return ApiResponse.ok("Booking deleted", Map.of("bookingId", bookingId));
    }

    @PostMapping("/bookings/{bookingId}/cancel")
    public Map<String, Object> cancelBooking(@PathVariable String bookingId) {
        return ApiResponse.ok("Booking cancelled", Map.of("bookingId", bookingId, "status", "cancelled"));
    }

    @PostMapping("/bookings/{bookingId}/undo")
    public Map<String, Object> undoCancellation(@PathVariable String bookingId) {
        return ApiResponse.ok("Cancellation undone", Map.of("bookingId", bookingId, "status", "confirmed"));
    }
}
