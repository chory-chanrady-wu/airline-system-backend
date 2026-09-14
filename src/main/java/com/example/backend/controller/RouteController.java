package com.example.backend.controller;

import com.example.backend.dto.request.RouteCreateRequest;
import com.example.backend.dto.request.RouteUpdateRequest;
import com.example.backend.entity.Airport;
import com.example.backend.entity.Flight;
import com.example.backend.entity.Route;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/routes")
@Transactional
public class RouteController {

    @PersistenceContext
    private EntityManager entityManager;

    @GetMapping
    public Map<String, Object> listRoutes() {
        List<Route> routes = entityManager.createQuery("select r from Route r order by r.id asc", Route.class)
                .getResultList();
        List<Map<String, Object>> items = new ArrayList<>();
        for (Route route : routes) {
            items.add(routeData(route));
        }
        return ApiResponse.ok("Routes fetched", Map.of("count", items.size(), "items", items));
    }

    @PostMapping
    public Map<String, Object> createRoute(@RequestBody RouteCreateRequest request) {
        Airport fromAirport = resolveAirport(request.fromAirportCode());
        Airport toAirport = resolveAirport(request.toAirportCode());
        if (fromAirport == null || toAirport == null) {
            return ApiResponse.badRequest("Both airports must exist before creating a route");
        }
        Route route = Route.builder()
                .fromAirport(fromAirport)
                .toAirport(toAirport)
                .distanceKm(request.distanceKm() == null ? 0 : request.distanceKm())
                .durationMinutes(request.durationMinutes() == null ? 0 : request.durationMinutes())
                .active(request.active() == null || request.active())
                .build();
        entityManager.persist(route);
        entityManager.flush();
        return ApiResponse.created("Route created", Map.of("route", routeData(route)));
    }

    @GetMapping("/{from}/{to}")
    public Map<String, Object> getRoute(@PathVariable String from, @PathVariable String to) {
        Route route = resolveRoute(from, to);
        if (route == null) {
            return ApiResponse.badRequest("Route not found");
        }
        return ApiResponse.ok("Route fetched", Map.of("route", routeData(route)));
    }

    @PatchMapping("/{from}/{to}")
    public Map<String, Object> updateRoute(@PathVariable String from, @PathVariable String to, @RequestBody RouteUpdateRequest request) {
        Route route = resolveRoute(from, to);
        if (route == null) {
            return ApiResponse.badRequest("Route not found");
        }
        if (request.fromAirportCode() != null && !request.fromAirportCode().isBlank()) {
            Airport airport = resolveAirport(request.fromAirportCode());
            if (airport != null) {
                route.setFromAirport(airport);
            }
        }
        if (request.toAirportCode() != null && !request.toAirportCode().isBlank()) {
            Airport airport = resolveAirport(request.toAirportCode());
            if (airport != null) {
                route.setToAirport(airport);
            }
        }
        if (request.distanceKm() != null) {
            route.setDistanceKm(request.distanceKm());
        }
        if (request.durationMinutes() != null) {
            route.setDurationMinutes(request.durationMinutes());
        }
        if (request.active() != null) {
            route.setActive(request.active());
        }
        entityManager.flush();
        return ApiResponse.ok("Route updated", Map.of("route", routeData(route)));
    }

    @DeleteMapping("/{from}/{to}")
    public Map<String, Object> deleteRoute(@PathVariable String from, @PathVariable String to) {
        Route route = resolveRoute(from, to);
        if (route == null) {
            return ApiResponse.badRequest("Route not found");
        }
        entityManager.remove(route);
        entityManager.flush();
        return ApiResponse.ok("Route deleted", Map.of("from", from, "to", to));
    }

    @GetMapping("/{from}/{to}/distance")
    public Map<String, Object> getDistance(@PathVariable String from, @PathVariable String to) {
        Route route = resolveRoute(from, to);
        if (route == null) {
            return ApiResponse.badRequest("Route not found");
        }
        return ApiResponse.ok("Distance fetched", Map.of(
                "from", from,
                "to", to,
                "distanceKm", route.getDistanceKm(),
                "unit", "km"
        ));
    }

