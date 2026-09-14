package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "permissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "permission_seq_gen")
    @SequenceGenerator(name = "permission_seq_gen", sequenceName = "permission_seq", allocationSize = 1, initialValue = 10000000)
    @Column(nullable = false)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @PrePersist
    protected void onCreate() {
    }
}
