package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "benchmark_results")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BenchmarkResult {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "benchmarkresult_seq_gen")
    @SequenceGenerator(name = "benchmarkresult_seq_gen", sequenceName = "benchmarkresult_seq", allocationSize = 1, initialValue = 10000000)
    @Column(nullable = false)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StructureType structure;

    @Column(nullable = false)
    private String operation;

    @Column(nullable = false)
    private Long inputSize;

    @Column(nullable = false)
    private Long runtimeMilliseconds;

    @Column(nullable = false)
    private String theoreticalComplexity;

    @Column(nullable = false)
    private LocalDateTime executedAt;

    @PrePersist
    protected void onCreate() {
        executedAt = LocalDateTime.now();
    }

    public enum StructureType {
        HashTable,
        AVLTree,
        Graph,
        Dijkstra,
        BFS,
        Queue,
        Stack
    }
}
