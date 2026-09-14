package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "itineraries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Itinerary {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "itinerary_seq_gen")
    @SequenceGenerator(name = "itinerary_seq_gen", sequenceName = "itinerary_seq", allocationSize = 1, initialValue = 10000000)
    @Column(nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "passenger_id", nullable = false)
    private PassengerProfile passenger;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @Column(nullable = false)
    private String originAirportCode;

    @Column(nullable = false)
    private String destinationAirportCode;

    @ElementCollection
    @CollectionTable(name = "itinerary_path", joinColumns = @JoinColumn(name = "itinerary_id"))
    @Column(name = "path_item")
    private List<String> path = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "itinerary_layovers", joinColumns = @JoinColumn(name = "itinerary_id"))
    @Column(name = "layover")
    private List<String> layovers = new ArrayList<>();

    @Column(nullable = false)
    private Integer stops;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @Column(nullable = false)
    private Integer durationMinutes;

    @Column(nullable = false)
    private String algorithm;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
