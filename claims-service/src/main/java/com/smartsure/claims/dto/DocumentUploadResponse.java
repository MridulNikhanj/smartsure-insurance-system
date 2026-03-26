package com.smartsure.claims.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DocumentUploadResponse {
    private Long documentId;
    private String fileName;
    private String contentType;
    private String message;
}