package com.example.backend.service.impl;

import com.example.backend.controller.ApiResponse;
import com.example.backend.dto.request.PassengerCreateRequest;
import com.example.backend.dto.request.PassengerUpdateRequest;
import com.example.backend.entity.Booking;
import com.example.backend.entity.BookingHistory;
import com.example.backend.entity.PassengerProfile;
import com.example.backend.entity.User;
import com.example.backend.service.EntityLookupSupport;
import com.example.backend.service.PassengerService;
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
public class PassengerServiceImpl implements PassengerService {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Map<String, Object> listPassengers(String search) {
        List<PassengerProfile> passengers = entityManager.createQuery("select p from PassengerProfile p order by p.id asc", PassengerProfile.class)
                .getResultList();
        String needle = EntityLookupSupport.normalized(search);
        List<Map<String, Object>> items = new ArrayList<>();
        for (PassengerProfile passenger : passengers) {
            if (!needle.isBlank()) {
                String haystack = (EntityLookupSupport.safe(passenger.getPassportNumber()) + " "
                        + EntityLookupSupport.safe(passenger.getNationality()) + " "
                        + EntityLookupSupport.safe(passenger.getPhone()) + " "
                        + (passenger.getUser() == null ? "" : EntityLookupSupport.safe(passenger.getUser().getName()))
                        + " " + (passenger.getUser() == null ? "" : EntityLookupSupport.safe(passenger.getUser().getEmail()))).toLowerCase(Locale.ROOT);
                if (!haystack.contains(needle)) {
                    continue;
                }
            }
            items.add(passengerData(passenger));
        }
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("search", search);
        payload.put("count", items.size());
        payload.put("items", items);
        return ApiResponse.ok("Passengers fetched", payload);
    }

    @Override
    public Map<String, Object> createPassenger(PassengerCreateRequest request) {
        User user = resolveUser(request.userId());
        if (user == null) {
            return ApiResponse.badRequest("User not found");
        }
        if (request.passportNumber() == null || request.passportNumber().isBlank()) {
            return ApiResponse.badRequest("Passport number is required");
        }
        PassengerProfile passenger = PassengerProfile.builder()
                .user(user)
                .passportNumber(request.passportNumber())
                .nationality(request.nationality())
                .phone(request.phone())
                .dateOfBirth(request.dateOfBirth())
                .emergencyContact(request.emergencyContact())
                .build();
        entityManager.persist(passenger);
        entityManager.flush();
        return ApiResponse.created("Passenger created", Map.of("passenger", passengerData(passenger)));
    }

    @Override
    public Map<String, Object> getPassenger(Integer id) {
        PassengerProfile passenger = findPassenger(id);
        if (passenger == null) {
            return ApiResponse.badRequest("Passenger not found");
        }
        return ApiResponse.ok("Passenger fetched", Map.of("passenger", passengerData(passenger)));
    }

    @Override
    public Map<String, Object> updatePassenger(Integer id, PassengerUpdateRequest request) {
        PassengerProfile passenger = findPassenger(id);
        if (passenger == null) {
            return ApiResponse.badRequest("Passenger not found");
        }
        if (request.passportNumber() != null && !request.passportNumber().isBlank()) {
            passenger.setPassportNumber(request.passportNumber());
        }
        if (request.nationality() != null && !request.nationality().isBlank()) {
            passenger.setNationality(request.nationality());
        }
        if (request.phone() != null && !request.phone().isBlank()) {
            passenger.setPhone(request.phone());
        }
        if (request.dateOfBirth() != null) {
            passenger.setDateOfBirth(request.dateOfBirth());
        }
        if (request.emergencyContact() != null && !request.emergencyContact().isBlank()) {
            passenger.setEmergencyContact(request.emergencyContact());
        }
        entityManager.flush();
        return ApiResponse.ok("Passenger updated", Map.of("passenger", passengerData(passenger)));
    }

    @Override
    public Map<String, Object> deletePassenger(Integer id) {
        PassengerProfile passenger = findPassenger(id);
        if (passenger == null) {
            return ApiResponse.badRequest("Passenger not found");
        }
        entityManager.createQuery("delete from BookingHistory h where h.passenger = :passenger")
                .setParameter("passenger", passenger)
                .executeUpdate();
        entityManager.createQuery("delete from Booking b where b.passenger = :passenger")
                .setParameter("passenger", passenger)
                .executeUpdate();
        entityManager.remove(passenger);
        entityManager.flush();
        return ApiResponse.ok("Passenger deleted", Map.of("id", passenger.getId()));
    }

