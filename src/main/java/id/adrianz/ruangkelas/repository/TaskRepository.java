package id.adrianz.ruangkelas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import id.adrianz.ruangkelas.model.Task;
import java.time.LocalDateTime;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("SELECT COUNT(t) FROM Task t JOIN t.classe c JOIN UserClass uc ON uc.classe.id = c.id " + "LEFT JOIN TaskSubmission ts ON ts.task.id = t.id AND ts.userClass.user.id = :userId " + "WHERE uc.user.id = :userId AND t.deadline < :now AND ts IS NULL")
    long countOverdueTasksForUser(@Param("userId") Long userId, @Param("now") LocalDateTime now);

   
    @Query("SELECT t FROM Task t JOIN t.classe c JOIN UserClass uc ON uc.classe.id = c.id " +"LEFT JOIN TaskSubmission ts ON ts.task.id = t.id AND ts.userClass.user.id = :userId " + "WHERE uc.user.id = :userId AND t.deadline BETWEEN :now AND :nextWeek AND ts IS NULL " + "ORDER BY t.deadline ASC")
    List<Task> findUpcomingDeadlinesForUser(@Param("userId") Long userId, @Param("now") LocalDateTime now, @Param("nextWeek") LocalDateTime nextWeek);

    
    @Query("SELECT COUNT(ts) FROM TaskSubmission ts WHERE ts.userClass.user.id = :userId")
    long countTotalSubmittedTasksForUser(@Param("userId") Long userId);

    
    @Query("SELECT COUNT(t) FROM Task t JOIN t.classe c JOIN UserClass uc ON uc.classe.id = c.id WHERE uc.user.id = :userId")
    long countTotalTasksForUser(@Param("userId") Long userId);

    List<Task> findByClasseId(Long classId);
    
    List<Task> findByClasseClassCodeOrderByDeadlineAsc(String classeClassCode);

    List<Task> findByDeadlineBetween(LocalDateTime startOfDay, LocalDateTime endOfDay);
}