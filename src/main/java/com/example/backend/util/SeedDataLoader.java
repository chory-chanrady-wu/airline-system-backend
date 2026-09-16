package com.example.backend.util;
import com.example.backend.entity.*;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.PasswordHasher;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Configuration
public class SeedDataLoader {
    @PersistenceContext
    private EntityManager entityManager;
    @Bean
    public CommandLineRunner seedDatabase(UserRepository userRepository, PlatformTransactionManager transactionManager) {
        return args -> {
            TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
            transactionTemplate.executeWithoutResult(status -> {
                if (userRepository.count() > 0) {
                    return;
                }
                seedPermissions();
                Role adminRole = seedRole("Administrator", "Full access to the platform",
                        List.of("USERS_READ", "USERS_WRITE", "AIRPORTS_READ", "FLIGHTS_READ", "BOOKINGS_READ", "BOOKINGS_WRITE"));
                Role agentRole = seedRole("Operations Agent", "Handles flights and bookings",
                        List.of("AIRPORTS_READ", "FLIGHTS_READ", "BOOKINGS_READ", "BOOKINGS_WRITE"));
                Role passengerRole = seedRole("Passenger", "Can browse flights and manage own bookings",
                        List.of("FLIGHTS_READ", "BOOKINGS_READ", "BOOKINGS_WRITE"));
                seedUser("System Admin", "admin@flightbooker.local", adminRole,
                        User.UserStatus.Active, "Admin@123");
                seedUser("Aviation Agent", "agent@flightbooker.local", agentRole,
                        User.UserStatus.Active, "Agent@123");
                User passenger = seedUser("Raka Pratama", "raka@flightbooker.local", passengerRole,
                        User.UserStatus.Active, "Passenger@123");
                Airport cgk = seedAirport("CGK", "Jakarta", "Indonesia", "Asia/Jakarta");
                Airport sin = seedAirport("SIN", "Singapore", "Singapore", "Asia/Singapore");
                Airport kul = seedAirport("KUL", "Kuala Lumpur", "Malaysia", "Asia/Kuala_Lumpur");
                Airline garuda = seedAirline("GA", "Garuda Indonesia", true,
                        "https://example.com/logos/garuda-indonesia.png");
                Aircraft aircraft = seedAircraft("PK-GAA", "Boeing 737-800", 180, true);
                Route cgkToSin = seedRoute(cgk, sin, 900, 95, true);
                Route sinToKul = seedRoute(sin, kul, 315, 55, true);
                LocalDateTime tomorrowMorning = LocalDateTime.now().plusDays(1).withHour(8).withMinute(30).withSecond(0).withNano(0);
                LocalDateTime tomorrowMidday = tomorrowMorning.plusHours(3).plusMinutes(20);
                Flight flight1 = seedFlight("GA-101", garuda, aircraft, cgkToSin, cgk, sin,
                        tomorrowMorning, tomorrowMorning.plusHours(1).plusMinutes(35),
                        new BigDecimal("145.00"), 180, 180, Flight.FlightStatusType.Scheduled);
                Flight flight2 = seedFlight("GA-202", garuda, aircraft, sinToKul, sin, kul,
                        tomorrowMidday, tomorrowMidday.plusMinutes(55),
                        new BigDecimal("99.00"), 180, 42, Flight.FlightStatusType.Boarding);
                PassengerProfile passengerProfile = seedPassengerProfile(passenger, "P12345678", "Indonesian",
                        "+62-812-3456-7890", LocalDate.of(1996, 5, 21), "Maya Pratama (+62-811-2222-3333)");
                seedBooking("BK-20260914-001", passengerProfile, flight1, "12A", new BigDecimal("145.00"),
                        "USD", Booking.BookingStatus.Confirmed, null);
                seedBooking("BK-20260914-002", passengerProfile, flight2, null, new BigDecimal("99.00"),
                        "USD", Booking.BookingStatus.Waitlisted, null);
                seedFlightStatus(flight1, "Scheduled", null, "A12", "T3", "On time and ready for boarding");
                seedFlightStatus(flight2, "Boarding", null, "B07", "T2", "Final call in 15 minutes");
            });
        };
    }
    private void seedPermissions() {
        persistPermission("USERS_READ", "Read users", "Allows viewing user accounts");
        persistPermission("USERS_WRITE", "Manage users", "Allows creating and updating users");
        persistPermission("AIRPORTS_READ", "Read airports", "Allows viewing airport data");
        persistPermission("FLIGHTS_READ", "Read flights", "Allows viewing flight schedules");
        persistPermission("BOOKINGS_READ", "Read bookings", "Allows viewing booking records");
        persistPermission("BOOKINGS_WRITE", "Manage bookings", "Allows creating and updating bookings");
    }
    private void persistPermission(String code, String name, String description) {
        Permission permission = Permission.builder()
                .code(code)
                .name(name)
                .description(description)
                .build();
        entityManager.persist(permission);
    }
    private Role seedRole(String name, String description, List<String> permissions) {
        Role role = Role.builder()
                .name(name)
                .description(description)
                .permissions(new ArrayList<>(permissions))
                .build();
        entityManager.persist(role);
        return role;
    }
    private User seedUser(String name, String email, Role role, User.UserStatus status, String rawPassword) {
        User user = User.builder()
                .name(name)
                .email(email)
                .passwordHash(PasswordHasher.sha256(rawPassword))
                .role(role)
                .status(status)
                .userType(User.UserType.SYSTEM_USER)
                .build();
        entityManager.persist(user);
        return user;
    }
    private Airport seedAirport(String code, String city, String country, String timezone) {
        Airport airport = Airport.builder()
                .code(code)
                .city(city)
                .country(country)
                .latitude(BigDecimal.ZERO)
                .longitude(BigDecimal.ZERO)
                .timezone(timezone)
                .build();
        entityManager.persist(airport);
        return airport;
    }
    private Airline seedAirline(String code, String name, boolean active, String logoUrl) {
        Airline airline = Airline.builder()
                .code(code)
                .name(name)
                .logoUrl(logoUrl)
                .active(active)
                .build();
        entityManager.persist(airline);
        return airline;
    }
    private Aircraft seedAircraft(String registrationNumber, String model, int seatCapacity, boolean active) {
        Aircraft aircraft = Aircraft.builder()
                .registrationNumber(registrationNumber)
                .model(model)
                .seatCapacity(seatCapacity)
                .active(active)
                .build();
        entityManager.persist(aircraft);
        return aircraft;
    }
    private Route seedRoute(Airport fromAirport, Airport toAirport, int distanceKm, int durationMinutes, boolean active) {
        Route route = Route.builder()
                .fromAirport(fromAirport)
                .toAirport(toAirport)
                .distanceKm(distanceKm)
                .durationMinutes(durationMinutes)
                .active(active)
                .build();
        entityManager.persist(route);
        return route;
    }
    private Flight seedFlight(String flightNumber, Airline airline, Aircraft aircraft, Route route,
                              Airport fromAirport, Airport toAirport, LocalDateTime departureTime,
                              LocalDateTime arrivalTime, BigDecimal price, int seatCapacity,
                              int seatsAvailable, Flight.FlightStatusType status) {
        Flight flight = Flight.builder()
                .flightNumber(flightNumber)
                .airline(airline)
                .aircraft(aircraft)
                .route(route)
                .fromAirport(fromAirport)
                .toAirport(toAirport)
                .departureTime(departureTime)
                .arrivalTime(arrivalTime)
                .price(price)
                .seatCapacity(seatCapacity)
                .seatsAvailable(seatsAvailable)
                .status(status)
                .build();
        entityManager.persist(flight);
        return flight;
    }
    private PassengerProfile seedPassengerProfile(User user, String passportNumber, String nationality, String phone,
                                                  LocalDate dateOfBirth, String emergencyContact) {
        PassengerProfile profile = PassengerProfile.builder()
                .user(user)
                .passportNumber(passportNumber)
                .nationality(nationality)
                .phone(phone)
                .dateOfBirth(dateOfBirth)
                .emergencyContact(emergencyContact)
                .build();
        entityManager.persist(profile);
        return profile;
    }
    private Booking seedBooking(String bookingReference, PassengerProfile passenger, Flight flight, String seatNumber,
                                BigDecimal amount, String currency, Booking.BookingStatus status,
                                LocalDateTime cancelledAt) {
        Booking booking = Booking.builder()
                .bookingReference(bookingReference)
                .passenger(passenger)
                .flight(flight)
                .seatNumber(seatNumber)
                .amount(amount)
                .currency(currency)
                .status(status)
                .cancelledAt(cancelledAt)
                .build();
        entityManager.persist(booking);
        return booking;
    }
    private FlightStatus seedFlightStatus(Flight flight, String status, Integer delayMinutes, String gate,
                                          String terminal, String remarks) {
        FlightStatus flightStatus = FlightStatus.builder()
                .flight(flight)
                .status(status)
                .delayMinutes(delayMinutes)
                .gate(gate)
                .terminal(terminal)
                .remarks(remarks)
                .build();
        entityManager.persist(flightStatus);
        return flightStatus;
    }
}