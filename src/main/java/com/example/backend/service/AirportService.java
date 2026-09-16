package com.example.backend.service;

import com.example.backend.dto.request.AirportCreateRequest;
import com.example.backend.dto.request.AirportUpdateRequest;

import java.util.Map;

public interface AirportService {
    Map<String, Object> listAirports(String search);

    Map<String, Object> createAirport(AirportCreateRequest request);

    Map<String, Object> getAirport(String code);

    Map<String, Object> updateAirport(String code, AirportUpdateRequest request);

    Map<String, Object> deleteAirport(String code);
}

