package id.adrianz.ruangkelas.repository;

import id.adrianz.ruangkelas.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    @Query("SELECT c FROM Comment c WHERE c.task.id = :taskId AND c.parent IS NULL")
    List<Comment> findAllMainComments(@Param("taskId") Long taskId);

    @Query("""
        SELECT DISTINCT c FROM Comment c 
        LEFT JOIN c.replies r 
        WHERE c.task.id = :taskId 
        AND c.parent IS NULL 
        AND (c.userClass.user.id = :userId 
             OR r.userClass.role = id.adrianz.ruangkelas.model.UserClass.Role.ADMIN)
    """)
    List<Comment> findCommentsForStudent(@Param("taskId") Long taskId, @Param("userId") Long userId);
}