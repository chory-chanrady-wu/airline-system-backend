package com.example.backend.service.impl;

import com.example.backend.controller.ApiResponse;
import com.example.backend.entity.RadarSnapshot;
import com.example.backend.service.HealthService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class HealthServiceImpl implements HealthService {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Map<String, Object> health() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("status", "ok");
        data.put("timestamp", Instant.now().toString());
        data.put("users", count("select count(u) from User u"));
        data.put("flights", count("select count(f) from Flight f"));
        data.put("bookings", count("select count(b) from Booking b"));
        return ApiResponse.ok("Service is healthy", data);
    }

    @Override
    public Map<String, Object> databaseHealth() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("status", "connected");
        data.put("tables", Map.of(
                "users", count("select count(u) from User u"),
                "roles", count("select count(r) from Role r"),
                "airports", count("select count(a) from Airport a"),
                "flights", count("select count(f) from Flight f"),
                "bookings", count("select count(b) from Booking b")
        ));
        return ApiResponse.ok("Database health check passed", data);
    }

    @Override
    public Map<String, Object> providerHealth() {
        List<RadarSnapshot> radarSnapshots = entityManager.createQuery("select r from RadarSnapshot r order by r.fetchedAt desc", RadarSnapshot.class)
                .setMaxResults(1)
                .getResultList();
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("radar", radarSnapshots.isEmpty() ? "offline" : "online");
        data.put("weather", "unknown");
        data.put("radarSnapshot", radarSnapshots.isEmpty() ? null : Map.of(
                "provider", radarSnapshots.get(0).getProvider(),
                "aircraftCount", radarSnapshots.get(0).getAircraftCount(),
                "fetchedAt", radarSnapshots.get(0).getFetchedAt(),
                "expiresAt", radarSnapshots.get(0).getExpiresAt()
        ));
        return ApiResponse.ok("External providers health check", data);
    }

    private long count(String query) {
        Long result = entityManager.createQuery(query, Long.class).getSingleResult();
        return result == null ? 0L : result;
    }
}

