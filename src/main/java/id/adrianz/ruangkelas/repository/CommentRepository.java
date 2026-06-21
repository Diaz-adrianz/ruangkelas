package id.adrianz.ruangkelas.repository;

import id.adrianz.ruangkelas.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    @Query("SELECT c FROM Comment c WHERE c.task.id = :taskId AND c.parent IS NULL")
    List<Comment> findByTaskIdAndParentIsNull(@Param("taskId") Long taskId);
}