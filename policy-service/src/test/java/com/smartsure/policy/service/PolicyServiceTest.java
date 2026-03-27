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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PolicyServiceTest {

    @Mock
    private PolicyRepository policyRepository;

    @Mock
    private PolicyTypeRepository policyTypeRepository;

    @InjectMocks
    private PolicyService policyService;

    private PolicyType policyType;
    private Policy policy;
    private PurchasePolicyRequest purchaseRequest;

    @BeforeEach
    void setUp() {
        policyType = new PolicyType();
        policyType.setId(1L);
        policyType.setName("Health Shield");
        policyType.setDescription("Health coverage");
        policyType.setBasePremium(5000.0);
        policyType.setDurationInMonths(12);

        policy = new Policy();
        policy.setId(1L);
        policy.setUserId(10L);
        policy.setPolicyTypeId(1L);
        policy.setPremiumAmount(5000.0);
        policy.setStatus(PolicyStatus.ACTIVE);
        policy.setStartDate(LocalDate.now());
        policy.setEndDate(LocalDate.now().plusMonths(12));

        purchaseRequest = new PurchasePolicyRequest();
        purchaseRequest.setPolicyTypeId(1L);
    }

    // ─── PURCHASE POLICY ────────────────────────────────────

    @Test
    void purchasePolicy_Success() {
        when(policyTypeRepository.findById(1L)).thenReturn(Optional.of(policyType));
        when(policyRepository.save(any(Policy.class))).thenReturn(policy);

        PolicyResponse response = policyService.purchasePolicy(10L, purchaseRequest);

        assertNotNull(response);
        assertEquals(PolicyStatus.ACTIVE.name(), response.getStatus());
        assertEquals(5000.0, response.getPremium());
        verify(policyRepository, times(1)).save(any(Policy.class));
    }

    @Test
    void purchasePolicy_ThrowsException_WhenPolicyTypeNotFound() {
        when(policyTypeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(PolicyTypeNotFoundException.class,
                () -> policyService.purchasePolicy(10L, purchaseRequest));

        verify(policyRepository, never()).save(any());
    }

    // ─── GET POLICY ──────────────────────────────────────────

    @Test
    void getPolicy_Success() {
        when(policyRepository.findById(1L)).thenReturn(Optional.of(policy));
        when(policyTypeRepository.findById(1L)).thenReturn(Optional.of(policyType));

        PolicyResponse response = policyService.getPolicy(1L, 10L);

        assertNotNull(response);
        assertEquals("Health Shield", response.getPolicyTypeName());
        assertEquals(PolicyStatus.ACTIVE.name(), response.getStatus());
    }

    @Test
    void getPolicy_ThrowsException_WhenPolicyNotFound() {
        when(policyRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(PolicyNotFoundException.class,
                () -> policyService.getPolicy(99L, 10L));
    }

    @Test
    void getPolicy_ThrowsException_WhenUserDoesNotOwnPolicy() {
        when(policyRepository.findById(1L)).thenReturn(Optional.of(policy));

        // policy belongs to userId 10, but we request as userId 99
        assertThrows(UnauthorizedAccessException.class,
                () -> policyService.getPolicy(1L, 99L));
    }

    // ─── GET MY POLICIES ─────────────────────────────────────

    @Test
    void getPoliciesByUser_ReturnsListForUser() {
        when(policyRepository.findByUserId(10L)).thenReturn(List.of(policy));
        when(policyTypeRepository.findById(1L)).thenReturn(Optional.of(policyType));

        List<PolicyResponse> result = policyService.getPoliciesByUser(10L);

        assertEquals(1, result.size());
        assertEquals("Health Shield", result.get(0).getPolicyTypeName());
    }

    @Test
    void getPoliciesByUser_ReturnsEmptyList_WhenNoPolicies() {
        when(policyRepository.findByUserId(99L)).thenReturn(List.of());

        List<PolicyResponse> result = policyService.getPoliciesByUser(99L);

        assertTrue(result.isEmpty());
    }

    // ─── ADMIN — CREATE POLICY TYPE ──────────────────────────

    @Test
    void createPolicyType_Success() {
        when(policyTypeRepository.save(any(PolicyType.class))).thenReturn(policyType);

        PolicyTypeResponse response = policyService.createPolicyType(policyType);

        assertNotNull(response);
        assertEquals("Health Shield", response.getName());
        assertEquals(5000.0, response.getBasePremium());
    }

    // ─── ADMIN — UPDATE POLICY TYPE ──────────────────────────

    @Test
    void updatePolicyType_Success() {
        PolicyType updated = new PolicyType();
        updated.setName("Health Shield Premium");
        updated.setDescription("Extended coverage");
        updated.setBasePremium(7500.0);
        updated.setDurationInMonths(12);

        when(policyTypeRepository.findById(1L)).thenReturn(Optional.of(policyType));
        when(policyTypeRepository.save(any(PolicyType.class))).thenReturn(policyType);

        PolicyTypeResponse response = policyService.updatePolicyType(1L, updated);

        assertNotNull(response);
        verify(policyTypeRepository, times(1)).save(any(PolicyType.class));
    }

    @Test
    void updatePolicyType_ThrowsException_WhenNotFound() {
        when(policyTypeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(PolicyTypeNotFoundException.class,
                () -> policyService.updatePolicyType(99L, policyType));
    }

    // ─── ADMIN — DELETE POLICY TYPE ──────────────────────────

    @Test
    void deletePolicyType_Success() {
        when(policyTypeRepository.existsById(1L)).thenReturn(true);

        String result = policyService.deletePolicyType(1L);

        assertEquals("Policy type deleted successfully", result);
        verify(policyTypeRepository, times(1)).deleteById(1L);
    }

    @Test
    void deletePolicyType_ThrowsException_WhenNotFound() {
        when(policyTypeRepository.existsById(99L)).thenReturn(false);

        assertThrows(PolicyTypeNotFoundException.class,
                () -> policyService.deletePolicyType(99L));

        verify(policyTypeRepository, never()).deleteById(any());
    }
}