package com.smartsure.admin.controller;

import com.smartsure.admin.dto.ClaimReviewRequest;
import com.smartsure.admin.service.AdminClaimService;
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
    public String reviewClaim(
            @PathVariable Long claimId,
            @RequestBody ClaimReviewRequest request) {

        return claimService.reviewClaim(claimId, request);
    }
}