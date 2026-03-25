package com.smartsure.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReportResponse {

    private Long totalClaims;
    private Long approvedClaims;
    private Long rejectedClaims;
    private Long pendingClaims;
}