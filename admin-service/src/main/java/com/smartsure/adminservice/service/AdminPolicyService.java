package com.smartsure.adminservice.service;

import com.smartsure.adminservice.client.PolicyClient;
import com.smartsure.adminservice.dto.PolicyRequest;
import com.smartsure.adminservice.dto.PolicyTypeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminPolicyService {

    private final PolicyClient policyClient;

    public PolicyTypeResponse createPolicy(PolicyRequest request) {
        return policyClient.createPolicy(request);
    }

    public PolicyTypeResponse updatePolicy(Long id, PolicyRequest request) {
        return policyClient.updatePolicy(id, request);
    }

    public String deletePolicy(Long id) {
        return policyClient.deletePolicy(id);
    }

    public List<PolicyTypeResponse> getAllPolicies() {
        return policyClient.getAllPolicies();
    }
}