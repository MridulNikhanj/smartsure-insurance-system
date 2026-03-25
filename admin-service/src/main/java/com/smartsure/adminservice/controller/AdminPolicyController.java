package com.smartsure.admin.controller;

import com.smartsure.admin.dto.PolicyRequest;
import com.smartsure.admin.service.AdminPolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/policies")
@RequiredArgsConstructor
public class AdminPolicyController {

    private final AdminPolicyService policyService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String createPolicy(@RequestBody PolicyRequest request) {
        return policyService.createPolicy(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String updatePolicy(@PathVariable Long id,
                               @RequestBody PolicyRequest request) {
        return policyService.updatePolicy(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deletePolicy(@PathVariable Long id) {
        return policyService.deletePolicy(id);
    }
}