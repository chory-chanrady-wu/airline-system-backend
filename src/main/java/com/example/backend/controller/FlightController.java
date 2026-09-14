package com.example.backend.controller;
import com.example.backend.dto.request.FlightCreateRequest;
import com.example.backend.dto.request.FlightUpdateRequest;
import com.example.backend.dto.request.WaitlistCreateRequest;
import com.example.backend.dto.request.WaitlistPromoteRequest;
import com.example.backend.entity.Aircraft;
import com.example.backend.entity.Airline;
import com.example.backend.entity.Airport;
import com.example.backend.entity.Booking;
import com.example.backend.entity.Flight;
import com.example.backend.entity.FlightStatus;
import com.example.backend.entity.PassengerProfile;
import com.example.backend.entity.Route;
import com.example.backend.entity.WaitlistEntry;
import com.example.backend.service.EntityLookupSupport;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
@RestController
@RequestMapping("/api/v1/flights")
@Transactional
public class FlightController {
    @PersistenceContext
    private EntityManager entityManager;
    @GetMapping
    public Map<String, Object> listFlights() { return ApiResponse.ok("Flights fetched", Map.of("count", 0, "items", List.of())); }
    @PostMapping
    public Map<String, Object> createFlight(@RequestBody FlightCreateRequest request) { return ApiResponse.badRequest("Flight create not available yet"); }
    @GetMapping("/{flightId}")
    public Map<String, Object> getFlight(@PathVariable String flightId) { return ApiResponse.badRequest("Flight not found"); }
    @PatchMapping("/{flightId}")
    public Map<String, Object> updateFlight(@PathVariable String flightId, @RequestBody FlightUpdateRequest request) { return ApiResponse.badRequest("Flight update not available yet"); }
    @DeleteMapping("/{flightId}")
    public Map<String, Object> deleteFlight(@PathVariable String flightId) { return ApiResponse.badRequest("Flight delete not available yet"); }
    @GetMapping("/search")
    public Map<String, Object> searchFlights(@RequestParam(required = false) String from, @RequestParam(required = false) String to, @RequestParam(required = false) String date) { return ApiResponse.ok("Flight search results", Map.of("count", 0, "items", List.of())); }
    @GetMapping("/schedule")
    public Map<String, Object> getSchedule(@RequestParam(required = false) String from, @RequestParam(required = false) String to, @RequestParam(required = false) String date, @RequestParam(required = false) String start, @RequestParam(required = false) String end) { return ApiResponse.ok("Flight schedule", Map.of("count", 0, "items", List.of())); }
    @GetMapping("/lookup/{flightId}")
    public Map<String, Object> lookupFlight(@PathVariable String flightId) { return ApiResponse.badRequest("Flight not found"); }
    @GetMapping("/{flightId}/waitlist")
    public Map<String, Object> getWaitlist(@PathVariable String flightId) { return ApiResponse.ok("Waitlist fetched", Map.of("count", 0, "items", List.of())); }
    @PostMapping("/{flightId}/waitlist")
    public Map<String, Object> addToWaitlist(@PathVariable String flightId, @RequestBody WaitlistCreateRequest request) { return ApiResponse.badRequest("Waitlist not available yet"); }
    @PostMapping("/{flightId}/waitlist/promote")
    public Map<String, Object> promoteWaitlist(@PathVariable String flightId, @RequestBody(required = false) WaitlistPromoteRequest request) { return ApiResponse.badRequest("Waitlist promote not available yet"); }
    @DeleteMapping("/{flightId}/waitlist/{bookingId}")
    public Map<String, Object> removeFromWaitlist(@PathVariable String flightId, @PathVariable String bookingId) { return ApiResponse.badRequest("Waitlist remove not available yet"); }
}