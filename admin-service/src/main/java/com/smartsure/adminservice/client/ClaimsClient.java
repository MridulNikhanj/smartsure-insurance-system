package com.smartsure.adminservice.client;

import com.smartsure.adminservice.config.FeignClientConfig;
import com.smartsure.adminservice.dto.ClaimCountsResponse;
import com.smartsure.adminservice.dto.ClaimReviewRequest;
import com.smartsure.adminservice.dto.ClaimResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "CLAIMS-SERVICE", configuration = FeignClientConfig.class)
public interface ClaimsClient {

    @PutMapping("/api/claims/admin/claims/{claimId}/review")
    ClaimResponse reviewClaim(@PathVariable Long claimId,
                              @RequestBody ClaimReviewRequest request);

    @GetMapping("/api/claims/admin/counts")
    ClaimCountsResponse getCounts();
}