package id.adrianz.ruangkelas.repository;

import id.adrianz.ruangkelas.model.Task;
import id.adrianz.ruangkelas.model.TaskSubmission;
import id.adrianz.ruangkelas.model.UserClass;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskSubmissionRepository extends JpaRepository<TaskSubmission, Long> {
    List<TaskSubmission> findByTask(Task task);

    Optional<TaskSubmission> findByTaskAndUserClass(Task task, UserClass userClass);

    List<TaskSubmission> findByTaskId(Long taskId);

    List<TaskSubmission>findByTaskIdOrderBySubmittedAtDesc(Long taskId);

    Optional<TaskSubmission> findByUserClassIdAndTaskId(Long userClassId, Long taskId);

}