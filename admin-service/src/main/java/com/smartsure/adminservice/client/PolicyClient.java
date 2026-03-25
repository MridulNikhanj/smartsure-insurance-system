package com.smartsure.admin.client;

import com.smartsure.admin.dto.PolicyRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "POLICY-SERVICE")
public interface PolicyClient {

    @PostMapping("/api/admin/policies")
    String createPolicy(@RequestBody PolicyRequest request);

    @PutMapping("/api/admin/policies/{id}")
    String updatePolicy(@PathVariable Long id,
                        @RequestBody PolicyRequest request);

    @DeleteMapping("/api/admin/policies/{id}")
    String deletePolicy(@PathVariable Long id);
}