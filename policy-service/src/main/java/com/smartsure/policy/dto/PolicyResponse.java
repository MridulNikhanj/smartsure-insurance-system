package com.smartsure.policy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PolicyResponse {
    private Long policyId;
    private Double premium;
    private String status;
}