package com.prescription.backend.repository;

import com.prescription.backend.entity.PrescriptionRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PrescriptionRepository extends JpaRepository<PrescriptionRecommendation, Long> {
    Optional<PrescriptionRecommendation> findByImageHash(String imageHash);
}
