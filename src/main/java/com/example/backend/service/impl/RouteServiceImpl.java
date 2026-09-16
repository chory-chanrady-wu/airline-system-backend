package com.example.backend.service.impl;

import com.example.backend.controller.ApiResponse;
import com.example.backend.dto.request.RouteCreateRequest;
import com.example.backend.dto.request.RouteUpdateRequest;
import com.example.backend.entity.Booking;
import com.example.backend.entity.BookingHistory;
import com.example.backend.entity.Airport;
import com.example.backend.entity.Flight;
import com.example.backend.entity.FlightStatus;
import com.example.backend.entity.Route;
import com.example.backend.entity.RouteLoadFactor;
import com.example.backend.entity.WaitlistEntry;
import com.example.backend.service.RouteService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@Transactional
public class RouteServiceImpl implements RouteService {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Map<String, Object> listRoutes() {
        List<Route> routes = entityManager.createQuery("select r from Route r order by r.id asc", Route.class)
                .getResultList();
        List<Map<String, Object>> items = new ArrayList<>();
        for (Route route : routes) {
            items.add(routeData(route));
        }
        return ApiResponse.ok("Routes fetched", Map.of("count", items.size(), "items", items));
    }

    @Override
    public Map<String, Object> createRoute(RouteCreateRequest request) {
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

    @Override
    public Map<String, Object> getRoute(String from, String to) {
        Route route = resolveRoute(from, to);
        if (route == null) {
            return ApiResponse.badRequest("Route not found");
        }
        return ApiResponse.ok("Route fetched", Map.of("route", routeData(route)));
    }

    @Override
    public Map<String, Object> updateRoute(String from, String to, RouteUpdateRequest request) {
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

    @Override
    public Map<String, Object> deleteRoute(String from, String to) {
        Route route = resolveRoute(from, to);
        if (route == null) {
            return ApiResponse.badRequest("Route not found");
        }
        deleteRouteDependencies(route);
        entityManager.remove(route);
        entityManager.flush();
        return ApiResponse.ok("Route deleted", Map.of("from", from, "to", to));
    }

    private void deleteRouteDependencies(Route route) {
        List<RouteLoadFactor> factors = entityManager.createQuery("select r from RouteLoadFactor r where r.route = :route", RouteLoadFactor.class)
                .setParameter("route", route)
                .getResultList();
        for (RouteLoadFactor factor : factors) {
            entityManager.remove(factor);
        }

        List<Flight> flights = entityManager.createQuery("select f from Flight f where f.route = :route", Flight.class)
                .setParameter("route", route)
                .getResultList();
        for (Flight flight : flights) {
            List<WaitlistEntry> waitlistEntries = entityManager.createQuery("select w from WaitlistEntry w where w.flight = :flight", WaitlistEntry.class)
                    .setParameter("flight", flight)
                    .getResultList();
            for (WaitlistEntry entry : waitlistEntries) {
                entityManager.remove(entry);
            }

            List<Booking> bookings = entityManager.createQuery("select b from Booking b where b.flight = :flight", Booking.class)
                    .setParameter("flight", flight)
                    .getResultList();
            for (Booking booking : bookings) {
                List<BookingHistory> histories = entityManager.createQuery("select h from BookingHistory h where h.booking = :booking", BookingHistory.class)
                        .setParameter("booking", booking)
                        .getResultList();
                for (BookingHistory history : histories) {
                    entityManager.remove(history);
                }
                entityManager.remove(booking);
            }

            List<FlightStatus> statuses = entityManager.createQuery("select s from FlightStatus s where s.flight = :flight", FlightStatus.class)
                    .setParameter("flight", flight)
                    .getResultList();
            for (FlightStatus status : statuses) {
                entityManager.remove(status);
            }

            entityManager.remove(flight);
        }
    }

    @Override
    public Map<String, Object> getDistance(String from, String to) {
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

    @Override
    public Map<String, Object> optimizeRoute(String from, String to, String type) {
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

