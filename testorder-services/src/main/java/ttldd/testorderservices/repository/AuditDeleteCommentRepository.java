package ttldd.testorderservices.repository;

import ttldd.testorderservices.entity.AuditDeleteComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditDeleteCommentRepository extends JpaRepository<AuditDeleteComment, Long> {
}
