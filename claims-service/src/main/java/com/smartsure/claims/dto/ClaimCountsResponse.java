package com.smartsure.claims.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ClaimCountsResponse {
    private Long total;
    private Long submitted;
    private Long underReview;
    private Long approved;
    private Long rejected;
    private Long closed;
}