package com.example.backend.controller;

import com.example.backend.service.AnalyticsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/dashboard")
    public Map<String, Object> dashboard() {
        return analyticsService.dashboard();
    }

    @GetMapping("/bookings")
    public Map<String, Object> bookingAnalytics() {
        return analyticsService.bookingAnalytics();
    }

    @GetMapping("/load-factors")
    public Map<String, Object> loadFactors() {
        return analyticsService.loadFactors();
    }

    @GetMapping("/revenue")
    public Map<String, Object> revenue() {
        return analyticsService.revenue();
    }

    @GetMapping("/flight-status")
    public Map<String, Object> flightStatus() {
        return analyticsService.flightStatus();
    }

    @GetMapping("/benchmarks")
    public Map<String, Object> benchmarks() {
        return analyticsService.benchmarks();
    }

    @PostMapping("/benchmarks/run")
    public Map<String, Object> runBenchmark() {
        return analyticsService.runBenchmark();
    }
}