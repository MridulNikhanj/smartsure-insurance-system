package com.smartsure.admin.dto;

import lombok.Data;

@Data
public class PolicyRequest {
    private String name;
    private String description;
    private Double basePremium;
    private Integer durationInMonths;
}