    @GetMapping("/optimize")
    public Map<String, Object> optimizeRoute(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam(defaultValue = "cheapest") String type) {
        List<Map<String, Object>> path = buildRoutePath(from, to, type);
        return ApiResponse.ok("Route optimization computed", Map.of(
                "from", from,
                "to", to,
                "type", type,
                "path", path
        ));
    }

    private List<Map<String, Object>> buildRoutePath(String from, String to, String type) {
        Route direct = resolveRoute(from, to);
        if (direct != null) {
            return List.of(routeSegment(direct, representativeFlight(direct)));
        }

        List<Route> firstLegs = entityManager.createQuery(
                        "select r from Route r where lower(r.fromAirport.code) = :from and r.active = true",
                        Route.class)
                .setParameter("from", from.trim().toLowerCase(Locale.ROOT))
                .getResultList();
        for (Route firstLeg : firstLegs) {
            List<Route> secondLegs = entityManager.createQuery(
                            "select r from Route r where lower(r.fromAirport.code) = :hub and lower(r.toAirport.code) = :to and r.active = true",
                            Route.class)
                    .setParameter("hub", airportCode(firstLeg.getToAirport()))
                    .setParameter("to", to.trim().toLowerCase(Locale.ROOT))
                    .getResultList();
            if (!secondLegs.isEmpty()) {
                Route secondLeg = secondLegs.get(0);
                return List.of(
                        routeSegment(firstLeg, representativeFlight(firstLeg)),
                        routeSegment(secondLeg, representativeFlight(secondLeg))
                );
            }
        }

        Map<String, Object> fallback = new LinkedHashMap<>();
        fallback.put("from", from);
        fallback.put("to", to);
        fallback.put("type", type);
        fallback.put("durationMinutes", null);
        fallback.put("price", null);
        return List.of(fallback);
    }

    private Map<String, Object> routeSegment(Route route, Flight flight) {
        Map<String, Object> segment = new LinkedHashMap<>();
        segment.put("from", route.getFromAirport() == null ? null : route.getFromAirport().getCode());
        segment.put("to", route.getToAirport() == null ? null : route.getToAirport().getCode());
        segment.put("airlineCode", flight == null || flight.getAirline() == null ? null : flight.getAirline().getCode());
        segment.put("durationMinutes", route.getDurationMinutes());
        segment.put("price", flight == null ? BigDecimal.valueOf(route.getDistanceKm() == null ? 0 : route.getDistanceKm()) : flight.getPrice());
        return segment;
    }

    private Flight representativeFlight(Route route) {
        List<Flight> flights = entityManager.createQuery("select f from Flight f where f.route = :route order by f.departureTime asc", Flight.class)
                .setParameter("route", route)
                .getResultList();
        return flights.isEmpty() ? null : flights.get(0);
    }

    private Route resolveRoute(String from, String to) {
        List<Route> routes = entityManager.createQuery(
                        "select r from Route r where lower(r.fromAirport.code) = :from and lower(r.toAirport.code) = :to",
                        Route.class)
                .setParameter("from", from.trim().toLowerCase(Locale.ROOT))
                .setParameter("to", to.trim().toLowerCase(Locale.ROOT))
                .getResultList();
        return routes.isEmpty() ? null : routes.get(0);
    }

    private Airport resolveAirport(String code) {
        if (code == null || code.isBlank()) {
            return null;
        }
        return entityManager.find(Airport.class, code.trim().toUpperCase(Locale.ROOT));
    }

    private String airportCode(Airport airport) {
        return airport == null || airport.getCode() == null ? null : airport.getCode().toLowerCase(Locale.ROOT);
    }

    private Map<String, Object> routeData(Route route) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", route.getId());
        data.put("fromAirportCode", route.getFromAirport() == null ? null : route.getFromAirport().getCode());
        data.put("toAirportCode", route.getToAirport() == null ? null : route.getToAirport().getCode());
        data.put("distanceKm", route.getDistanceKm());
        data.put("durationMinutes", route.getDurationMinutes());
        data.put("active", route.getActive());
        data.put("createdAt", route.getCreatedAt());
        data.put("updatedAt", route.getUpdatedAt());
        return data;
    }
}
