package com.smartsure.claims.service;

import com.smartsure.claims.dto.ClaimRequest;
import com.smartsure.claims.dto.ClaimResponse;
import com.smartsure.claims.entity.Claim;
import com.smartsure.claims.entity.ClaimDocument;
import com.smartsure.claims.entity.ClaimStatus;
import com.smartsure.claims.repository.ClaimDocumentRepository;
import com.smartsure.claims.repository.ClaimRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClaimService {

    private final ClaimRepository claimRepository;
    private final ClaimDocumentRepository documentRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    // ================= FILE UPLOAD =================
    public String uploadDocument(MultipartFile file) throws Exception {

        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();

        String filePath = uploadDir + "/" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
        file.transferTo(new File(filePath));

        return filePath;
    }

    // ================= CUSTOMER =================
    public ClaimResponse initiateClaim(Long userId, ClaimRequest request) {

        Claim claim = Claim.builder()
                .userId(userId)
                .policyId(request.getPolicyId())
                .description(request.getDescription())
                .status(ClaimStatus.SUBMITTED)
                .createdAt(LocalDateTime.now())
                .build();

        claimRepository.save(claim);

        // OPTIONAL: Save document mapping (future enhancement)

        return new ClaimResponse(claim.getId(), claim.getStatus().name());
    }

    public Claim getClaim(Long claimId, Long userId) {

        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new RuntimeException("Claim not found"));

        if (!claim.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized access");
        }

        return claim;
    }

    public List<Claim> getUserClaims(Long userId) {
        return claimRepository.findByUserId(userId);
    }
}