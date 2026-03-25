package com.smartsure.policy.controller;

import com.smartsure.policy.entity.PolicyType;
import com.smartsure.policy.service.PolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/policies")
@RequiredArgsConstructor
public class AdminPolicyController {

    private final PolicyService policyService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public PolicyType createPolicy(@RequestBody PolicyType policyType) {
        return policyService.createPolicyType(policyType);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public PolicyType updatePolicy(
            @PathVariable Long id,
            @RequestBody PolicyType policyType) {

        return policyService.updatePolicyType(id, policyType);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deletePolicy(@PathVariable Long id) {
        return policyService.deletePolicyType(id);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<PolicyType> getAllPolicies() {
        return policyService.getAllPolicyTypes();
    }
}