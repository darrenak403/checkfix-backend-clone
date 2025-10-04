package com.datnguyen.testorderservices.repository;

import com.datnguyen.testorderservices.entity.TestResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TestResultRepository extends JpaRepository<TestResult, Long> {
    List<TestResult> findByOrderId(Long orderId);
}