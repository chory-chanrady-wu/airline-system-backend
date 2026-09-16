package com.example.backend.service.impl;

import com.example.backend.controller.ApiResponse;
import com.example.backend.dto.request.AircraftCreateRequest;
import com.example.backend.dto.request.AircraftUpdateRequest;
import com.example.backend.entity.Aircraft;
import com.example.backend.entity.Flight;
import com.example.backend.service.AircraftService;
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
public class AircraftServiceImpl implements AircraftService {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Map<String, Object> listAircraft(String search) {
        List<Aircraft> aircraftList = entityManager.createQuery("select a from Aircraft a order by a.id asc", Aircraft.class)
                .getResultList();
        String needle = EntityLookupSupport.normalized(search);
        List<Map<String, Object>> items = new ArrayList<>();
        for (Aircraft aircraft : aircraftList) {
            if (!needle.isBlank()) {
                String haystack = (EntityLookupSupport.safe(aircraft.getRegistrationNumber()) + " "
                        + EntityLookupSupport.safe(aircraft.getModel())).toLowerCase(Locale.ROOT);
                if (!haystack.contains(needle)) {
                    continue;
                }
            }
            items.add(aircraftData(aircraft));
        }
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("search", search);
        data.put("count", items.size());
        data.put("items", items);
        return ApiResponse.ok("Aircraft fetched", data);
    }

    @Override
    public Map<String, Object> createAircraft(AircraftCreateRequest request) {
        if (request.registrationNumber() == null || request.registrationNumber().isBlank()) {
            return ApiResponse.badRequest("Registration number is required");
        }
        if (request.model() == null || request.model().isBlank()) {
            return ApiResponse.badRequest("Model is required");
        }
        if (request.seatCapacity() == null || request.seatCapacity() <= 0) {
            return ApiResponse.badRequest("Seat capacity must be greater than zero");
        }

        String registrationNumber = request.registrationNumber().trim().toUpperCase(Locale.ROOT);
        if (registrationExists(registrationNumber)) {
            return ApiResponse.badRequest("Registration number already exists");
        }

        Aircraft aircraft = Aircraft.builder()
                .registrationNumber(registrationNumber)
                .model(request.model().trim())
                .seatCapacity(request.seatCapacity())
                .active(request.active() == null || request.active())
                .build();
        entityManager.persist(aircraft);
        entityManager.flush();
        return ApiResponse.created("Aircraft created", Map.of("aircraft", aircraftData(aircraft)));
    }

    @Override
    public Map<String, Object> getAircraft(Integer id) {
        Aircraft aircraft = findAircraft(id);
        if (aircraft == null) {
            return ApiResponse.badRequest("Aircraft not found");
        }
        return ApiResponse.ok("Aircraft fetched", Map.of("aircraft", aircraftData(aircraft)));
    }

    @Override
    public Map<String, Object> updateAircraft(Integer id, AircraftUpdateRequest request) {
        Aircraft aircraft = findAircraft(id);
        if (aircraft == null) {
            return ApiResponse.badRequest("Aircraft not found");
        }

        if (request.registrationNumber() != null && !request.registrationNumber().isBlank()) {
            String registrationNumber = request.registrationNumber().trim().toUpperCase(Locale.ROOT);
            if (!registrationNumber.equalsIgnoreCase(aircraft.getRegistrationNumber()) && registrationExists(registrationNumber)) {
                return ApiResponse.badRequest("Registration number already exists");
            }
            aircraft.setRegistrationNumber(registrationNumber);
        }
        if (request.model() != null && !request.model().isBlank()) {
            aircraft.setModel(request.model().trim());
        }
        if (request.seatCapacity() != null) {
            if (request.seatCapacity() <= 0) {
                return ApiResponse.badRequest("Seat capacity must be greater than zero");
            }
            if (isSeatCapacityLowerThanUsedSeats(aircraft, request.seatCapacity())) {
                return ApiResponse.badRequest("Seat capacity cannot be lower than currently occupied seats");
            }
            aircraft.setSeatCapacity(request.seatCapacity());
        }
        if (request.active() != null) {
            aircraft.setActive(request.active());
        }

        entityManager.flush();
        return ApiResponse.ok("Aircraft updated", Map.of("aircraft", aircraftData(aircraft)));
    }

    @Override
    public Map<String, Object> deleteAircraft(Integer id) {
        Aircraft aircraft = findAircraft(id);
        if (aircraft == null) {
            return ApiResponse.badRequest("Aircraft not found");
        }

        Long flightsCount = entityManager.createQuery("select count(f) from Flight f where f.aircraft = :aircraft", Long.class)
                .setParameter("aircraft", aircraft)
                .getSingleResult();
        if (flightsCount != null && flightsCount > 0) {
            return ApiResponse.badRequest("Cannot delete aircraft because it is still used by flights");
        }

        entityManager.remove(aircraft);
        entityManager.flush();
        return ApiResponse.ok("Aircraft deleted", Map.of("id", id));
    }

    private Aircraft findAircraft(Integer id) {
        return id == null ? null : entityManager.find(Aircraft.class, id);
    }

    private boolean registrationExists(String registrationNumber) {
        Long count = entityManager.createQuery("select count(a) from Aircraft a where lower(a.registrationNumber) = :registration", Long.class)
                .setParameter("registration", registrationNumber.toLowerCase(Locale.ROOT))
                .getSingleResult();
        return count != null && count > 0;
    }

    private boolean isSeatCapacityLowerThanUsedSeats(Aircraft aircraft, int newSeatCapacity) {
        List<Flight> flights = entityManager.createQuery("select f from Flight f where f.aircraft = :aircraft", Flight.class)
                .setParameter("aircraft", aircraft)
                .getResultList();
        for (Flight flight : flights) {
            if (flight.getSeatCapacity() == null || flight.getSeatsAvailable() == null) {
                continue;
            }
            int usedSeats = Math.max(0, flight.getSeatCapacity() - flight.getSeatsAvailable());
            if (newSeatCapacity < usedSeats) {
                return true;
            }
        }
        return false;
    }

    private Map<String, Object> aircraftData(Aircraft aircraft) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", aircraft.getId());
        data.put("registrationNumber", aircraft.getRegistrationNumber());
        data.put("model", aircraft.getModel());
        data.put("seatCapacity", aircraft.getSeatCapacity());
        data.put("active", aircraft.getActive());
        data.put("createdAt", aircraft.getCreatedAt());
        data.put("updatedAt", aircraft.getUpdatedAt());
        return data;
    }
}
