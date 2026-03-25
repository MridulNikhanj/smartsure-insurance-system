package com.smartsure.policy.controller;

import com.smartsure.policy.dto.PolicyResponse;
import com.smartsure.policy.dto.PurchasePolicyRequest;
import com.smartsure.policy.entity.Policy;
import com.smartsure.policy.service.PolicyService;
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

    // ✅ Purchase Policy
    @PostMapping("/purchase")
    @PreAuthorize("hasRole('CUSTOMER')")
    public PolicyResponse purchasePolicy(
            Authentication authentication,
            @RequestBody PurchasePolicyRequest request) {

        Long userId = Long.parseLong(authentication.getName());

        return policyService.purchasePolicy(userId, request);
    }

    // ✅ Get All Policies of Logged-in User
    @GetMapping("/my")
    @PreAuthorize("hasRole('CUSTOMER')")
    public List<Policy> getMyPolicies(Authentication authentication) {

        Long userId = Long.parseLong(authentication.getName());

        return policyService.getPoliciesByUser(userId);
    }

    // ✅ Get Policy by ID (SECURE)
    @GetMapping("/{policyId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public Policy getPolicy(
            @PathVariable Long policyId,
            Authentication authentication) {

        Long userId = Long.parseLong(authentication.getName());

        return policyService.getPolicy(policyId, userId);
    }
}