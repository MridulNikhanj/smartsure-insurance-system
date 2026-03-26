package com.smartsure.adminservice.service;

import com.smartsure.adminservice.client.ClaimsClient;
import com.smartsure.adminservice.dto.ClaimReviewRequest;
import com.smartsure.adminservice.dto.ClaimResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminClaimService {

    private final ClaimsClient claimsClient;

    public ClaimResponse reviewClaim(Long claimId, ClaimReviewRequest request) {
        return claimsClient.reviewClaim(claimId, request);
    }
}