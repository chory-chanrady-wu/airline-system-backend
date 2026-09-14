package com.example.backend.controller;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/routes")
public class RouteController {

    @GetMapping
    public Map<String, Object> listRoutes() {
        return ApiResponse.ok("Routes fetched", new Object[]{
                Map.of("from", "JFK", "to", "LHR", "distanceKm", 5570),
                Map.of("from", "JFK", "to", "NRT", "distanceKm", 10863)
        });
    }

    @PostMapping
    public Map<String, Object> createRoute(@RequestBody Map<String, Object> payload) {
        return ApiResponse.created("Route created", Map.of(
                "from", payload.getOrDefault("from", ""),
                "to", payload.getOrDefault("to", ""),
                "distanceKm", payload.getOrDefault("distanceKm", 0)
        ));
    }

    @GetMapping("/{from}/{to}")
    public Map<String, Object> getRoute(@PathVariable String from, @PathVariable String to) {
        return ApiResponse.ok("Route fetched", Map.of(
                "from", from,
                "to", to,
                "distanceKm", 5570,
                "durationMinutes", 420
        ));
    }

    @PatchMapping("/{from}/{to}")
    public Map<String, Object> updateRoute(@PathVariable String from, @PathVariable String to, @RequestBody Map<String, Object> payload) {
        return ApiResponse.ok("Route updated", Map.of(
                "from", from,
                "to", to,
                "distanceKm", payload.getOrDefault("distanceKm", 5570)
        ));
    }

    @DeleteMapping("/{from}/{to}")
    public Map<String, Object> deleteRoute(@PathVariable String from, @PathVariable String to) {
        return ApiResponse.ok("Route deleted", Map.of("from", from, "to", to));
    }

    @GetMapping("/{from}/{to}/distance")
    public Map<String, Object> getDistance(@PathVariable String from, @PathVariable String to) {
        return ApiResponse.ok("Distance fetched", Map.of(
                "from", from,
                "to", to,
                "distanceKm", 5570,
                "unit", "km"
        ));
    }

    @GetMapping("/optimize")
    public Map<String, Object> optimizeRoute(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam(defaultValue = "cheapest") String type) {
        return ApiResponse.ok("Route optimization computed", Map.of(
                "from", from,
                "to", to,
                "type", type,
                "path", new Object[]{
                        Map.of("from", from, "to", "LHR"),
                        Map.of("from", "LHR", "to", to)
                }
        ));
    }
}
