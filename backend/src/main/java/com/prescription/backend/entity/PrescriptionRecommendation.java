package com.prescription.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "prescription_recommendations")
@Data
@NoArgsConstructor
public class PrescriptionRecommendation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String imageHash;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String recommendations;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
