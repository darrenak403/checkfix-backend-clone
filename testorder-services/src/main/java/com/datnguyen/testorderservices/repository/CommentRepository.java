package com.datnguyen.testorderservices.repository;

import com.datnguyen.testorderservices.dto.request.CommentRequest;
import com.datnguyen.testorderservices.entity.Comment;
import com.datnguyen.testorderservices.entity.TestOrder;
import com.datnguyen.testorderservices.entity.TestResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByDoctorId(Long doctorId);
}