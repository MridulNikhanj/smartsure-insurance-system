package com.smartsure.policy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PolicyTypeResponse {
    private Long id;
    private String name;
    private String description;
    private Double basePremium;
    private Integer durationInMonths;
}