package com.example.backend.service.impl;

import com.example.backend.controller.ApiResponse;
import com.example.backend.dto.request.FlightCreateRequest;
import com.example.backend.dto.request.FlightUpdateRequest;
import com.example.backend.dto.request.WaitlistCreateRequest;
import com.example.backend.dto.request.WaitlistPromoteRequest;
import com.example.backend.entity.Aircraft;
import com.example.backend.entity.Airline;
import com.example.backend.entity.Airport;
import com.example.backend.entity.Booking;
import com.example.backend.entity.Flight;
import com.example.backend.entity.FlightStatus;
import com.example.backend.entity.PassengerProfile;
import com.example.backend.entity.Route;
import com.example.backend.entity.WaitlistEntry;
import com.example.backend.service.EntityLookupSupport;
import com.example.backend.service.FlightService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Locale;
import java.math.BigDecimal;

@Service
@Transactional
public class FlightServiceImpl implements FlightService {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Map<String, Object> listFlights() {
        List<Flight> flights = entityManager.createQuery("select f from Flight f order by f.departureTime asc, f.id asc", Flight.class)
                .getResultList();
        List<Map<String, Object>> items = new ArrayList<>();
        for (Flight flight : flights) {
            items.add(flightData(flight));
        }
        return ApiResponse.ok("Flights fetched", Map.of("count", items.size(), "items", items));
    }

    @Override
    public Map<String, Object> createFlight(FlightCreateRequest request) {
        if (request.flightNumber() == null || request.flightNumber().isBlank()) {
            return ApiResponse.badRequest("Flight number is required");
        }
        if (request.departureTime() == null) {
            return ApiResponse.badRequest("Departure time is required");
        }
        if (request.arrivalTime() == null) {
            return ApiResponse.badRequest("Arrival time is required");
        }
        if (request.price() == null) {
            return ApiResponse.badRequest("Price is required");
        }

        if (flightNumberExists(request.flightNumber())) {
            return ApiResponse.badRequest("Flight number already exists");
        }

        Airline airline = resolveAirline(request.airlineId());
        Aircraft aircraft = resolveAircraft(request.aircraftId());
        Route route = resolveRoute(request.routeId(), request.fromAirportCode(), request.toAirportCode());
        Airport fromAirport = resolveAirport(request.fromAirportCode());
        Airport toAirport = resolveAirport(request.toAirportCode());

        if (airline == null) {
            return ApiResponse.badRequest("Airline not found");
        }
        if (aircraft == null) {
            return ApiResponse.badRequest("Aircraft not found");
        }
        if (route == null) {
            return ApiResponse.badRequest("Route not found");
        }
        if (fromAirport == null || toAirport == null) {
            return ApiResponse.badRequest("Both airports must exist before creating a flight");
        }
        if (route.getFromAirport() != null && !route.getFromAirport().getCode().equalsIgnoreCase(fromAirport.getCode())) {
            return ApiResponse.badRequest("Route from airport does not match fromAirportCode");
        }
        if (route.getToAirport() != null && !route.getToAirport().getCode().equalsIgnoreCase(toAirport.getCode())) {
            return ApiResponse.badRequest("Route to airport does not match toAirportCode");
        }

        int resolvedSeatCapacity = request.seatCapacity() != null ? request.seatCapacity() : defaultSeatCapacity(aircraft);
        if (resolvedSeatCapacity <= 0) {
            return ApiResponse.badRequest("Seat capacity must be greater than zero");
        }
        if (aircraft.getSeatCapacity() != null && resolvedSeatCapacity > aircraft.getSeatCapacity()) {
            return ApiResponse.badRequest("Seat capacity cannot exceed aircraft seat capacity");
        }

        int resolvedSeatsAvailable = request.seatsAvailable() != null ? request.seatsAvailable() : resolvedSeatCapacity;
        if (resolvedSeatsAvailable < 0) {
            return ApiResponse.badRequest("Seats available cannot be negative");
        }
        if (resolvedSeatsAvailable > resolvedSeatCapacity) {
            return ApiResponse.badRequest("Seats available cannot exceed seat capacity");
        }

        LocalDateTime departureTime = request.departureTime();
        LocalDateTime arrivalTime = request.arrivalTime();
        if (!arrivalTime.isAfter(departureTime)) {
            return ApiResponse.badRequest("Arrival time must be after departure time");
        }

        Flight flight = Flight.builder()
                .flightNumber(request.flightNumber().trim())
                .airline(airline)
                .aircraft(aircraft)
                .route(route)
                .fromAirport(fromAirport)
                .toAirport(toAirport)
                .departureTime(departureTime)
                .arrivalTime(arrivalTime)
                .price(request.price())
                .seatCapacity(resolvedSeatCapacity)
                .seatsAvailable(resolvedSeatsAvailable)
                .status(EntityLookupSupport.parseEnum(Flight.FlightStatusType.class, request.status(), Flight.FlightStatusType.Scheduled))
                .build();
        entityManager.persist(flight);
        entityManager.flush();
        return ApiResponse.created("Flight created", Map.of("flight", flightData(flight)));
    }

    @Override
    public Map<String, Object> getFlight(String flightId) {
        Flight flight = resolveFlight(flightId);
        if (flight == null) {
            return ApiResponse.badRequest("Flight not found");
        }
        return ApiResponse.ok("Flight fetched", Map.of("flight", flightData(flight)));
    }

    @Override
    public Map<String, Object> updateFlight(String flightId, FlightUpdateRequest request) {
        return ApiResponse.badRequest("Flight update not available yet");
    }

