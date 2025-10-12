package com.datnguyen.testorderservices.repository;

import com.datnguyen.testorderservices.dto.response.TestOrderResponse;
import com.datnguyen.testorderservices.entity.TestOrder;
import com.datnguyen.testorderservices.entity.OrderStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TestOrderRepository extends JpaRepository<TestOrder, Long> {
    Page<TestOrder> findByDeletedFalse(Pageable pageable);
    Page<TestOrder> findByDeletedFalseAndStatus(OrderStatus status, Pageable pageable);
    Optional<TestOrder> findById(Long testOrderId);
    Page<TestOrder> findByPatientIdAndDeletedFalse(Long id, Pageable pageable);
}
