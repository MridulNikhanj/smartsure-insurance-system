package com.smartsure.claims.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ClaimResponse {

    private Long claimId;
    private String status;
}