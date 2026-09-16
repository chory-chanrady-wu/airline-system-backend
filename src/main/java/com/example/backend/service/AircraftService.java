package com.example.backend.service;

import com.example.backend.dto.request.AircraftCreateRequest;
import com.example.backend.dto.request.AircraftUpdateRequest;

import java.util.Map;

public interface AircraftService {
    Map<String, Object> listAircraft(String search);

    Map<String, Object> createAircraft(AircraftCreateRequest request);

    Map<String, Object> getAircraft(Integer id);

    Map<String, Object> updateAircraft(Integer id, AircraftUpdateRequest request);

    Map<String, Object> deleteAircraft(Integer id);
}

