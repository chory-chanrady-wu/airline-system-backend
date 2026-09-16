package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "aircraft")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Aircraft {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "aircraft_seq_gen")
    @SequenceGenerator(name = "aircraft_seq_gen", sequenceName = "aircraft_seq", allocationSize = 1, initialValue = 10000000)
    @Column(nullable = false)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String registrationNumber;

    @Column(nullable = false)
    private String model;


    @Column(nullable = false)
    private Integer seatCapacity;

    @Column(nullable = false)
    private Boolean active;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
