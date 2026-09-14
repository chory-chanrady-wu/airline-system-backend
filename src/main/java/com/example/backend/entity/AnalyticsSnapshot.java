package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "analytics_snapshots")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalyticsSnapshot {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "analyticssnapshot_seq_gen")
    @SequenceGenerator(name = "analyticssnapshot_seq_gen", sequenceName = "analyticssnapshot_seq", allocationSize = 1, initialValue = 10000000)
    @Column(nullable = false)
    private Integer id;

    @Column(nullable = false)
    private Long totalFlights;

    @Column(nullable = false)
    private Long totalBookings;

    @Column(nullable = false)
    private Long totalPassengers;

    @Column(nullable = false)
    private Long totalAirports;

    @Column(nullable = false)
    private Long totalRoutes;

    @Column(nullable = false)
    private Long confirmedBookings;

    @Column(nullable = false)
    private Long cancelledBookings;

    @Column(nullable = false)
    private Long waitlistedBookings;

    @Column(nullable = false)
    private Long totalCapacity;

    @Column(nullable = false)
    private Long occupiedSeats;

    @Column(nullable = false)
    private Long availableSeats;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal loadFactor;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal confirmedRevenue;

    @Column(nullable = false)
    private LocalDateTime generatedAt;

    @PrePersist
    protected void onCreate() {
        generatedAt = LocalDateTime.now();
    }
}
