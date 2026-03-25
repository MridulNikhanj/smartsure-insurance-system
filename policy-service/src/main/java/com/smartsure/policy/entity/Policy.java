package com.smartsure.policy.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "policies")
@Data
public class Policy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long policyTypeId;

    private Double premiumAmount;

    @Enumerated(EnumType.STRING)
    private PolicyStatus status;

    private LocalDate startDate;

    private LocalDate endDate;
}