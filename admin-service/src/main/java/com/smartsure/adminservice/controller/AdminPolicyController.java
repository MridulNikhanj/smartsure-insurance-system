package com.smartsure.adminservice.controller;

import com.smartsure.adminservice.dto.PolicyRequest;
import com.smartsure.adminservice.dto.PolicyTypeResponse;
import com.smartsure.adminservice.service.AdminPolicyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/policies")
@RequiredArgsConstructor
public class AdminPolicyController {

    private final AdminPolicyService policyService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public PolicyTypeResponse createPolicy(@Valid @RequestBody PolicyRequest request) {
        return policyService.createPolicy(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public PolicyTypeResponse updatePolicy(
            @PathVariable Long id,
            @Valid @RequestBody PolicyRequest request) {
        return policyService.updatePolicy(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deletePolicy(@PathVariable Long id) {
        return policyService.deletePolicy(id);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<PolicyTypeResponse> getAllPolicies() {
        return policyService.getAllPolicies();
    }
}