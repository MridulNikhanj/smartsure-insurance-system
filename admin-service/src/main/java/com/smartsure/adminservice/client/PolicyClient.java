package com.smartsure.adminservice.client;

import com.smartsure.adminservice.config.FeignClientConfig;
import com.smartsure.adminservice.dto.PolicyRequest;
import com.smartsure.adminservice.dto.PolicyTypeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "POLICY-SERVICE", configuration = FeignClientConfig.class)
public interface PolicyClient {

    @PostMapping("/api/admin/policies")
    PolicyTypeResponse createPolicy(@RequestBody PolicyRequest request);

    @PutMapping("/api/admin/policies/{id}")
    PolicyTypeResponse updatePolicy(@PathVariable Long id,
                                    @RequestBody PolicyRequest request);

    @DeleteMapping("/api/admin/policies/{id}")
    String deletePolicy(@PathVariable Long id);

    @GetMapping("/api/admin/policies")
    List<PolicyTypeResponse> getAllPolicies();

    @GetMapping("/api/policies/admin/count")
    Long getPolicyCount();
}