package com.example.backend.service.impl;

import com.example.backend.controller.ApiResponse;
import com.example.backend.dto.request.BookingCreateRequest;
import com.example.backend.dto.request.BookingUpdateRequest;
import com.example.backend.entity.Booking;
import com.example.backend.entity.BookingHistory;
import com.example.backend.entity.Flight;
import com.example.backend.entity.PassengerProfile;
import com.example.backend.entity.WaitlistEntry;
import com.example.backend.service.BookingService;
import com.example.backend.service.EntityLookupSupport;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Transactional
public class BookingServiceImpl implements BookingService {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Map<String, Object> listBookings() {
        List<Booking> bookings = entityManager.createQuery("select b from Booking b order by b.bookedAt desc, b.id desc", Booking.class).getResultList();
        List<Map<String, Object>> items = new ArrayList<>();
        for (Booking booking : bookings) {
            items.add(bookingData(booking));
        }
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("count", items.size());
        data.put("items", items);
        return ApiResponse.ok("Bookings fetched", data);
    }

    @Override
    public Map<String, Object> createBooking(BookingCreateRequest request) {
        PassengerProfile passenger = null;
        if (request.passengerName() != null && !request.passengerName().isBlank()) {
            passenger = resolvePassengerByName(request.passengerName());
        }
        if (passenger == null) {
            passenger = resolvePassenger(request.passengerId());
        }

        Flight flight = null;
        if (request.flightNumber() != null && !request.flightNumber().isBlank()) {
            flight = resolveFlight(request.flightNumber());
        }
        if (flight == null) {
            flight = resolveFlight(request.flightId());
        }

        if (passenger == null) {
            return ApiResponse.badRequest("Passenger not found");
        }
        if (flight == null) {
            return ApiResponse.badRequest("Flight not found");
        }

        Booking.BookingStatus status = EntityLookupSupport.parseEnum(Booking.BookingStatus.class, request.status(), Booking.BookingStatus.Confirmed);
        if (status == Booking.BookingStatus.Confirmed && flight.getSeatsAvailable() != null && flight.getSeatsAvailable() <= 0) {
            status = Booking.BookingStatus.Waitlisted;
        }

        String assignedSeatNumber = null;
        if (status == Booking.BookingStatus.Confirmed) {
            assignedSeatNumber = assignRandomSeatNumber(flight);
        }

        // Persist with a temporary unique reference to satisfy NOT NULL/UNIQUE constraints.
        Booking booking = Booking.builder()
                .bookingReference("TMP-" + UUID.randomUUID())
                .passenger(passenger)
                .flight(flight)
                .seatNumber(assignedSeatNumber)
                .amount(request.amount() == null ? BigDecimal.ZERO : request.amount())
                .currency(request.currency() == null || request.currency().isBlank() ? "USD" : request.currency())
                .status(status)
                .build();

        entityManager.persist(booking);
        entityManager.flush();
        booking.setBookingReference(String.format("BK-%08d", booking.getId()));

        if (status == Booking.BookingStatus.Confirmed) {
            occupySeat(flight);
        }

        writeHistory(booking, passenger,
                status == Booking.BookingStatus.Waitlisted ? BookingHistory.ActionType.Waitlisted : BookingHistory.ActionType.Created,
                null,
                status.name());

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("booking", bookingData(booking));
        if (status == Booking.BookingStatus.Waitlisted) {
            WaitlistEntry waitlistEntry = createWaitlistEntry(flight, passenger, booking);
            data.put("waitlistEntry", waitlistData(waitlistEntry));
        }

        entityManager.flush();
        return ApiResponse.created("Booking created", data);
    }

    @Override
    public Map<String, Object> getBooking(String bookingId) {
        Booking booking = resolveBooking(bookingId);
        if (booking == null) {
            return ApiResponse.badRequest("Booking not found");
        }
        return ApiResponse.ok("Booking fetched", Map.of("booking", bookingData(booking)));
    }

    @Override
    public Map<String, Object> updateBooking(String bookingId, BookingUpdateRequest request) {
        Booking booking = resolveBooking(bookingId);
        if (booking == null) {
            return ApiResponse.badRequest("Booking not found");
        }
        if (request.seatNumber() != null && !request.seatNumber().isBlank()) {
            booking.setSeatNumber(request.seatNumber());
        }
        if (request.status() != null && !request.status().isBlank()) {
            applyStatusChange(booking, EntityLookupSupport.parseEnum(Booking.BookingStatus.class, request.status(), booking.getStatus()));
        }
        entityManager.flush();
        return ApiResponse.ok("Booking updated", Map.of("booking", bookingData(booking)));
    }

    @Override
    public Map<String, Object> deleteBooking(String bookingId) {
        Booking booking = resolveBooking(bookingId);
        if (booking == null) {
            return ApiResponse.badRequest("Booking not found");
        }
        releaseSeatIfNeeded(booking);
        entityManager.createQuery("delete from BookingHistory h where h.booking = :booking").setParameter("booking", booking).executeUpdate();
        entityManager.createQuery("delete from WaitlistEntry w where w.booking = :booking").setParameter("booking", booking).executeUpdate();
        entityManager.remove(booking);
        entityManager.flush();
        return ApiResponse.ok("Booking deleted", Map.of("bookingId", booking.getId()));
    }

