package com.example.backend.controller;

import com.example.backend.dto.request.FlightCreateRequest;
import com.example.backend.dto.request.FlightUpdateRequest;
import com.example.backend.dto.request.WaitlistCreateRequest;
import com.example.backend.dto.request.WaitlistPromoteRequest;
import com.example.backend.service.FlightService;
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
@RequestMapping("/api/v1/flights")
public class FlightController {

    private final FlightService flightService;

    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    @GetMapping
    public Map<String, Object> listFlights() {
        return flightService.listFlights();
    }

    @PostMapping
    public Map<String, Object> createFlight(@RequestBody FlightCreateRequest request) {
        return flightService.createFlight(request);
    }

    @GetMapping("/{flightId}")
    public Map<String, Object> getFlight(@PathVariable String flightId) {
        return flightService.getFlight(flightId);
    }

    @PatchMapping("/{flightId}")
    public Map<String, Object> updateFlight(@PathVariable String flightId, @RequestBody FlightUpdateRequest request) {
        return flightService.updateFlight(flightId, request);
    }

    @DeleteMapping("/{flightId}")
    public Map<String, Object> deleteFlight(@PathVariable String flightId) {
        return flightService.deleteFlight(flightId);
    }

    @GetMapping("/search")
    public Map<String, Object> searchFlights(@RequestParam(required = false) String from, @RequestParam(required = false) String to, @RequestParam(required = false) String date) {
        return flightService.searchFlights(from, to, date);
    }

    @GetMapping("/schedule")
    public Map<String, Object> getSchedule(@RequestParam(required = false) String from, @RequestParam(required = false) String to, @RequestParam(required = false) String date, @RequestParam(required = false) String start, @RequestParam(required = false) String end) {
        return flightService.getSchedule(from, to, date, start, end);
    }

    @GetMapping("/lookup/{flightId}")
    public Map<String, Object> lookupFlight(@PathVariable String flightId) {
        return flightService.lookupFlight(flightId);
    }

    @GetMapping("/{flightId}/waitlist")
    public Map<String, Object> getWaitlist(@PathVariable String flightId) {
        return flightService.getWaitlist(flightId);
    }

    @PostMapping("/{flightId}/waitlist")
    public Map<String, Object> addToWaitlist(@PathVariable String flightId, @RequestBody WaitlistCreateRequest request) {
        return flightService.addToWaitlist(flightId, request);
    }

    @PostMapping("/{flightId}/waitlist/promote")
    public Map<String, Object> promoteWaitlist(@PathVariable String flightId, @RequestBody(required = false) WaitlistPromoteRequest request) {
        return flightService.promoteWaitlist(flightId, request);
    }

    @DeleteMapping("/{flightId}/waitlist/{bookingId}")
    public Map<String, Object> removeFromWaitlist(@PathVariable String flightId, @PathVariable String bookingId) {
        return flightService.removeFromWaitlist(flightId, bookingId);
    }
}