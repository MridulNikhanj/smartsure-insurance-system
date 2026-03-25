package com.smartsure.policy.repository;

import com.smartsure.policy.entity.PolicyType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PolicyTypeRepository extends JpaRepository<PolicyType, Long> {
}