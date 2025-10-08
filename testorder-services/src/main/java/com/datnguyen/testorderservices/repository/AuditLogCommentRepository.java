package com.datnguyen.testorderservices.repository;

import com.datnguyen.testorderservices.entity.AuditLogComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogCommentRepository extends JpaRepository<AuditLogComment, Long> {
}
