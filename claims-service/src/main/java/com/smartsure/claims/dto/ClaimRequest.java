package com.smartsure.claims.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ClaimRequest {

    @NotNull(message = "policyId is required")
    private Long policyId;

    @NotBlank(message = "description is required")
    private String description;

    // Optional: document ID returned from /api/claims/upload
    private Long documentId;
}