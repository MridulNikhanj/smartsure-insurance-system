package com.smartsure.adminservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReportResponse {
    private Long totalClaims;
    private Long submittedClaims;
    private Long underReviewClaims;
    private Long approvedClaims;
    private Long rejectedClaims;
    private Long closedClaims;
    private Long activePolicies;
}