package com.example.backend.controller;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/analytics")
public class AnalyticsController {

    @GetMapping("/dashboard")
    public Map<String, Object> dashboard() {
        return ApiResponse.ok("Dashboard analytics fetched", Map.of(
                "activeFlights", 218,
                "bookingsToday", 164,
                "revenue", 152000.0
        ));
    }

    @GetMapping("/bookings")
    public Map<String, Object> bookingAnalytics() {
        return ApiResponse.ok("Booking analytics fetched", Map.of("bookings", 1240, "confirmed", 1100));
    }

    @GetMapping("/load-factors")
    public Map<String, Object> loadFactors() {
        return ApiResponse.ok("Load factor analytics fetched", Map.of("averageLoadFactor", 78.4));
    }

    @GetMapping("/revenue")
    public Map<String, Object> revenue() {
        return ApiResponse.ok("Revenue analytics fetched", Map.of("totalRevenue", 842300.0));
    }

    @GetMapping("/flight-status")
    public Map<String, Object> flightStatus() {
        return ApiResponse.ok("Flight status analytics fetched", new Object[]{
                Map.of("flightId", "FL-1001", "status", "on-time")
        });
    }

    @GetMapping("/benchmarks")
    public Map<String, Object> benchmarks() {
        return ApiResponse.ok("Benchmark report fetched", Map.of("averageDelayMinutes", 12));
    }

    @PostMapping("/benchmarks/run")
    public Map<String, Object> runBenchmark() {
        return ApiResponse.ok("Benchmark run started", Map.of("jobId", "BM-001", "status", "running"));
    }
}
