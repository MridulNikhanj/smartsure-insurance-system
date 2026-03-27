package com.smartsure.adminservice.service;

import com.smartsure.adminservice.client.PolicyClient;
import com.smartsure.adminservice.dto.PolicyRequest;
import com.smartsure.adminservice.dto.PolicyTypeResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminPolicyServiceTest {

    @Mock
    private PolicyClient policyClient;

    @InjectMocks
    private AdminPolicyService adminPolicyService;

    private PolicyRequest policyRequest;
    private PolicyTypeResponse policyTypeResponse;

    @BeforeEach
    void setUp() {
        policyRequest = new PolicyRequest();
        policyRequest.setName("Health Shield Basic");
        policyRequest.setDescription("Health coverage");
        policyRequest.setBasePremium(5000.0);
        policyRequest.setDurationInMonths(12);

        policyTypeResponse = new PolicyTypeResponse();
        policyTypeResponse.setId(1L);
        policyTypeResponse.setName("Health Shield Basic");
        policyTypeResponse.setDescription("Health coverage");
        policyTypeResponse.setBasePremium(5000.0);
        policyTypeResponse.setDurationInMonths(12);
    }

    // ─── CREATE ──────────────────────────────────────────────

    @Test
    void createPolicy_CallsFeignClient_AndReturnsResponse() {
        when(policyClient.createPolicy(policyRequest)).thenReturn(policyTypeResponse);

        PolicyTypeResponse response = adminPolicyService.createPolicy(policyRequest);

        assertNotNull(response);
        assertEquals("Health Shield Basic", response.getName());
        assertEquals(5000.0, response.getBasePremium());
        verify(policyClient, times(1)).createPolicy(policyRequest);
    }

    // ─── UPDATE ──────────────────────────────────────────────

    @Test
    void updatePolicy_CallsFeignClient_WithCorrectIdAndRequest() {
        policyTypeResponse.setName("Health Shield Premium");
        when(policyClient.updatePolicy(1L, policyRequest)).thenReturn(policyTypeResponse);

        PolicyTypeResponse response = adminPolicyService.updatePolicy(1L, policyRequest);

        assertNotNull(response);
        verify(policyClient, times(1)).updatePolicy(1L, policyRequest);
    }

    @Test
    void updatePolicy_ThrowsException_WhenFeignFails() {
        when(policyClient.updatePolicy(99L, policyRequest))
                .thenThrow(new RuntimeException("Policy type not found: 99"));

        assertThrows(RuntimeException.class,
                () -> adminPolicyService.updatePolicy(99L, policyRequest));
    }

    // ─── DELETE ──────────────────────────────────────────────

    @Test
    void deletePolicy_CallsFeignClient_AndReturnsSuccessMessage() {
        when(policyClient.deletePolicy(1L)).thenReturn("Policy type deleted successfully");

        String result = adminPolicyService.deletePolicy(1L);

        assertEquals("Policy type deleted successfully", result);
        verify(policyClient, times(1)).deletePolicy(1L);
    }

    @Test
    void deletePolicy_ThrowsException_WhenFeignFails() {
        when(policyClient.deletePolicy(99L))
                .thenThrow(new RuntimeException("Policy type not found: 99"));

        assertThrows(RuntimeException.class,
                () -> adminPolicyService.deletePolicy(99L));
    }

    // ─── GET ALL ─────────────────────────────────────────────

    @Test
    void getAllPolicies_ReturnsList_FromFeignClient() {
        when(policyClient.getAllPolicies()).thenReturn(List.of(policyTypeResponse));

        List<PolicyTypeResponse> result = adminPolicyService.getAllPolicies();

        assertEquals(1, result.size());
        assertEquals("Health Shield Basic", result.get(0).getName());
        verify(policyClient, times(1)).getAllPolicies();
    }

    @Test
    void getAllPolicies_ReturnsEmptyList_WhenNoTypes() {
        when(policyClient.getAllPolicies()).thenReturn(List.of());

        List<PolicyTypeResponse> result = adminPolicyService.getAllPolicies();

        assertTrue(result.isEmpty());
    }
}