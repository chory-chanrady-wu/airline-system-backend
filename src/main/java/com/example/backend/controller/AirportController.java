package com.example.backend.controller;

import com.example.backend.dto.request.AirportCreateRequest;
import com.example.backend.dto.request.AirportUpdateRequest;
import com.example.backend.service.AirportService;
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
@RequestMapping("/api/v1")
public class AirportController {

    private final AirportService airportService;

    public AirportController(AirportService airportService) {
        this.airportService = airportService;
    }

    @GetMapping("/airports")
    public Map<String, Object> listAirports(@RequestParam(required = false) String search) {
        return airportService.listAirports(search);
    }

    @PostMapping("/airports")
    public Map<String, Object> createAirport(@RequestBody AirportCreateRequest request) {
        return airportService.createAirport(request);
    }

    @GetMapping("/airports/{code}")
    public Map<String, Object> getAirport(@PathVariable String code) {
        return airportService.getAirport(code);
    }

    @PatchMapping("/airports/{code}")
    public Map<String, Object> updateAirport(@PathVariable String code, @RequestBody AirportUpdateRequest request) {
        return airportService.updateAirport(code, request);
    }

    @DeleteMapping("/airports/{code}")
    public Map<String, Object> deleteAirport(@PathVariable String code) {
        return airportService.deleteAirport(code);
    }
}
