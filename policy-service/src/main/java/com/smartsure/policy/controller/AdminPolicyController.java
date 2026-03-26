package com.smartsure.policy.controller;

import com.smartsure.policy.dto.PolicyTypeResponse;
import com.smartsure.policy.entity.PolicyType;
import com.smartsure.policy.service.PolicyService;
import jakarta.validation.Valid;
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
    public PolicyTypeResponse createPolicy(@Valid @RequestBody PolicyType policyType) {
        return policyService.createPolicyType(policyType);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public PolicyTypeResponse updatePolicy(
            @PathVariable Long id,
            @Valid @RequestBody PolicyType policyType) {
        return policyService.updatePolicyType(id, policyType);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deletePolicy(@PathVariable Long id) {
        return policyService.deletePolicyType(id);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<PolicyTypeResponse> getAllPolicies() {
        return policyService.getAllPolicyTypes();
    }
}