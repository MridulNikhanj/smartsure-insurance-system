package com.smartsure.policy.controller;

import com.smartsure.policy.dto.PolicyResponse;
import com.smartsure.policy.dto.PurchasePolicyRequest;
import com.smartsure.policy.service.PolicyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/policies")
@RequiredArgsConstructor
public class PolicyController {

    private final PolicyService policyService;

    @PostMapping("/purchase")
    @PreAuthorize("hasRole('CUSTOMER')")
    public PolicyResponse purchasePolicy(
            Authentication authentication,
            @Valid @RequestBody PurchasePolicyRequest request) {

        Long userId = Long.parseLong(authentication.getName());
        return policyService.purchasePolicy(userId, request);
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('CUSTOMER')")
    public List<PolicyResponse> getMyPolicies(Authentication authentication) {

        Long userId = Long.parseLong(authentication.getName());
        return policyService.getPoliciesByUser(userId);
    }

    @GetMapping("/{policyId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public PolicyResponse getPolicy(
            @PathVariable Long policyId,
            Authentication authentication) {

        Long userId = Long.parseLong(authentication.getName());
        return policyService.getPolicy(policyId, userId);
    }

    @GetMapping("/admin/count")
    @PreAuthorize("hasRole('ADMIN')")
    public Long getPolicyCount() {
        return policyService.getActivePolicyCount();
    }
}