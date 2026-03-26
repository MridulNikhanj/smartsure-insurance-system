package com.smartsure.policy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class PolicyResponse {
    private Long policyId;
    private String policyTypeName;
    private Double premium;
    private String status;
    private LocalDate startDate;
    private LocalDate endDate;
}