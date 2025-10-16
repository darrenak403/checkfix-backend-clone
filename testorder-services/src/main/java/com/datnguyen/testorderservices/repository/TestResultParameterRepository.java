package com.datnguyen.testorderservices.repository;


import com.datnguyen.testorderservices.entity.TestResultParameter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestResultParameterRepository extends JpaRepository<TestResultParameter, Long> {
}