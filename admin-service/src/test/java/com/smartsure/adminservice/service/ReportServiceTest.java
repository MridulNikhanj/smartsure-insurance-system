package com.smartsure.adminservice.service;

import com.smartsure.adminservice.client.ClaimsClient;
import com.smartsure.adminservice.client.PolicyClient;
import com.smartsure.adminservice.dto.ClaimCountsResponse;
import com.smartsure.adminservice.dto.ReportResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private ClaimsClient claimsClient;

    @Mock
    private PolicyClient policyClient;

    @InjectMocks
    private ReportService reportService;

    private ClaimCountsResponse claimCounts;

    @BeforeEach
    void setUp() {
        claimCounts = new ClaimCountsResponse();
        claimCounts.setTotal(10L);
        claimCounts.setSubmitted(4L);
        claimCounts.setUnderReview(2L);
        claimCounts.setApproved(3L);
        claimCounts.setRejected(1L);
        claimCounts.setClosed(0L);
    }

    @Test
    void generateReport_ReturnsRealData_FromFeignClients() {
        when(claimsClient.getCounts()).thenReturn(claimCounts);
        when(policyClient.getPolicyCount()).thenReturn(5L);

        ReportResponse report = reportService.generateReport();

        assertNotNull(report);
        assertEquals(10L, report.getTotalClaims());
        assertEquals(4L,  report.getSubmittedClaims());
        assertEquals(2L,  report.getUnderReviewClaims());
        assertEquals(3L,  report.getApprovedClaims());
        assertEquals(1L,  report.getRejectedClaims());
        assertEquals(0L,  report.getClosedClaims());
        assertEquals(5L,  report.getActivePolicies());
    }

    @Test
    void generateReport_CallsBothFeignClients_ExactlyOnce() {
        when(claimsClient.getCounts()).thenReturn(claimCounts);
        when(policyClient.getPolicyCount()).thenReturn(5L);

        reportService.generateReport();

        // Ensures report always fetches from both sources
        verify(claimsClient,  times(1)).getCounts();
        verify(policyClient,  times(1)).getPolicyCount();
    }

    @Test
    void generateReport_ThrowsException_WhenClaimsFeignFails() {
        when(claimsClient.getCounts())
                .thenThrow(new RuntimeException("Claims service unavailable"));

        assertThrows(RuntimeException.class,
                () -> reportService.generateReport());
    }

    @Test
    void generateReport_ThrowsException_WhenPolicyFeignFails() {
        when(claimsClient.getCounts()).thenReturn(claimCounts);
        when(policyClient.getPolicyCount())
                .thenThrow(new RuntimeException("Policy service unavailable"));

        assertThrows(RuntimeException.class,
                () -> reportService.generateReport());
    }
}