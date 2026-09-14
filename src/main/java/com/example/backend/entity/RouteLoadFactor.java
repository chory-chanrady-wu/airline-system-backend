package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "route_load_factors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteLoadFactor {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "routeloadfactor_seq_gen")
    @SequenceGenerator(name = "routeloadfactor_seq_gen", sequenceName = "routeloadfactor_seq", allocationSize = 1, initialValue = 10000000)
    @Column(nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @Column(nullable = false)
    private Long totalFlights;

    @Column(nullable = false)
    private Long totalCapacity;

    @Column(nullable = false)
    private Long occupiedSeats;

    @Column(nullable = false)
    private Long availableSeats;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal loadFactor;

    @Column(nullable = false)
    private LocalDateTime calculatedAt;

    @PrePersist
    protected void onCreate() {
        calculatedAt = LocalDateTime.now();
    }
}
