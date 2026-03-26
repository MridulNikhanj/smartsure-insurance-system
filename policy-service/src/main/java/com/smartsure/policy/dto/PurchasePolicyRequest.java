package com.smartsure.policy.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PurchasePolicyRequest {

    @NotNull(message = "policyTypeId is required")
    private Long policyTypeId;
}