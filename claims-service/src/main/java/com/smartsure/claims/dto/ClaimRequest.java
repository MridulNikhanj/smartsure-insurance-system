package com.smartsure.claims.dto;

import lombok.Data;

@Data
public class ClaimRequest {
    private Long policyId;
    private String description;
}