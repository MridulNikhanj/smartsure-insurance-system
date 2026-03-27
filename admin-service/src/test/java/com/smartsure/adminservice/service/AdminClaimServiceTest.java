package com.smartsure.adminservice.service;

import com.smartsure.adminservice.client.ClaimsClient;
import com.smartsure.adminservice.dto.ClaimReviewRequest;
import com.smartsure.adminservice.dto.ClaimResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminClaimServiceTest {

    @Mock
    private ClaimsClient claimsClient;

    @InjectMocks
    private AdminClaimService adminClaimService;

    private ClaimReviewRequest reviewRequest;
    private ClaimResponse claimResponse;

    @BeforeEach
    void setUp() {
        reviewRequest = new ClaimReviewRequest();
        reviewRequest.setStatus("APPROVED");
        reviewRequest.setRemarks("All documents verified");

        claimResponse = new ClaimResponse();
        claimResponse.setClaimId(1L);
        claimResponse.setPolicyId(5L);
        claimResponse.setDescription("Dengue hospitalization");
        claimResponse.setStatus("APPROVED");
        claimResponse.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void reviewClaim_CallsFeignClient_WithCorrectArguments() {
        when(claimsClient.reviewClaim(1L, reviewRequest)).thenReturn(claimResponse);

        ClaimResponse response = adminClaimService.reviewClaim(1L, reviewRequest);

        assertNotNull(response);
        assertEquals("APPROVED", response.getStatus());
        assertEquals(1L, response.getClaimId());
        // Verifies the Feign client was called exactly once with the right params
        verify(claimsClient, times(1)).reviewClaim(1L, reviewRequest);
    }

    @Test
    void reviewClaim_ReturnsRejectedStatus_WhenFeignReturnsRejected() {
        reviewRequest.setStatus("REJECTED");
        claimResponse.setStatus("REJECTED");

        when(claimsClient.reviewClaim(1L, reviewRequest)).thenReturn(claimResponse);

        ClaimResponse response = adminClaimService.reviewClaim(1L, reviewRequest);

        assertEquals("REJECTED", response.getStatus());
        verify(claimsClient, times(1)).reviewClaim(1L, reviewRequest);
    }

    @Test
    void reviewClaim_NeverCallsFeign_WhenExceptionThrown() {
        when(claimsClient.reviewClaim(99L, reviewRequest))
                .thenThrow(new RuntimeException("Claim not found"));

        assertThrows(RuntimeException.class,
                () -> adminClaimService.reviewClaim(99L, reviewRequest));

        verify(claimsClient, times(1)).reviewClaim(99L, reviewRequest);
    }
}