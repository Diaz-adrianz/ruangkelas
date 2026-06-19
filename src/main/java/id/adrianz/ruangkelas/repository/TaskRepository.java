package id.adrianz.ruangkelas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import id.adrianz.ruangkelas.model.Task;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByClasseId(Long classId);
    
    List<Task> findByClasseIdOrderByDeadlineAsc(Long classId);
}