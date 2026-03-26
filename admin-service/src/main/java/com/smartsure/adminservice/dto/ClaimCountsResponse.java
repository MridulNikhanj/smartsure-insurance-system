package com.smartsure.adminservice.dto;

import lombok.Data;

@Data
public class ClaimCountsResponse {
    private Long total;
    private Long submitted;
    private Long underReview;
    private Long approved;
    private Long rejected;
    private Long closed;
}