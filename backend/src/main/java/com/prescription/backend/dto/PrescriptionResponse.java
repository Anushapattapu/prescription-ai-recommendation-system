package com.prescription.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PrescriptionResponse {
    private String recommendations;
    private boolean fromCache;
}
