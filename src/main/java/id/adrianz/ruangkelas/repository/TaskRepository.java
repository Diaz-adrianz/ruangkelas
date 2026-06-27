package id.adrianz.ruangkelas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import id.adrianz.ruangkelas.model.Task;
import java.time.LocalDateTime;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByClasseId(Long classId);
    
    List<Task> findByClasseClassCodeOrderByDeadlineAsc(String classeClassCode);

    List<Task> findByDeadlineBetween(LocalDateTime startOfDay, LocalDateTime endOfDay);
}