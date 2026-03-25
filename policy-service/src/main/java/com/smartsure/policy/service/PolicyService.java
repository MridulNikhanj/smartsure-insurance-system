package com.smartsure.policy.service;

import com.smartsure.policy.dto.PolicyResponse;
import com.smartsure.policy.dto.PurchasePolicyRequest;
import com.smartsure.policy.entity.Policy;
import com.smartsure.policy.entity.PolicyStatus;
import com.smartsure.policy.entity.PolicyType;
import com.smartsure.policy.repository.PolicyRepository;
import com.smartsure.policy.repository.PolicyTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PolicyService {

    private final PolicyRepository policyRepository;
    private final PolicyTypeRepository policyTypeRepository;

    // ================= CUSTOMER =================

    // ✅ Purchase Policy
    public PolicyResponse purchasePolicy(Long userId, PurchasePolicyRequest request) {

        PolicyType type = policyTypeRepository.findById(request.getPolicyTypeId())
                .orElseThrow(() -> new RuntimeException("Policy type not found"));

        Policy policy = new Policy();
        policy.setUserId(userId);
        policy.setPolicyTypeId(type.getId());
        policy.setPremiumAmount(type.getBasePremium());
        policy.setStatus(PolicyStatus.ACTIVE);
        policy.setStartDate(LocalDate.now());
        policy.setEndDate(LocalDate.now().plusMonths(type.getDurationInMonths()));

        policyRepository.save(policy);

        return new PolicyResponse(
                policy.getId(),
                policy.getPremiumAmount(),
                policy.getStatus().name()
        );
    }

    // ✅ Get Policy by ID (SECURE)
    public Policy getPolicy(Long policyId, Long userId) {

        Policy policy = policyRepository.findById(policyId)
                .orElseThrow(() -> new RuntimeException("Policy not found"));

        // 🔒 Ensure user can only access their own policy
        if (!policy.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized access");
        }

        return policy;
    }

    // ✅ Get All Policies of a User
    public List<Policy> getPoliciesByUser(Long userId) {
        return policyRepository.findByUserId(userId);
    }

    // ================= ADMIN =================

    // ✅ Create Policy Type
    public PolicyType createPolicyType(PolicyType policyType) {
        return policyTypeRepository.save(policyType);
    }

    // ✅ Update Policy Type
    public PolicyType updatePolicyType(Long id, PolicyType updated) {

        PolicyType existing = policyTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Policy type not found"));

        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        existing.setBasePremium(updated.getBasePremium());
        existing.setDurationInMonths(updated.getDurationInMonths());

        return policyTypeRepository.save(existing);
    }

    // ✅ Delete Policy Type
    public String deletePolicyType(Long id) {
        policyTypeRepository.deleteById(id);
        return "Policy type deleted successfully";
    }

    // ✅ Get All Policy Types
    public List<PolicyType> getAllPolicyTypes() {
        return policyTypeRepository.findAll();
    }
}