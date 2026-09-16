package com.example.backend.controller;

import com.example.backend.service.RadarService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/flights")
public class RadarController {

    private final RadarService radarService;

    public RadarController(RadarService radarService) {
        this.radarService = radarService;
    }

    @GetMapping("/radar")
    public Map<String, Object> radar() {
        return radarService.radar();
    }

    @GetMapping("/radar/status")
    public Map<String, Object> radarStatus() {
        return radarService.radarStatus();
    }

    @GetMapping("/radar/{flightId}")
    public Map<String, Object> flightRadar(@PathVariable String flightId) {
        return radarService.flightRadar(flightId);
    }
}