    @Override
    public Map<String, Object> cancelBooking(String bookingId) {
        Booking booking = resolveBooking(bookingId);
        if (booking == null) {
            return ApiResponse.badRequest("Booking not found");
        }
        applyStatusChange(booking, Booking.BookingStatus.Cancelled);
        entityManager.flush();
        return ApiResponse.ok("Booking cancelled", Map.of("booking", bookingData(booking)));
    }

    @Override
    public Map<String, Object> undoCancellation(String bookingId) {
        Booking booking = resolveBooking(bookingId);
        if (booking == null) {
            return ApiResponse.badRequest("Booking not found");
        }
        applyStatusChange(booking, Booking.BookingStatus.Confirmed);
        entityManager.flush();
        return ApiResponse.ok("Cancellation undone", Map.of("booking", bookingData(booking)));
    }

    private void applyStatusChange(Booking booking, Booking.BookingStatus newStatus) {
        Booking.BookingStatus oldStatus = booking.getStatus();
        if (oldStatus == newStatus) {
            return;
        }
        if (oldStatus == Booking.BookingStatus.Confirmed && newStatus != Booking.BookingStatus.Confirmed) {
            releaseSeatIfNeeded(booking);
        }
        if (oldStatus != Booking.BookingStatus.Confirmed && newStatus == Booking.BookingStatus.Confirmed) {
            occupySeat(booking.getFlight());
        }
        booking.setStatus(newStatus);
        booking.setCancelledAt(newStatus == Booking.BookingStatus.Cancelled ? LocalDateTime.now() : null);
        writeHistory(booking, booking.getPassenger(), historyAction(oldStatus, newStatus), oldStatus == null ? null : oldStatus.name(), newStatus.name());
    }

    private BookingHistory.ActionType historyAction(Booking.BookingStatus oldStatus, Booking.BookingStatus newStatus) {
        if (newStatus == Booking.BookingStatus.Cancelled) {
            return BookingHistory.ActionType.Cancelled;
        }
        if (oldStatus == Booking.BookingStatus.Cancelled && newStatus == Booking.BookingStatus.Confirmed) {
            return BookingHistory.ActionType.Undone;
        }
        if (newStatus == Booking.BookingStatus.Waitlisted) {
            return BookingHistory.ActionType.Waitlisted;
        }
        return BookingHistory.ActionType.Promoted;
    }

    private void releaseSeatIfNeeded(Booking booking) {
        if (booking.getStatus() == Booking.BookingStatus.Confirmed) {
            Flight flight = booking.getFlight();
            if (flight != null && flight.getSeatsAvailable() != null) {
                flight.setSeatsAvailable(flight.getSeatsAvailable() + 1);
            }
        }
    }

    private void occupySeat(Flight flight) {
        if (flight != null && flight.getSeatsAvailable() != null && flight.getSeatsAvailable() > 0) {
            flight.setSeatsAvailable(flight.getSeatsAvailable() - 1);
        }
    }

    private void writeHistory(Booking booking, PassengerProfile passenger, BookingHistory.ActionType actionType, String previousStatus, String newStatus) {
        BookingHistory history = BookingHistory.builder().booking(booking).passenger(passenger).action(actionType).previousStatus(previousStatus).newStatus(newStatus).build();
        entityManager.persist(history);
    }

    private WaitlistEntry createWaitlistEntry(Flight flight, PassengerProfile passenger, Booking booking) {
        Integer position = nextWaitlistPosition(flight);
        WaitlistEntry entry = WaitlistEntry.builder().flight(flight).passenger(passenger).booking(booking).position(position).status(WaitlistEntry.WaitlistStatus.Waiting).build();
        entityManager.persist(entry);
        return entry;
    }

    private Integer nextWaitlistPosition(Flight flight) {
        Long count = entityManager.createQuery("select count(w) from WaitlistEntry w where w.flight = :flight", Long.class).setParameter("flight", flight).getSingleResult();
        return count == null ? 1 : count.intValue() + 1;
    }

    private Booking resolveBooking(String bookingIdentifier) {
        Integer numericId = EntityLookupSupport.parseInteger(bookingIdentifier);
        if (numericId != null) {
            Booking booking = entityManager.find(Booking.class, numericId);
            if (booking != null) {
                return booking;
            }
        }
        if (bookingIdentifier == null || bookingIdentifier.isBlank()) {
            return null;
        }
        List<Booking> bookings = entityManager.createQuery("select b from Booking b where lower(b.bookingReference) = :reference", Booking.class).setParameter("reference", bookingIdentifier.trim().toLowerCase(Locale.ROOT)).getResultList();
        return bookings.isEmpty() ? null : bookings.get(0);
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
        List<Flight> flights = entityManager.createQuery("select f from Flight f where lower(f.flightNumber) = :flightNumber", Flight.class).setParameter("flightNumber", flightIdentifier.trim().toLowerCase(Locale.ROOT)).getResultList();
        return flights.isEmpty() ? null : flights.get(0);
    }

