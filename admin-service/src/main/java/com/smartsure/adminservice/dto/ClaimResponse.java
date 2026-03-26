package com.smartsure.adminservice.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ClaimResponse {
    private Long claimId;
    private Long policyId;
    private String description;
    private String status;
    private LocalDateTime createdAt;
}