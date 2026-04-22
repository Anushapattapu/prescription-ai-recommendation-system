package com.prescription.backend.service;

import com.prescription.backend.dto.PrescriptionResponse;
import com.prescription.backend.entity.PrescriptionRecommendation;
import com.prescription.backend.repository.PrescriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class PrescriptionServiceTest {

    private PrescriptionRepository repository;
    private GeminiAiService aiService;
    private PrescriptionService prescriptionService;

    @BeforeEach
    void setUp() {
        repository = mock(PrescriptionRepository.class);
        aiService = mock(GeminiAiService.class);
        prescriptionService = new PrescriptionService(repository, aiService);
    }

    @Test
    void testQuotaLogic_DbFirst() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "dummy_image_content".getBytes());

        // Scenario 1: First upload (Not in DB)
        when(repository.findByImageHash(anyString())).thenReturn(Optional.empty());
        when(aiService.analyzePrescription(anyString(), anyString())).thenReturn(Mono.just("Test Recommendations"));
        
        PrescriptionResponse response1 = prescriptionService.processPrescription(file).block();
        
        assertNotNull(response1);
        assertEquals("Test Recommendations", response1.getRecommendations());
        assertFalse(response1.isFromCache(), "Should not be from cache on first upload");
        verify(aiService, times(1)).analyzePrescription(anyString(), anyString());
        verify(repository, times(1)).save(any(PrescriptionRecommendation.class));

        // Scenario 2: Second upload (Exists in DB)
        PrescriptionRecommendation cachedRec = new PrescriptionRecommendation();
        cachedRec.setRecommendations("Test Recommendations");
        cachedRec.setImageHash("hashedValue");
        
        when(repository.findByImageHash(anyString())).thenReturn(Optional.of(cachedRec));

        PrescriptionResponse response2 = prescriptionService.processPrescription(file).block();
        
        assertNotNull(response2);
        assertEquals("Test Recommendations", response2.getRecommendations());
        assertTrue(response2.isFromCache(), "Should be from cache on second upload");
        
        // Ensure AI service is STILL only called once total
        verify(aiService, times(1)).analyzePrescription(anyString(), anyString());
    }
}
