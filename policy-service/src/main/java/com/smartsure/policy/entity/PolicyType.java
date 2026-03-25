package com.smartsure.policy.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "policy_types")
@Data
public class PolicyType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private Double basePremium;

    private Integer durationInMonths;
}