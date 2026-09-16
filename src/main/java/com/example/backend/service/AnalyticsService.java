package com.example.backend.service;

import java.util.Map;

public interface AnalyticsService {
    Map<String, Object> dashboard();

    Map<String, Object> bookingAnalytics();

    Map<String, Object> loadFactors();

    Map<String, Object> revenue();

    Map<String, Object> flightStatus();

    Map<String, Object> benchmarks();

    Map<String, Object> runBenchmark();
}

