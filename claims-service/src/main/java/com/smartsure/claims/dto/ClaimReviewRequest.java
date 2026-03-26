package com.smartsure.claims.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ClaimReviewRequest {

    @NotBlank(message = "status is required (APPROVED or REJECTED)")
    private String status;

    private String remarks;
}