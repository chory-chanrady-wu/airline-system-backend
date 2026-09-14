package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "flight_statuses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlightStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "flightstatus_seq_gen")
    @SequenceGenerator(name = "flightstatus_seq_gen", sequenceName = "flightstatus_seq", allocationSize = 1, initialValue = 10000000)
    @Column(nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flight_id", nullable = false)
    private Flight flight;

    @Column(nullable = false)
    private String status;

    @Column
    private Integer delayMinutes;

    @Column
    private String gate;

    @Column
    private String terminal;

    @Column
    private String remarks;

    @Column(nullable = false)
    private LocalDateTime recordedAt;

    @PrePersist
    protected void onCreate() {
        recordedAt = LocalDateTime.now();
    }
}
