package com.example.backend.controller;

import com.example.backend.dto.request.AirportCreateRequest;
import com.example.backend.dto.request.AirportUpdateRequest;
import com.example.backend.entity.Airport;
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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@Transactional
public class AirportController {

    @PersistenceContext
    private EntityManager entityManager;

    @GetMapping("/airports")
    public Map<String, Object> listAirports(@RequestParam(required = false) String search) {
        List<Airport> airports = entityManager.createQuery("select a from Airport a order by a.code asc", Airport.class)
                .getResultList();
        String needle = EntityLookupSupport.normalized(search);
        List<Map<String, Object>> items = new ArrayList<>();
        for (Airport airport : airports) {
            if (!needle.isBlank()) {
                String haystack = (EntityLookupSupport.safe(airport.getCode()) + " "
                        + EntityLookupSupport.safe(airport.getCity()) + " "
                        + EntityLookupSupport.safe(airport.getCountry())).toLowerCase(Locale.ROOT);
                if (!haystack.contains(needle)) {
                    continue;
                }
            }
            items.add(airportData(airport));
        }
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("search", search);
        data.put("count", items.size());
        data.put("items", items);
        return ApiResponse.ok("Airports fetched", data);
    }

    @PostMapping("/airports")
    public Map<String, Object> createAirport(@RequestBody AirportCreateRequest request) {
        if (request.code() == null || request.code().isBlank()) {
            return ApiResponse.badRequest("Airport code is required");
        }
        String code = request.code().trim().toUpperCase(Locale.ROOT);
        if (entityManager.find(Airport.class, code) != null) {
            return ApiResponse.badRequest("Airport code already exists");
        }
        Airport airport = Airport.builder()
                .code(code)
                .city(request.city())
                .country(request.country())
                .latitude(request.latitude())
                .longitude(request.longitude())
                .timezone(request.timezone())
                .build();
        entityManager.persist(airport);
        entityManager.flush();
        return ApiResponse.created("Airport created", Map.of("airport", airportData(airport)));
    }

    @GetMapping("/airports/{code}")
    public Map<String, Object> getAirport(@PathVariable String code) {
        Airport airport = findAirport(code);
        if (airport == null) {
            return ApiResponse.badRequest("Airport not found");
        }
        return ApiResponse.ok("Airport fetched", Map.of("airport", airportData(airport)));
    }

    @PatchMapping("/airports/{code}")
    public Map<String, Object> updateAirport(@PathVariable String code, @RequestBody AirportUpdateRequest request) {
        Airport airport = findAirport(code);
        if (airport == null) {
            return ApiResponse.badRequest("Airport not found");
        }
        if (request.city() != null && !request.city().isBlank()) {
            airport.setCity(request.city());
        }
        if (request.country() != null && !request.country().isBlank()) {
            airport.setCountry(request.country());
        }
        if (request.latitude() != null) {
            airport.setLatitude(request.latitude());
        }
        if (request.longitude() != null) {
            airport.setLongitude(request.longitude());
        }
        if (request.timezone() != null && !request.timezone().isBlank()) {
            airport.setTimezone(request.timezone());
        }
        entityManager.flush();
        return ApiResponse.ok("Airport updated", Map.of("airport", airportData(airport)));
    }

    @DeleteMapping("/airports/{code}")
    public Map<String, Object> deleteAirport(@PathVariable String code) {
        Airport airport = findAirport(code);
        if (airport == null) {
            return ApiResponse.badRequest("Airport not found");
        }
        entityManager.remove(airport);
        entityManager.flush();
        return ApiResponse.ok("Airport deleted", Map.of("code", airport.getCode()));
    }

    private Airport findAirport(String code) {
        if (code == null || code.isBlank()) {
            return null;
        }
        return entityManager.find(Airport.class, code.trim().toUpperCase(Locale.ROOT));
    }

    private Map<String, Object> airportData(Airport airport) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("code", airport.getCode());
        data.put("city", airport.getCity());
        data.put("country", airport.getCountry());
        data.put("latitude", airport.getLatitude());
        data.put("longitude", airport.getLongitude());
        data.put("timezone", airport.getTimezone());
        data.put("createdAt", airport.getCreatedAt());
        data.put("updatedAt", airport.getUpdatedAt());
        return data;
    }
}
