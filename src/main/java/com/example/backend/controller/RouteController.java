package com.example.backend.controller;

import com.example.backend.dto.request.RouteCreateRequest;
import com.example.backend.dto.request.RouteUpdateRequest;
import com.example.backend.service.RouteService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/routes")
public class RouteController {

    private final RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @GetMapping
    public Map<String, Object> listRoutes() {
        return routeService.listRoutes();
    }

    @PostMapping
    public Map<String, Object> createRoute(@RequestBody RouteCreateRequest request) {
        return routeService.createRoute(request);
    }

    @GetMapping("/{from}/{to}")
    public Map<String, Object> getRoute(@PathVariable String from, @PathVariable String to) {
        return routeService.getRoute(from, to);
    }

    @PatchMapping("/{from}/{to}")
    public Map<String, Object> updateRoute(@PathVariable String from, @PathVariable String to, @RequestBody RouteUpdateRequest request) {
        return routeService.updateRoute(from, to, request);
    }

    @DeleteMapping("/{from}/{to}")
    public Map<String, Object> deleteRoute(@PathVariable String from, @PathVariable String to) {
        return routeService.deleteRoute(from, to);
    }

    @GetMapping("/{from}/{to}/distance")
    public Map<String, Object> getDistance(@PathVariable String from, @PathVariable String to) {
        return routeService.getDistance(from, to);
    }

    @GetMapping("/optimize")
    public Map<String, Object> optimizeRoute(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam(defaultValue = "cheapest") String type) {
        return routeService.optimizeRoute(from, to, type);
    }
}
