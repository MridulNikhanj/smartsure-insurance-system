package com.smartsure.claims.service;

import com.smartsure.claims.dto.ClaimRequest;
import com.smartsure.claims.dto.ClaimResponse;
import com.smartsure.claims.dto.ClaimReviewRequest;
import com.smartsure.claims.entity.Claim;
import com.smartsure.claims.entity.ClaimStatus;
import com.smartsure.claims.exception.ClaimNotFoundException;
import com.smartsure.claims.exception.InvalidFileTypeException;
import com.smartsure.claims.exception.UnauthorizedAccessException;
import com.smartsure.claims.repository.ClaimDocumentRepository;
import com.smartsure.claims.repository.ClaimRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClaimServiceTest {

    @Mock
    private ClaimRepository claimRepository;

    @Mock
    private ClaimDocumentRepository documentRepository;

    @InjectMocks
    private ClaimService claimService;

    private Claim submittedClaim;
    private ClaimRequest claimRequest;

    @BeforeEach
    void setUp() {
        submittedClaim = Claim.builder()
                .id(1L)
                .userId(10L)
                .policyId(5L)
                .description("Dengue hospitalization")
                .status(ClaimStatus.SUBMITTED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        claimRequest = new ClaimRequest();
        claimRequest.setPolicyId(5L);
        claimRequest.setDescription("Dengue hospitalization");
    }

    // ─── FILE UPLOAD ─────────────────────────────────────────

    @Test
    void uploadDocument_ThrowsException_WhenFileTypeInvalid() {
        MockMultipartFile invalidFile = new MockMultipartFile(
                "file", "test.txt", "text/plain", "some text".getBytes());

        assertThrows(InvalidFileTypeException.class,
                () -> claimService.uploadDocument(invalidFile));

        verify(documentRepository, never()).save(any());
    }

    @Test
    void uploadDocument_ThrowsException_WhenContentTypeNull() {
        MockMultipartFile nullTypeFile = new MockMultipartFile(
                "file", "test.bin", null, "bytes".getBytes());

        assertThrows(InvalidFileTypeException.class,
                () -> claimService.uploadDocument(nullTypeFile));
    }

    // ─── INITIATE CLAIM ──────────────────────────────────────

    @Test
    void initiateClaim_Success() {
        when(claimRepository.save(any(Claim.class))).thenReturn(submittedClaim);

        ClaimResponse response = claimService.initiateClaim(10L, claimRequest);

        assertNotNull(response);
        assertEquals(ClaimStatus.SUBMITTED.name(), response.getStatus());
        assertEquals(5L, response.getPolicyId());
        verify(claimRepository, times(1)).save(any(Claim.class));
    }

    @Test
    void initiateClaim_SetsStatusToSubmitted() {
        when(claimRepository.save(any(Claim.class))).thenReturn(submittedClaim);

        ClaimResponse response = claimService.initiateClaim(10L, claimRequest);

        assertEquals("SUBMITTED", response.getStatus());
    }

    // ─── GET CLAIM ───────────────────────────────────────────

    @Test
    void getClaim_Success() {
        when(claimRepository.findById(1L)).thenReturn(Optional.of(submittedClaim));

        ClaimResponse response = claimService.getClaim(1L, 10L);

        assertNotNull(response);
        assertEquals(1L, response.getClaimId());
        assertEquals("SUBMITTED", response.getStatus());
    }

    @Test
    void getClaim_ThrowsException_WhenClaimNotFound() {
        when(claimRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ClaimNotFoundException.class,
                () -> claimService.getClaim(99L, 10L));
    }

    @Test
    void getClaim_ThrowsException_WhenUserDoesNotOwnClaim() {
        when(claimRepository.findById(1L)).thenReturn(Optional.of(submittedClaim));

        // claim belongs to userId 10, requesting as userId 99
        assertThrows(UnauthorizedAccessException.class,
                () -> claimService.getClaim(1L, 99L));
    }

    // ─── GET USER CLAIMS ─────────────────────────────────────

    @Test
    void getUserClaims_ReturnsList() {
        when(claimRepository.findByUserId(10L)).thenReturn(List.of(submittedClaim));

        List<ClaimResponse> result = claimService.getUserClaims(10L);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getClaimId());
    }

    @Test
    void getUserClaims_ReturnsEmpty_WhenNoClaims() {
        when(claimRepository.findByUserId(99L)).thenReturn(List.of());

        List<ClaimResponse> result = claimService.getUserClaims(99L);

        assertTrue(result.isEmpty());
    }

    // ─── REVIEW CLAIM ────────────────────────────────────────

    @Test
    void reviewClaim_Approve_Success() {
        when(claimRepository.findById(1L)).thenReturn(Optional.of(submittedClaim));
        when(claimRepository.save(any(Claim.class))).thenReturn(submittedClaim);

        ClaimReviewRequest request = new ClaimReviewRequest();
        request.setStatus("APPROVED");

        ClaimResponse response = claimService.reviewClaim(1L, request);

        assertNotNull(response);
        verify(claimRepository, times(1)).save(any(Claim.class));
    }

    @Test
    void reviewClaim_Reject_Success() {
        when(claimRepository.findById(1L)).thenReturn(Optional.of(submittedClaim));
        when(claimRepository.save(any(Claim.class))).thenReturn(submittedClaim);

        ClaimReviewRequest request = new ClaimReviewRequest();
        request.setStatus("REJECTED");

        ClaimResponse response = claimService.reviewClaim(1L, request);

        assertNotNull(response);
        verify(claimRepository, times(1)).save(any(Claim.class));
    }

    @Test
    void reviewClaim_ThrowsException_WhenClaimNotFound() {
        when(claimRepository.findById(99L)).thenReturn(Optional.empty());

        ClaimReviewRequest request = new ClaimReviewRequest();
        request.setStatus("APPROVED");

        assertThrows(ClaimNotFoundException.class,
                () -> claimService.reviewClaim(99L, request));
    }

    @Test
    void reviewClaim_ThrowsException_WhenClaimAlreadyApproved() {
        submittedClaim.setStatus(ClaimStatus.APPROVED);
        when(claimRepository.findById(1L)).thenReturn(Optional.of(submittedClaim));

        ClaimReviewRequest request = new ClaimReviewRequest();
        request.setStatus("APPROVED");

        // Cannot review a claim that is already APPROVED
        assertThrows(IllegalStateException.class,
                () -> claimService.reviewClaim(1L, request));
    }

    @Test
    void reviewClaim_ThrowsException_WhenInvalidStatusProvided() {
        when(claimRepository.findById(1L)).thenReturn(Optional.of(submittedClaim));

        ClaimReviewRequest request = new ClaimReviewRequest();
        request.setStatus("CANCELLED"); // not a valid admin transition

        assertThrows(IllegalStateException.class,
                () -> claimService.reviewClaim(1L, request));
    }
}