    @Override
    public Map<String, Object> deleteFlight(String flightId) {
        return ApiResponse.badRequest("Flight delete not available yet");
    }

    @Override
    public Map<String, Object> searchFlights(String from, String to, String date) {
        return ApiResponse.ok("Flight search results", Map.of("count", 0, "items", List.of()));
    }

    @Override
    public Map<String, Object> getSchedule(String from, String to, String date, String start, String end) {
        return ApiResponse.ok("Flight schedule", Map.of("count", 0, "items", List.of()));
    }

    @Override
    public Map<String, Object> lookupFlight(String flightId) {
        return ApiResponse.badRequest("Flight not found");
    }

    @Override
    public Map<String, Object> getWaitlist(String flightId) {
        return ApiResponse.ok("Waitlist fetched", Map.of("count", 0, "items", List.of()));
    }

    @Override
    public Map<String, Object> addToWaitlist(String flightId, WaitlistCreateRequest request) {
        return ApiResponse.badRequest("Waitlist not available yet");
    }

    @Override
    public Map<String, Object> promoteWaitlist(String flightId, WaitlistPromoteRequest request) {
        return ApiResponse.badRequest("Waitlist promote not available yet");
    }

    @Override
    public Map<String, Object> removeFromWaitlist(String flightId, String bookingId) {
        return ApiResponse.badRequest("Waitlist remove not available yet");
    }

    private Flight resolveFlight(String flightIdentifier) {
        Integer numericId = EntityLookupSupport.parseInteger(flightIdentifier);
        if (numericId != null) {
            Flight flight = entityManager.find(Flight.class, numericId);
            if (flight != null) {
                return flight;
            }
        }
        if (flightIdentifier == null || flightIdentifier.isBlank()) {
            return null;
        }
        List<Flight> flights = entityManager.createQuery("select f from Flight f where lower(f.flightNumber) = :flightNumber", Flight.class)
                .setParameter("flightNumber", flightIdentifier.trim().toLowerCase(Locale.ROOT))
                .getResultList();
        return flights.isEmpty() ? null : flights.get(0);
    }

    private boolean flightNumberExists(String flightNumber) {
        Long count = entityManager.createQuery("select count(f) from Flight f where lower(f.flightNumber) = :flightNumber", Long.class)
                .setParameter("flightNumber", flightNumber.trim().toLowerCase(Locale.ROOT))
                .getSingleResult();
        return count != null && count > 0;
    }

    private Airline resolveAirline(String airlineIdentifier) {
        Integer numericId = EntityLookupSupport.parseInteger(airlineIdentifier);
        if (numericId != null) {
            return entityManager.find(Airline.class, numericId);
        }
        return null;
    }

    private Aircraft resolveAircraft(String aircraftIdentifier) {
        Integer numericId = EntityLookupSupport.parseInteger(aircraftIdentifier);
        if (numericId != null) {
            return entityManager.find(Aircraft.class, numericId);
        }
        return null;
    }

    private Route resolveRoute(String routeIdentifier, String fromAirportCode, String toAirportCode) {
        Integer numericId = EntityLookupSupport.parseInteger(routeIdentifier);
        if (numericId != null) {
            Route route = entityManager.find(Route.class, numericId);
            if (route != null) {
                return route;
            }
        }
        Airport fromAirport = resolveAirport(fromAirportCode);
        Airport toAirport = resolveAirport(toAirportCode);
        if (fromAirport == null || toAirport == null) {
            return null;
        }
        List<Route> routes = entityManager.createQuery(
                        "select r from Route r where lower(r.fromAirport.code) = :from and lower(r.toAirport.code) = :to",
                        Route.class)
                .setParameter("from", fromAirport.getCode().trim().toLowerCase(Locale.ROOT))
                .setParameter("to", toAirport.getCode().trim().toLowerCase(Locale.ROOT))
                .getResultList();
        return routes.isEmpty() ? null : routes.get(0);
    }

    private Airport resolveAirport(String code) {
        if (code == null || code.isBlank()) {
            return null;
        }
        return entityManager.find(Airport.class, code.trim().toUpperCase(Locale.ROOT));
    }

    private int defaultSeatCapacity(Aircraft aircraft) {
        return aircraft == null || aircraft.getSeatCapacity() == null ? 0 : aircraft.getSeatCapacity();
    }

    private Map<String, Object> flightData(Flight flight) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", flight.getId());
        data.put("flightNumber", flight.getFlightNumber());
        data.put("airlineId", flight.getAirline() == null ? null : flight.getAirline().getId());
        data.put("airlineCode", flight.getAirline() == null ? null : flight.getAirline().getCode());
        data.put("aircraftId", flight.getAircraft() == null ? null : flight.getAircraft().getId());
        data.put("aircraftRegistrationNumber", flight.getAircraft() == null ? null : flight.getAircraft().getRegistrationNumber());
        data.put("routeId", flight.getRoute() == null ? null : flight.getRoute().getId());
        data.put("fromAirportCode", flight.getFromAirport() == null ? null : flight.getFromAirport().getCode());
        data.put("toAirportCode", flight.getToAirport() == null ? null : flight.getToAirport().getCode());
        data.put("departureTime", flight.getDepartureTime());
        data.put("arrivalTime", flight.getArrivalTime());
        data.put("price", flight.getPrice());
        data.put("seatCapacity", flight.getSeatCapacity());
        data.put("seatsAvailable", flight.getSeatsAvailable());
        data.put("status", flight.getStatus());
        data.put("createdAt", flight.getCreatedAt());
        data.put("updatedAt", flight.getUpdatedAt());
        return data;
    }
}

