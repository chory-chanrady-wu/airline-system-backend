package com.example.backend.service;

import com.example.backend.dto.request.FlightCreateRequest;
import com.example.backend.dto.request.FlightUpdateRequest;
import com.example.backend.dto.request.WaitlistCreateRequest;
import com.example.backend.dto.request.WaitlistPromoteRequest;

import java.util.Map;

public interface FlightService {
    Map<String, Object> listFlights();

    Map<String, Object> createFlight(FlightCreateRequest request);

    Map<String, Object> getFlight(String flightId);

    Map<String, Object> updateFlight(String flightId, FlightUpdateRequest request);

    Map<String, Object> deleteFlight(String flightId);

    Map<String, Object> searchFlights(String from, String to, String date);

    Map<String, Object> getSchedule(String from, String to, String date, String start, String end);

    Map<String, Object> lookupFlight(String flightId);

    Map<String, Object> getWaitlist(String flightId);

    Map<String, Object> addToWaitlist(String flightId, WaitlistCreateRequest request);

    Map<String, Object> promoteWaitlist(String flightId, WaitlistPromoteRequest request);

    Map<String, Object> removeFromWaitlist(String flightId, String bookingId);
}