    private PassengerProfile resolvePassenger(String passengerIdentifier) {
        Integer numericId = EntityLookupSupport.parseInteger(passengerIdentifier);
        if (numericId != null) {
            PassengerProfile passenger = entityManager.find(PassengerProfile.class, numericId);
            if (passenger != null) {
                return passenger;
            }
        }
        if (passengerIdentifier == null || passengerIdentifier.isBlank()) {
            return null;
        }
        List<PassengerProfile> passengers = entityManager.createQuery("select p from PassengerProfile p where lower(p.passportNumber) = :passport", PassengerProfile.class).setParameter("passport", passengerIdentifier.trim().toLowerCase(Locale.ROOT)).getResultList();
        return passengers.isEmpty() ? null : passengers.get(0);
    }

    private PassengerProfile resolvePassengerByName(String passengerName) {
        if (passengerName == null || passengerName.isBlank()) {
            return null;
        }
        String normalizedName = passengerName.trim().toLowerCase(Locale.ROOT);

        List<PassengerProfile> directMatches = entityManager.createQuery(
                        "select p from PassengerProfile p where lower(p.fullName) = :name",
                        PassengerProfile.class)
                .setParameter("name", normalizedName)
                .setMaxResults(1)
                .getResultList();
        if (!directMatches.isEmpty()) {
            return directMatches.get(0);
        }

        List<PassengerProfile> userNameMatches = entityManager.createQuery(
                        "select p from PassengerProfile p where p.user is not null and lower(p.user.name) = :name",
                        PassengerProfile.class)
                .setParameter("name", normalizedName)
                .setMaxResults(1)
                .getResultList();
        return userNameMatches.isEmpty() ? null : userNameMatches.get(0);
    }

    private String assignRandomSeatNumber(Flight flight) {
        if (flight == null || flight.getSeatCapacity() == null || flight.getSeatCapacity() <= 0) {
            return null;
        }

        int seatCapacity = flight.getSeatCapacity();
        Set<String> occupiedSeats = new HashSet<>(entityManager.createQuery(
                        "select b.seatNumber from Booking b where b.flight = :flight and b.status = :status and b.seatNumber is not null",
                        String.class)
                .setParameter("flight", flight)
                .setParameter("status", Booking.BookingStatus.Confirmed)
                .getResultList());

        if (occupiedSeats.size() >= seatCapacity) {
            return null;
        }

        List<String> availableSeats = new ArrayList<>();
        int rows = (seatCapacity + 5) / 6;
        char[] columns = {'A', 'B', 'C', 'D', 'E', 'F'};
        int seatIndex = 0;

        for (int row = 1; row <= rows; row++) {
            for (char column : columns) {
                seatIndex++;
                if (seatIndex > seatCapacity) {
                    break;
                }
                String seat = row + String.valueOf(column);
                if (!occupiedSeats.contains(seat)) {
                    availableSeats.add(seat);
                }
            }
        }

        if (availableSeats.isEmpty()) {
            return null;
        }

        int randomIndex = ThreadLocalRandom.current().nextInt(availableSeats.size());
        return availableSeats.get(randomIndex);
    }

    private Map<String, Object> bookingData(Booking booking) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", booking.getId());
        data.put("bookingReference", booking.getBookingReference());
        data.put("passengerId", booking.getPassenger() == null ? null : booking.getPassenger().getId());
        data.put("passengerName", booking.getPassenger() == null ? null : booking.getPassenger().getFullName());
        data.put("flightId", booking.getFlight() == null ? null : booking.getFlight().getId());
        data.put("flightNumber", booking.getFlight() == null ? null : booking.getFlight().getFlightNumber());
        data.put("seatNumber", booking.getSeatNumber());
        data.put("amount", booking.getAmount());
        data.put("currency", booking.getCurrency());
        data.put("status", booking.getStatus());
        data.put("bookedAt", booking.getBookedAt());
        data.put("cancelledAt", booking.getCancelledAt());
        data.put("createdAt", booking.getCreatedAt());
        data.put("updatedAt", booking.getUpdatedAt());
        return data;
    }

    private Map<String, Object> waitlistData(WaitlistEntry entry) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", entry.getId());
        data.put("flightId", entry.getFlight() == null ? null : entry.getFlight().getId());
        data.put("flightNumber", entry.getFlight() == null ? null : entry.getFlight().getFlightNumber());
        data.put("passengerId", entry.getPassenger() == null ? null : entry.getPassenger().getId());
        data.put("bookingId", entry.getBooking() == null ? null : entry.getBooking().getId());
        data.put("bookingReference", entry.getBooking() == null ? null : entry.getBooking().getBookingReference());
        data.put("position", entry.getPosition());
        data.put("status", entry.getStatus());
        data.put("joinedAt", entry.getJoinedAt());
        data.put("offeredAt", entry.getOfferedAt());
        data.put("expiresAt", entry.getExpiresAt());
        return data;
    }
}