    @Override
    public Map<String, Object> getPassengerBookings(Integer passengerId) {
        PassengerProfile passenger = findPassenger(passengerId);
        if (passenger == null) {
            return ApiResponse.badRequest("Passenger not found");
        }
        List<Booking> bookings = entityManager.createQuery("select b from Booking b where b.passenger = :passenger order by b.bookedAt desc", Booking.class)
                .setParameter("passenger", passenger)
                .getResultList();
        List<Map<String, Object>> items = new ArrayList<>();
        for (Booking booking : bookings) {
            items.add(bookingData(booking));
        }
        return ApiResponse.ok("Passenger bookings fetched", Map.of("passengerId", passengerId, "count", items.size(), "items", items));
    }

    @Override
    public Map<String, Object> getPassengerBookingHistory(Integer passengerId) {
        PassengerProfile passenger = findPassenger(passengerId);
        if (passenger == null) {
            return ApiResponse.badRequest("Passenger not found");
        }
        List<BookingHistory> history = entityManager.createQuery("select h from BookingHistory h where h.passenger = :passenger order by h.performedAt desc", BookingHistory.class)
                .setParameter("passenger", passenger)
                .getResultList();
        List<Map<String, Object>> items = new ArrayList<>();
        for (BookingHistory entry : history) {
            items.add(historyData(entry));
        }
        return ApiResponse.ok("Passenger booking history fetched", Map.of("passengerId", passengerId, "count", items.size(), "items", items));
    }

    private PassengerProfile findPassenger(Integer id) {
        return id == null ? null : entityManager.find(PassengerProfile.class, id);
    }

    private User resolveUser(String userIdentifier) {
        Integer numericId = EntityLookupSupport.parseInteger(userIdentifier);
        if (numericId != null) {
            return entityManager.find(User.class, numericId);
        }
        if (userIdentifier == null || userIdentifier.isBlank()) {
            return null;
        }
        List<User> users = entityManager.createQuery("select u from User u where lower(u.email) = :email", User.class)
                .setParameter("email", userIdentifier.trim().toLowerCase(Locale.ROOT))
                .getResultList();
        return users.isEmpty() ? null : users.get(0);
    }

    private Map<String, Object> passengerData(PassengerProfile passenger) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", passenger.getId());
        data.put("userId", passenger.getUser() == null ? null : passenger.getUser().getId());
        data.put("userName", passenger.getUser() == null ? null : passenger.getUser().getName());
        data.put("userEmail", passenger.getUser() == null ? null : passenger.getUser().getEmail());
        data.put("passportNumber", passenger.getPassportNumber());
        data.put("nationality", passenger.getNationality());
        data.put("phone", passenger.getPhone());
        data.put("dateOfBirth", passenger.getDateOfBirth());
        data.put("emergencyContact", passenger.getEmergencyContact());
        data.put("createdAt", passenger.getCreatedAt());
        data.put("updatedAt", passenger.getUpdatedAt());
        return data;
    }

    private Map<String, Object> bookingData(Booking booking) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", booking.getId());
        data.put("bookingReference", booking.getBookingReference());
        data.put("flightId", booking.getFlight() == null ? null : booking.getFlight().getId());
        data.put("flightNumber", booking.getFlight() == null ? null : booking.getFlight().getFlightNumber());
        data.put("seatNumber", booking.getSeatNumber());
        data.put("amount", booking.getAmount());
        data.put("currency", booking.getCurrency());
        data.put("status", booking.getStatus());
        data.put("bookedAt", booking.getBookedAt());
        data.put("cancelledAt", booking.getCancelledAt());
        return data;
    }

    private Map<String, Object> historyData(BookingHistory history) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", history.getId());
        data.put("bookingId", history.getBooking() == null ? null : history.getBooking().getId());
        data.put("bookingReference", history.getBooking() == null ? null : history.getBooking().getBookingReference());
        data.put("action", history.getAction());
        data.put("previousStatus", history.getPreviousStatus());
        data.put("newStatus", history.getNewStatus());
        data.put("performedAt", history.getPerformedAt());
        return data;
    }
}

