package com.smartsure.policy.service;

import com.smartsure.policy.dto.PolicyResponse;
import com.smartsure.policy.dto.PolicyTypeResponse;
import com.smartsure.policy.dto.PurchasePolicyRequest;
import com.smartsure.policy.entity.Policy;
import com.smartsure.policy.entity.PolicyStatus;
import com.smartsure.policy.entity.PolicyType;
import com.smartsure.policy.exception.PolicyNotFoundException;
import com.smartsure.policy.exception.PolicyTypeNotFoundException;
import com.smartsure.policy.exception.UnauthorizedAccessException;
import com.smartsure.policy.repository.PolicyRepository;
import com.smartsure.policy.repository.PolicyTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PolicyService {

    private final PolicyRepository policyRepository;
    private final PolicyTypeRepository policyTypeRepository;

    // ═══════════════════════ CUSTOMER ═══════════════════════

    public PolicyResponse purchasePolicy(Long userId, PurchasePolicyRequest request) {

        PolicyType type = policyTypeRepository.findById(request.getPolicyTypeId())
                .orElseThrow(() -> new PolicyTypeNotFoundException(
                        "Policy type not found: " + request.getPolicyTypeId()));

        Policy policy = new Policy();
        policy.setUserId(userId);
        policy.setPolicyTypeId(type.getId());
        policy.setPremiumAmount(type.getBasePremium());
        policy.setStatus(PolicyStatus.ACTIVE);
        policy.setStartDate(LocalDate.now());
        policy.setEndDate(LocalDate.now().plusMonths(type.getDurationInMonths()));

        policyRepository.save(policy);

        return toResponse(policy, type.getName());
    }

    public PolicyResponse getPolicy(Long policyId, Long userId) {

        Policy policy = policyRepository.findById(policyId)
                .orElseThrow(() -> new PolicyNotFoundException("Policy not found: " + policyId));

        if (!policy.getUserId().equals(userId)) {
            throw new UnauthorizedAccessException("You do not have access to this policy");
        }

        PolicyType type = policyTypeRepository.findById(policy.getPolicyTypeId())
                .orElseThrow(() -> new PolicyTypeNotFoundException("Policy type not found"));

        return toResponse(policy, type.getName());
    }

    public List<PolicyResponse> getPoliciesByUser(Long userId) {
        return policyRepository.findByUserId(userId).stream()
                .map(p -> {
                    String typeName = policyTypeRepository.findById(p.getPolicyTypeId())
                            .map(PolicyType::getName)
                            .orElse("Unknown");
                    return toResponse(p, typeName);
                })
                .collect(Collectors.toList());
    }

    // ═══════════════════════ ADMIN ═══════════════════════

    public PolicyTypeResponse createPolicyType(PolicyType policyType) {
        PolicyType saved = policyTypeRepository.save(policyType);
        return toTypeResponse(saved);
    }

    public PolicyTypeResponse updatePolicyType(Long id, PolicyType updated) {

        PolicyType existing = policyTypeRepository.findById(id)
                .orElseThrow(() -> new PolicyTypeNotFoundException("Policy type not found: " + id));

        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        existing.setBasePremium(updated.getBasePremium());
        existing.setDurationInMonths(updated.getDurationInMonths());

        return toTypeResponse(policyTypeRepository.save(existing));
    }

    public String deletePolicyType(Long id) {
        if (!policyTypeRepository.existsById(id)) {
            throw new PolicyTypeNotFoundException("Policy type not found: " + id);
        }
        policyTypeRepository.deleteById(id);
        return "Policy type deleted successfully";
    }

    public List<PolicyTypeResponse> getAllPolicyTypes() {
        return policyTypeRepository.findAll()
                .stream()
                .map(this::toTypeResponse)
                .collect(Collectors.toList());
    }


    // Add to PolicyService:
    public Long getActivePolicyCount() {
        return policyRepository.countByStatus(PolicyStatus.ACTIVE);
    }


    // ═══════════════════════ HELPERS ═══════════════════════

    private PolicyResponse toResponse(Policy p, String typeName) {
        return new PolicyResponse(
                p.getId(),
                typeName,
                p.getPremiumAmount(),
                p.getStatus().name(),
                p.getStartDate(),
                p.getEndDate()
        );
    }

    private PolicyTypeResponse toTypeResponse(PolicyType t) {
        return new PolicyTypeResponse(
                t.getId(), t.getName(), t.getDescription(),
                t.getBasePremium(), t.getDurationInMonths()
        );
    }
}