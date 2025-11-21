package ttldd.testorderservices.repository;

import ttldd.testorderservices.entity.Comment;
import ttldd.testorderservices.entity.CommentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByDoctorId(Long doctorId);

    List<Comment> findByTestOrderIdAndStatus(Long testOrderId, CommentStatus status);

    List<Comment> findByTestOrderIdAndStatusAndLevelOrderByCreatedAtDesc(
            Long testOrderId, CommentStatus status, Integer level);
}