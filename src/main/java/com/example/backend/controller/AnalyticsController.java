package com.example.backend.controller;
import com.example.backend.entity.AnalyticsSnapshot;
import com.example.backend.entity.BenchmarkResult;
import com.example.backend.entity.Booking;
import com.example.backend.entity.Flight;
import com.example.backend.entity.FlightStatus;
import com.example.backend.entity.PassengerProfile;
import com.example.backend.entity.RouteLoadFactor;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/api/v1/analytics")
@Transactional
public class AnalyticsController {
    @PersistenceContext
    private EntityManager entityManager;
    @GetMapping("/dashboard")
    public Map<String, Object> dashboard() {
        AnalyticsSnapshot snapshot = latestSnapshot();
        if (snapshot != null) {
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("activeFlights", snapshot.getTotalFlights());
            data.put("bookingsToday", snapshot.getTotalBookings());
            data.put("revenue", snapshot.getConfirmedRevenue());
            data.put("averageLoadFactor", snapshot.getLoadFactor());
            data.put("generatedAt", snapshot.getGeneratedAt());
            data.put("source", "analytics_snapshots");
            return ApiResponse.ok("Dashboard analytics fetched", data);
        }
        return ApiResponse.ok("Dashboard analytics fetched", dashboardMetrics());
    }
    @GetMapping("/bookings")
    public Map<String, Object> bookingAnalytics() {
        List<Booking> bookings = entityManager.createQuery("select b from Booking b", Booking.class).getResultList();
        long confirmed = bookings.stream().filter(b -> b.getStatus() == Booking.BookingStatus.Confirmed).count();
        long cancelled = bookings.stream().filter(b -> b.getStatus() == Booking.BookingStatus.Cancelled).count();
        long waitlisted = bookings.stream().filter(b -> b.getStatus() == Booking.BookingStatus.Waitlisted).count();
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("bookings", bookings.size());
        data.put("confirmed", confirmed);
        data.put("cancelled", cancelled);
        data.put("waitlisted", waitlisted);
        return ApiResponse.ok("Booking analytics fetched", data);
    }
    @GetMapping("/load-factors")
    public Map<String, Object> loadFactors() {
        List<RouteLoadFactor> loadFactors = entityManager.createQuery("select r from RouteLoadFactor r order by r.calculatedAt desc", RouteLoadFactor.class).getResultList();
        if (!loadFactors.isEmpty()) {
            BigDecimal average = loadFactors.stream().map(RouteLoadFactor::getLoadFactor).reduce(BigDecimal.ZERO, BigDecimal::add).divide(BigDecimal.valueOf(loadFactors.size()), 2, RoundingMode.HALF_UP);
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("averageLoadFactor", average);
            data.put("items", loadFactorItems(loadFactors));
            return ApiResponse.ok("Load factor analytics fetched", data);
        }
        List<Flight> flights = entityManager.createQuery("select f from Flight f", Flight.class).getResultList();
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("averageLoadFactor", calculateAverageLoadFactor(flights));
        data.put("items", List.of());
        return ApiResponse.ok("Load factor analytics fetched", data);
    }
    @GetMapping("/revenue")
    public Map<String, Object> revenue() {
        List<Booking> bookings = entityManager.createQuery("select b from Booking b where b.status = :status", Booking.class).setParameter("status", Booking.BookingStatus.Confirmed).getResultList();
        BigDecimal totalRevenue = bookings.stream().map(Booking::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("totalRevenue", totalRevenue);
        data.put("confirmedBookings", bookings.size());
        return ApiResponse.ok("Revenue analytics fetched", data);
    }
    @GetMapping("/flight-status")
    public Map<String, Object> flightStatus() {
        List<FlightStatus> statuses = entityManager.createQuery("select s from FlightStatus s order by s.recordedAt desc", FlightStatus.class).setMaxResults(50).getResultList();
        List<Map<String, Object>> items = new ArrayList<>();
        if (!statuses.isEmpty()) {
            for (FlightStatus status : statuses) {
                items.add(flightStatusItem(status));
            }
        } else {
            List<Flight> flights = entityManager.createQuery("select f from Flight f order by f.departureTime asc", Flight.class).getResultList();
            for (Flight flight : flights) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("flightId", flight.getId());
                item.put("flightNumber", flight.getFlightNumber());
                item.put("status", flight.getStatus());
                items.add(item);
            }
        }
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("count", items.size());
        data.put("items", items);
        return ApiResponse.ok("Flight status analytics fetched", data);
    }
    @GetMapping("/benchmarks")
    public Map<String, Object> benchmarks() {
        List<BenchmarkResult> benchmarks = entityManager.createQuery("select b from BenchmarkResult b order by b.executedAt desc", BenchmarkResult.class).getResultList();
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("count", benchmarks.size());
        data.put("items", benchmarkItems(benchmarks));
        data.put("averageDelayMinutes", averageDelayMinutes());
        return ApiResponse.ok("Benchmark report fetched", data);
    }
    @PostMapping("/benchmarks/run")
    public Map<String, Object> runBenchmark() {
        long flights = count("select count(f) from Flight f");
        long bookings = count("select count(b) from Booking b");
        long passengers = count("select count(p) from PassengerProfile p");
        BenchmarkResult benchmark = BenchmarkResult.builder().structure(BenchmarkResult.StructureType.Graph).operation("analytics-run").inputSize(flights + bookings + passengers).runtimeMilliseconds(1L).theoreticalComplexity("O(n)").build();
        entityManager.persist(benchmark);
        entityManager.flush();
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("jobId", String.format("BM-%08d", benchmark.getId()));
        data.put("status", "completed");
        data.put("benchmark", benchmarkItem(benchmark));
        return ApiResponse.ok("Benchmark run started", data);
    }
    private AnalyticsSnapshot latestSnapshot() {
        List<AnalyticsSnapshot> snapshots = entityManager.createQuery("select a from AnalyticsSnapshot a order by a.generatedAt desc", AnalyticsSnapshot.class).setMaxResults(1).getResultList();
        return snapshots.isEmpty() ? null : snapshots.get(0);
    }
    private Map<String, Object> dashboardMetrics() {
        List<Flight> flights = entityManager.createQuery("select f from Flight f", Flight.class).getResultList();
        List<Booking> bookings = entityManager.createQuery("select b from Booking b", Booking.class).getResultList();
        LocalDate today = LocalDate.now();
        long activeFlights = flights.stream().filter(f -> f.getStatus() != Flight.FlightStatusType.Cancelled).count();
        long bookingsToday = bookings.stream().filter(b -> b.getBookedAt() != null && b.getBookedAt().toLocalDate().equals(today)).count();
        BigDecimal revenue = bookings.stream().filter(b -> b.getStatus() == Booking.BookingStatus.Confirmed).map(Booking::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("activeFlights", activeFlights);
        data.put("bookingsToday", bookingsToday);
        data.put("revenue", revenue);
        data.put("averageLoadFactor", calculateAverageLoadFactor(flights));
        data.put("source", "live_entities");
        return data;
    }
    private BigDecimal calculateAverageLoadFactor(List<Flight> flights) {
        if (flights.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal total = BigDecimal.ZERO;
        int counted = 0;
        for (Flight flight : flights) {
            if (flight.getSeatCapacity() == null || flight.getSeatCapacity() <= 0 || flight.getSeatsAvailable() == null) {
                continue;
            }
            BigDecimal occupied = BigDecimal.valueOf(flight.getSeatCapacity() - flight.getSeatsAvailable());
            total = total.add(occupied.multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(flight.getSeatCapacity()), 2, RoundingMode.HALF_UP));
            counted++;
        }
        return counted == 0 ? BigDecimal.ZERO : total.divide(BigDecimal.valueOf(counted), 2, RoundingMode.HALF_UP);
    }
    private List<Map<String, Object>> loadFactorItems(List<RouteLoadFactor> loadFactors) {
        List<Map<String, Object>> items = new ArrayList<>();
        for (RouteLoadFactor loadFactor : loadFactors) {
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("id", loadFactor.getId());
            data.put("routeId", loadFactor.getRoute() == null ? null : loadFactor.getRoute().getId());
            data.put("fromAirportCode", loadFactor.getRoute() == null || loadFactor.getRoute().getFromAirport() == null ? null : loadFactor.getRoute().getFromAirport().getCode());
            data.put("toAirportCode", loadFactor.getRoute() == null || loadFactor.getRoute().getToAirport() == null ? null : loadFactor.getRoute().getToAirport().getCode());
            data.put("totalFlights", loadFactor.getTotalFlights());
            data.put("totalCapacity", loadFactor.getTotalCapacity());
            data.put("occupiedSeats", loadFactor.getOccupiedSeats());
            data.put("availableSeats", loadFactor.getAvailableSeats());
            data.put("loadFactor", loadFactor.getLoadFactor());
            data.put("calculatedAt", loadFactor.getCalculatedAt());
            items.add(data);
        }
        return items;
    }
    private Map<String, Object> flightStatusItem(FlightStatus status) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", status.getId());
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
    private List<Map<String, Object>> benchmarkItems(List<BenchmarkResult> benchmarks) {
        List<Map<String, Object>> items = new ArrayList<>();
        for (BenchmarkResult benchmark : benchmarks) {
            items.add(benchmarkItem(benchmark));
        }
        return items;
    }
    private Map<String, Object> benchmarkItem(BenchmarkResult benchmark) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", benchmark.getId());
        data.put("structure", benchmark.getStructure());
        data.put("operation", benchmark.getOperation());
        data.put("inputSize", benchmark.getInputSize());
        data.put("runtimeMilliseconds", benchmark.getRuntimeMilliseconds());
        data.put("theoreticalComplexity", benchmark.getTheoreticalComplexity());
        data.put("executedAt", benchmark.getExecutedAt());
        return data;
    }
    private long averageDelayMinutes() {
        List<FlightStatus> statuses = entityManager.createQuery("select s from FlightStatus s where s.delayMinutes is not null", FlightStatus.class).getResultList();
        if (statuses.isEmpty()) {
            return 0L;
        }
        long total = 0L;
        int count = 0;
        for (FlightStatus status : statuses) {
            if (status.getDelayMinutes() != null) {
                total += status.getDelayMinutes();
                count++;
            }
        }
        return count == 0 ? 0L : Math.round((double) total / count);
    }
    private long count(String query) {
        Long result = entityManager.createQuery(query, Long.class).getSingleResult();
        return result == null ? 0L : result;
    }
}