package com.smartsure.adminservice.controller;

import com.smartsure.adminservice.dto.ClaimReviewRequest;
import com.smartsure.adminservice.dto.ClaimResponse;
import com.smartsure.adminservice.service.AdminClaimService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/claims")
@RequiredArgsConstructor
public class AdminClaimController {

    private final AdminClaimService claimService;

    @PutMapping("/{claimId}/review")
    @PreAuthorize("hasRole('ADMIN')")
    public ClaimResponse reviewClaim(
            @PathVariable Long claimId,
            @Valid @RequestBody ClaimReviewRequest request) {

        return claimService.reviewClaim(claimId, request);
    }
}