package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "radar_snapshots")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RadarSnapshot {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "radarsnapshot_seq_gen")
    @SequenceGenerator(name = "radarsnapshot_seq_gen", sequenceName = "radarsnapshot_seq", allocationSize = 1, initialValue = 10000000)
    @Column(nullable = false)
    private Integer id;

    @Column(nullable = false)
    private String provider;

    @Column(nullable = false)
    private Integer aircraftCount;

    @Column(nullable = false)
    private LocalDateTime fetchedAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @PrePersist
    protected void onCreate() {
        fetchedAt = LocalDateTime.now();
    }
}
