package com.smartsure.adminservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class PolicyRequest {

    @NotBlank(message = "name is required")
    private String name;

    private String description;

    @NotNull
    @Positive(message = "basePremium must be positive")
    private Double basePremium;

    @NotNull
    @Positive(message = "durationInMonths must be positive")
    private Integer durationInMonths;
}