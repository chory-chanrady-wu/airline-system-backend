package com.example.backend.controller;

import com.example.backend.dto.request.AircraftCreateRequest;
import com.example.backend.dto.request.AircraftUpdateRequest;
import com.example.backend.service.AircraftService;
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
public class AircraftController {

    private final AircraftService aircraftService;

    public AircraftController(AircraftService aircraftService) {
        this.aircraftService = aircraftService;
    }

    @GetMapping("/aircraft")
    public Map<String, Object> listAircraft(@RequestParam(required = false) String search) {
        return aircraftService.listAircraft(search);
    }

    @PostMapping("/aircraft")
    public Map<String, Object> createAircraft(@RequestBody AircraftCreateRequest request) {
        return aircraftService.createAircraft(request);
    }

    @GetMapping("/aircraft/{id}")
    public Map<String, Object> getAircraft(@PathVariable Integer id) {
        return aircraftService.getAircraft(id);
    }

    @PatchMapping("/aircraft/{id}")
    public Map<String, Object> updateAircraft(@PathVariable Integer id, @RequestBody AircraftUpdateRequest request) {
        return aircraftService.updateAircraft(id, request);
    }

    @DeleteMapping("/aircraft/{id}")
    public Map<String, Object> deleteAircraft(@PathVariable Integer id) {
        return aircraftService.deleteAircraft(id);
    }
}

