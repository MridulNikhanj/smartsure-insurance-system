package com.smartsure.admin.client;

import com.smartsure.admin.dto.ClaimReviewRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "CLAIMS-SERVICE")
public interface ClaimsClient {

    @PutMapping("/api/admin/claims/{claimId}/review")
    String reviewClaim(@PathVariable Long claimId,
                       @RequestBody ClaimReviewRequest request);
}