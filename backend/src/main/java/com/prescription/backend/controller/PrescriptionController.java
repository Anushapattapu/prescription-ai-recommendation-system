package com.prescription.backend.controller;

import com.prescription.backend.dto.PrescriptionResponse;
import com.prescription.backend.service.PrescriptionService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/prescriptions")
@CrossOrigin(origins = "*") // Allows Android app or any other client to access
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    public PrescriptionController(PrescriptionService prescriptionService) {
        this.prescriptionService = prescriptionService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<PrescriptionResponse> uploadPrescription(@RequestParam("file") MultipartFile file) {
        return prescriptionService.processPrescription(file);
    }
}
