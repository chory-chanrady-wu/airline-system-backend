package com.example.backend.service.impl;

import com.example.backend.controller.ApiResponse;
import com.example.backend.dto.request.AirportCreateRequest;
import com.example.backend.dto.request.AirportUpdateRequest;
import com.example.backend.entity.Airport;
import com.example.backend.entity.Booking;
import com.example.backend.entity.BookingHistory;
import com.example.backend.entity.Flight;
import com.example.backend.entity.FlightStatus;
import com.example.backend.entity.Route;
import com.example.backend.entity.RouteLoadFactor;
import com.example.backend.entity.WaitlistEntry;
import com.example.backend.service.AirportService;
import com.example.backend.service.EntityLookupSupport;
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
public class AirportServiceImpl implements AirportService {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Map<String, Object> listAirports(String search) {
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

    @Override
    public Map<String, Object> createAirport(AirportCreateRequest request) {
        if (request.code() == null || request.code().isBlank()) {
            return ApiResponse.badRequest("Airport code is required");
        }
        if (request.city() == null || request.city().isBlank()) {
            return ApiResponse.badRequest("City is required");
        }
        if (request.country() == null || request.country().isBlank()) {
            return ApiResponse.badRequest("Country is required");
        }
        if (request.latitude() == null) {
            return ApiResponse.badRequest("Latitude is required");
        }
        if (request.longitude() == null) {
            return ApiResponse.badRequest("Longitude is required");
        }
        if (request.timezone() == null || request.timezone().isBlank()) {
            return ApiResponse.badRequest("Timezone is required");
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

    @Override
    public Map<String, Object> getAirport(String code) {
        Airport airport = findAirport(code);
        if (airport == null) {
            return ApiResponse.badRequest("Airport not found");
        }
        return ApiResponse.ok("Airport fetched", Map.of("airport", airportData(airport)));
    }

    @Override
    public Map<String, Object> updateAirport(String code, AirportUpdateRequest request) {
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

    @Override
    public Map<String, Object> deleteAirport(String code) {
        Airport airport = findAirport(code);
        if (airport == null) {
            return ApiResponse.badRequest("Airport not found");
        }
        deleteAirportDependencies(airport);
        entityManager.remove(airport);
        entityManager.flush();
        return ApiResponse.ok("Airport deleted", Map.of("code", airport.getCode()));
    }

    private void deleteAirportDependencies(Airport airport) {
        List<Route> routes = entityManager.createQuery(
                        "select r from Route r where r.fromAirport = :airport or r.toAirport = :airport",
                        Route.class)
                .setParameter("airport", airport)
                .getResultList();

        List<Flight> flights = entityManager.createQuery(
                        "select f from Flight f where f.fromAirport = :airport or f.toAirport = :airport",
                        Flight.class)
                .setParameter("airport", airport)
                .getResultList();
        if (!routes.isEmpty()) {
            List<Flight> routeFlights = entityManager.createQuery(
                            "select f from Flight f where f.route in :routes",
                            Flight.class)
                    .setParameter("routes", routes)
                    .getResultList();
            for (Flight flight : routeFlights) {
                if (!flights.contains(flight)) {
                    flights.add(flight);
                }
            }
        }

        deleteRouteLoadFactors(routes);
        deleteFlightDependencies(flights);

        for (Route route : routes) {
            entityManager.remove(route);
        }
    }

    private void deleteRouteLoadFactors(List<Route> routes) {
        for (Route route : routes) {
            List<RouteLoadFactor> factors = entityManager.createQuery("select r from RouteLoadFactor r where r.route = :route", RouteLoadFactor.class)
                    .setParameter("route", route)
                    .getResultList();
            for (RouteLoadFactor factor : factors) {
                entityManager.remove(factor);
            }
        }
    }

    private void deleteFlightDependencies(List<Flight> flights) {
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
