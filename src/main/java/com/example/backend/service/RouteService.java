package com.example.backend.service;

import com.example.backend.dto.request.RouteCreateRequest;
import com.example.backend.dto.request.RouteUpdateRequest;

import java.util.Map;

public interface RouteService {
    Map<String, Object> listRoutes();

    Map<String, Object> createRoute(RouteCreateRequest request);

    Map<String, Object> getRoute(String from, String to);

    Map<String, Object> updateRoute(String from, String to, RouteUpdateRequest request);

    Map<String, Object> deleteRoute(String from, String to);

    Map<String, Object> getDistance(String from, String to);

    Map<String, Object> optimizeRoute(String from, String to, String type);
}

