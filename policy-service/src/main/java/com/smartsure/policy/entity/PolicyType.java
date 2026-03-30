package com.smartsure.policy.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Entity
@Table(name = "policy_types")
@Data
public class PolicyType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    @NotNull
    @Positive
    @Column(nullable = false)
    private Double basePremium;

    @NotNull
    @Positive
    @Column(nullable = false)
    private Integer durationInMonths;
}



