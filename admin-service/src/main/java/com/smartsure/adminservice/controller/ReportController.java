package com.smartsure.adminservice.controller;

import com.smartsure.adminservice.dto.ReportResponse;
import com.smartsure.adminservice.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ReportResponse getReports() {
        return reportService.generateReport();
    }
}