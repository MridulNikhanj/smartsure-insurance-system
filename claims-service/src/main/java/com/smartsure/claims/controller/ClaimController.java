package com.smartsure.claims.controller;

import com.smartsure.claims.dto.ClaimRequest;
import com.smartsure.claims.dto.ClaimResponse;
import com.smartsure.claims.entity.Claim;
import com.smartsure.claims.service.ClaimService;
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

    // ✅ Upload document
    @PostMapping("/upload")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String uploadFile(@RequestParam("file") MultipartFile file) throws Exception {
        return claimService.uploadDocument(file);
    }

    // ✅ Initiate claim
    @PostMapping("/initiate")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ClaimResponse initiateClaim(
            Authentication authentication,
            @RequestBody ClaimRequest request) {

        Long userId = Long.parseLong(authentication.getName());
        return claimService.initiateClaim(userId, request);
    }

    // ✅ Get single claim
    @GetMapping("/status/{claimId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public Claim getClaim(
            @PathVariable Long claimId,
            Authentication authentication) {

        Long userId = Long.parseLong(authentication.getName());
        return claimService.getClaim(claimId, userId);
    }

    // ✅ Get all claims of user
    @GetMapping("/my")
    @PreAuthorize("hasRole('CUSTOMER')")
    public List<Claim> getMyClaims(Authentication authentication) {

        Long userId = Long.parseLong(authentication.getName());
        return claimService.getUserClaims(userId);
    }
}