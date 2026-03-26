package com.smartsure.claims.controller;

import com.smartsure.claims.dto.*;
import com.smartsure.claims.service.ClaimService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/claims")
@RequiredArgsConstructor
public class ClaimController {

    private final ClaimService claimService;

    // ── Customer endpoints ──────────────────────────────────────────────────

    @PostMapping("/upload")
    @PreAuthorize("hasRole('CUSTOMER')")
    public DocumentUploadResponse uploadFile(
            @RequestParam("file") MultipartFile file) throws Exception {
        return claimService.uploadDocument(file);
    }

    @PostMapping("/initiate")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ClaimResponse initiateClaim(
            Authentication authentication,
            @Valid @RequestBody ClaimRequest request) {

        Long userId = Long.parseLong(authentication.getName());
        return claimService.initiateClaim(userId, request);
    }

    @GetMapping("/status/{claimId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ClaimResponse getClaim(
            @PathVariable Long claimId,
            Authentication authentication) {

        Long userId = Long.parseLong(authentication.getName());
        return claimService.getClaim(claimId, userId);
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('CUSTOMER')")
    public List<ClaimResponse> getMyClaims(Authentication authentication) {

        Long userId = Long.parseLong(authentication.getName());
        return claimService.getUserClaims(userId);
    }

    // ── Admin internal endpoint (called by admin-service via Feign) ─────────

    @PutMapping("/admin/claims/{claimId}/review")
    @PreAuthorize("hasRole('ADMIN')")
    public ClaimResponse reviewClaim(
            @PathVariable Long claimId,
            @Valid @RequestBody ClaimReviewRequest request) {

        return claimService.reviewClaim(claimId, request);
    }

    // Add inside ClaimController, after the reviewClaim method:

    @GetMapping("/admin/counts")
    @PreAuthorize("hasRole('ADMIN')")
    public ClaimCountsResponse getClaimCounts() {
        return claimService.getClaimCounts();
    }
}