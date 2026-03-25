package com.smartsure.claims.repository;

import com.smartsure.claims.entity.ClaimDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClaimDocumentRepository extends JpaRepository<ClaimDocument, Long> {
}