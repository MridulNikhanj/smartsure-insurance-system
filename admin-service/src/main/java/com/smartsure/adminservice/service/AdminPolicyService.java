package com.smartsure.admin.service;

import com.smartsure.admin.client.PolicyClient;
import com.smartsure.admin.dto.PolicyRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminPolicyService {

    private final PolicyClient policyClient;

    public String createPolicy(PolicyRequest request) {
        return policyClient.createPolicy(request);
    }

    public String updatePolicy(Long id, PolicyRequest request) {
        return policyClient.updatePolicy(id, request);
    }

    public String deletePolicy(Long id) {
        return policyClient.deletePolicy(id);
    }
}