package com.smartsure.adminservice.dto;

import lombok.Data;

@Data
public class PolicyTypeResponse {
    private Long id;
    private String name;
    private String description;
    private Double basePremium;
    private Integer durationInMonths;
}