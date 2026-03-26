package com.smartsure.adminservice.service;

import com.smartsure.adminservice.client.ClaimsClient;
import com.smartsure.adminservice.client.PolicyClient;
import com.smartsure.adminservice.dto.ClaimCountsResponse;
import com.smartsure.adminservice.dto.ReportResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ClaimsClient claimsClient;
    private final PolicyClient policyClient;

    public ReportResponse generateReport() {
        ClaimCountsResponse counts = claimsClient.getCounts();
        Long activePolicies = policyClient.getPolicyCount();

        return new ReportResponse(
                counts.getTotal(),
                counts.getSubmitted(),
                counts.getUnderReview(),
                counts.getApproved(),
                counts.getRejected(),
                counts.getClosed(),
                activePolicies
        );
    }
}