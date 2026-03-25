package com.smartsure.admin.service;

import com.smartsure.admin.dto.ReportResponse;
import org.springframework.stereotype.Service;

@Service
public class ReportService {

    public ReportResponse generateReport() {

        // 🚧 TEMPORARY (dummy data)
        // Later → fetch via Feign from Claims Service / Policy Service

        long totalClaims = 100;
        long approvedClaims = 60;
        long rejectedClaims = 20;
        long pendingClaims = 20;

        return new ReportResponse(
                totalClaims,
                approvedClaims,
                rejectedClaims,
                pendingClaims
        );
    }
}