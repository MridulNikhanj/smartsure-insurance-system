package com.smartsure.admin.service;

import com.smartsure.admin.client.ClaimsClient;
import com.smartsure.admin.dto.ClaimReviewRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminClaimService {

    private final ClaimsClient claimsClient;

    public String reviewClaim(Long claimId, ClaimReviewRequest request) {
        return claimsClient.reviewClaim(claimId, request);
    }
}