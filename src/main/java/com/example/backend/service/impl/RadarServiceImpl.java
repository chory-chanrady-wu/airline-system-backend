package com.example.backend.service.impl;

import com.example.backend.controller.ApiResponse;
import com.example.backend.entity.Flight;
import com.example.backend.entity.FlightStatus;
import com.example.backend.entity.RadarSnapshot;
import com.example.backend.service.EntityLookupSupport;
import com.example.backend.service.RadarService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@Transactional
public class RadarServiceImpl implements RadarService {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Map<String, Object> radar() {
        List<FlightStatus> statuses = entityManager.createQuery("select s from FlightStatus s order by s.recordedAt desc", FlightStatus.class)
                .setMaxResults(50)
                .getResultList();
        List<Map<String, Object>> items = new ArrayList<>();
        for (FlightStatus status : statuses) {
            items.add(radarItem(status));
        }
        return ApiResponse.ok("Radar data fetched", Map.of("count", items.size(), "items", items));
    }

    @Override
    public Map<String, Object> radarStatus() {
        RadarSnapshot snapshot = latestRadarSnapshot();
        if (snapshot != null) {
            boolean online = snapshot.getExpiresAt() == null || snapshot.getExpiresAt().isAfter(java.time.LocalDateTime.now());
            return ApiResponse.ok("Radar provider status", Map.of(
                    "provider", snapshot.getProvider(),
                    "status", online ? "online" : "stale",
                    "refreshIntervalSeconds", snapshot.getExpiresAt() == null ? 30 : Math.max(1, (int) java.time.Duration.between(snapshot.getFetchedAt(), snapshot.getExpiresAt()).getSeconds()),
                    "aircraftCount", snapshot.getAircraftCount(),
                    "fetchedAt", snapshot.getFetchedAt(),
                    "expiresAt", snapshot.getExpiresAt()
            ));
        }
        long trackedFlights = entityManager.createQuery("select count(s) from FlightStatus s", Long.class).getSingleResult();
        return ApiResponse.ok("Radar provider status", Map.of(
                "provider", "database",
                "status", trackedFlights > 0 ? "online" : "offline",
                "refreshIntervalSeconds", 30,
                "aircraftCount", trackedFlights
        ));
    }

    @Override
    public Map<String, Object> flightRadar(String flightId) {
        Flight flight = resolveFlight(flightId);
        if (flight == null) {
            return ApiResponse.badRequest("Flight not found");
        }
        FlightStatus status = latestStatus(flight);
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("flightId", flight.getId());
        data.put("flightNumber", flight.getFlightNumber());
        data.put("route", flight.getRoute() == null ? null : Map.of(
                "from", flight.getFromAirport() == null ? null : flight.getFromAirport().getCode(),
                "to", flight.getToAirport() == null ? null : flight.getToAirport().getCode()
        ));
        data.put("status", status == null ? flight.getStatus() : status.getStatus());
        data.put("gate", status == null ? null : status.getGate());
        data.put("terminal", status == null ? null : status.getTerminal());
        data.put("delayMinutes", status == null ? null : status.getDelayMinutes());
        data.put("remarks", status == null ? null : status.getRemarks());
        data.put("recordedAt", status == null ? null : status.getRecordedAt());
        return ApiResponse.ok("Flight radar data fetched", data);
    }

    private RadarSnapshot latestRadarSnapshot() {
        List<RadarSnapshot> snapshots = entityManager.createQuery("select r from RadarSnapshot r order by r.fetchedAt desc", RadarSnapshot.class)
                .setMaxResults(1)
                .getResultList();
        return snapshots.isEmpty() ? null : snapshots.get(0);
    }

    private Flight resolveFlight(String flightIdentifier) {
        Integer numericId = EntityLookupSupport.parseInteger(flightIdentifier);
        if (numericId != null) {
            Flight flight = entityManager.find(Flight.class, numericId);
            if (flight != null) {
                return flight;
            }
        }
        List<Flight> flights = entityManager.createQuery("select f from Flight f where lower(f.flightNumber) = :flightNumber", Flight.class)
                .setParameter("flightNumber", flightIdentifier.trim().toLowerCase(Locale.ROOT))
                .getResultList();
        return flights.isEmpty() ? null : flights.get(0);
    }

    private FlightStatus latestStatus(Flight flight) {
        List<FlightStatus> statuses = entityManager.createQuery("select s from FlightStatus s where s.flight = :flight order by s.recordedAt desc", FlightStatus.class)
                .setParameter("flight", flight)
                .setMaxResults(1)
                .getResultList();
        return statuses.isEmpty() ? null : statuses.get(0);
    }

    private Map<String, Object> radarItem(FlightStatus status) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("flightId", status.getFlight() == null ? null : status.getFlight().getId());
        data.put("flightNumber", status.getFlight() == null ? null : status.getFlight().getFlightNumber());
        data.put("status", status.getStatus());
        data.put("delayMinutes", status.getDelayMinutes());
        data.put("gate", status.getGate());
        data.put("terminal", status.getTerminal());
        data.put("remarks", status.getRemarks());
        data.put("recordedAt", status.getRecordedAt());
        return data;
    }
}

