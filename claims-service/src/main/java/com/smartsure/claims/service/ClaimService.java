package com.smartsure.claims.service;

import com.smartsure.claims.dto.ClaimCountsResponse;
import com.smartsure.claims.dto.ClaimRequest;
import com.smartsure.claims.dto.ClaimResponse;
import com.smartsure.claims.dto.ClaimReviewRequest;
import com.smartsure.claims.dto.DocumentUploadResponse;
import com.smartsure.claims.entity.Claim;
import com.smartsure.claims.entity.ClaimDocument;
import com.smartsure.claims.entity.ClaimStatus;
import com.smartsure.claims.exception.ClaimNotFoundException;
import com.smartsure.claims.exception.InvalidFileTypeException;
import com.smartsure.claims.exception.UnauthorizedAccessException;
import com.smartsure.claims.repository.ClaimDocumentRepository;
import com.smartsure.claims.repository.ClaimRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClaimService {

    private final ClaimRepository claimRepository;
    private final ClaimDocumentRepository documentRepository;

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "application/pdf",
            "image/jpeg",
            "image/png",
            "image/jpg"
    );

    // ═══════════════════════ FILE UPLOAD ═══════════════════════

    public DocumentUploadResponse uploadDocument(MultipartFile file) throws Exception {

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new InvalidFileTypeException(
                    "Invalid file type. Only PDF, JPEG, and PNG are allowed.");
        }

        // Absolute path under user's home directory — works on Windows and Linux
        String baseUploadDir = System.getProperty("user.home") + File.separator + "smartsure-uploads";

        Path uploadPath = Paths.get(baseUploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Sanitize filename — strip special characters that break Windows paths
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) originalFilename = "file";
        String sanitizedFilename = originalFilename.replaceAll("[^a-zA-Z0-9._-]", "_");

        String storedFileName = System.currentTimeMillis() + "_" + sanitizedFilename;
        Path   targetPath     = uploadPath.resolve(storedFileName);

        // Files.copy avoids the Windows cross-device move problem with transferTo()
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
        }

        ClaimDocument doc = ClaimDocument.builder()
                .claimId(0L)
                .fileName(originalFilename)
                .filePath(targetPath.toString())
                .contentType(contentType)
                .build();

        ClaimDocument saved = documentRepository.save(doc);

        return new DocumentUploadResponse(
                saved.getId(),
                saved.getFileName(),
                saved.getContentType(),
                "Document uploaded successfully. Use documentId " + saved.getId()
                        + " when initiating your claim."
        );
    }

    // ═══════════════════════ CUSTOMER ═══════════════════════

    public ClaimResponse initiateClaim(Long userId, ClaimRequest request) {

        Claim claim = Claim.builder()
                .userId(userId)
                .policyId(request.getPolicyId())
                .description(request.getDescription())
                .status(ClaimStatus.SUBMITTED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        claimRepository.save(claim);

        if (request.getDocumentId() != null) {
            documentRepository.findById(request.getDocumentId())
                    .ifPresent(doc -> {
                        doc.setClaimId(claim.getId());
                        documentRepository.save(doc);
                    });
        }

        return toResponse(claim);
    }

    public ClaimResponse getClaim(Long claimId, Long userId) {

        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new ClaimNotFoundException("Claim not found: " + claimId));

        if (!claim.getUserId().equals(userId)) {
            throw new UnauthorizedAccessException("You do not have access to this claim");
        }

        return toResponse(claim);
    }

    public List<ClaimResponse> getUserClaims(Long userId) {
        return claimRepository.findByUserId(userId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ═══════════════════════ ADMIN (internal endpoint) ═══════════════════════

    public ClaimResponse reviewClaim(Long claimId, ClaimReviewRequest request) {

        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new ClaimNotFoundException("Claim not found: " + claimId));

        if (claim.getStatus() != ClaimStatus.SUBMITTED
                && claim.getStatus() != ClaimStatus.UNDER_REVIEW) {
            throw new IllegalStateException(
                    "Claim cannot be reviewed. Current status: " + claim.getStatus());
        }

        ClaimStatus newStatus;
        try {
            newStatus = ClaimStatus.valueOf(request.getStatus().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException(
                    "Invalid status. Allowed values: APPROVED, REJECTED, UNDER_REVIEW");
        }

        if (newStatus != ClaimStatus.APPROVED
                && newStatus != ClaimStatus.REJECTED
                && newStatus != ClaimStatus.UNDER_REVIEW) {
            throw new IllegalStateException(
                    "Admin can only set status to APPROVED, REJECTED, or UNDER_REVIEW");
        }

        claim.setStatus(newStatus);
        claim.setUpdatedAt(LocalDateTime.now());
        claimRepository.save(claim);

        return toResponse(claim);
    }

    public ClaimCountsResponse getClaimCounts() {
        long total       = claimRepository.count();
        long submitted   = claimRepository.countByStatus(ClaimStatus.SUBMITTED);
        long underReview = claimRepository.countByStatus(ClaimStatus.UNDER_REVIEW);
        long approved    = claimRepository.countByStatus(ClaimStatus.APPROVED);
        long rejected    = claimRepository.countByStatus(ClaimStatus.REJECTED);
        long closed      = claimRepository.countByStatus(ClaimStatus.CLOSED);

        return new ClaimCountsResponse(total, submitted, underReview, approved, rejected, closed);
    }

    // ═══════════════════════ HELPERS ═══════════════════════

    private ClaimResponse toResponse(Claim c) {
        return new ClaimResponse(
                c.getId(),
                c.getPolicyId(),
                c.getDescription(),
                c.getStatus().name(),
                c.getCreatedAt()
        );
    }
}