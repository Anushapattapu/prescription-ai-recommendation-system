package com.prescription.backend.service;

import com.prescription.backend.dto.PrescriptionResponse;
import com.prescription.backend.entity.PrescriptionRecommendation;
import com.prescription.backend.repository.PrescriptionRepository;
import com.prescription.backend.utils.HashUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Base64;
import java.util.Optional;

@Service
public class PrescriptionService {

    private final PrescriptionRepository repository;
    private final GeminiAiService aiService;

    public PrescriptionService(PrescriptionRepository repository, GeminiAiService aiService) {
        this.repository = repository;
        this.aiService = aiService;
    }

    public Mono<PrescriptionResponse> processPrescription(MultipartFile file) {
        try {
            String fileHash = HashUtils.hashFile(file);

            // DB-First Strategy: Check Quota / Cache
            Optional<PrescriptionRecommendation> existing = repository.findByImageHash(fileHash);
            if (existing.isPresent()) {
                return Mono.just(new PrescriptionResponse(existing.get().getRecommendations(), true));
            }

            // Convert to Base64
            String base64Image = Base64.getEncoder().encodeToString(file.getBytes());
            String mimeType = file.getContentType();

            // Call external AI
            return aiService.analyzePrescription(base64Image, mimeType)
                    .map(recommendation -> {
                        // Save new recommendation to DB
                        PrescriptionRecommendation rec = new PrescriptionRecommendation();
                        rec.setImageHash(fileHash);
                        rec.setRecommendations(recommendation);
                        repository.save(rec);

                        return new PrescriptionResponse(recommendation, false);
                    });

        } catch (IOException e) {
            return Mono.error(new RuntimeException("Failed to read file", e));
        }
    }
}
