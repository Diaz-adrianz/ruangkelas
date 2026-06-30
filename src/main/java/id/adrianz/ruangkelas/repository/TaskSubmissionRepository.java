package id.adrianz.ruangkelas.repository;

import id.adrianz.ruangkelas.model.TaskSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskSubmissionRepository extends JpaRepository<TaskSubmission, Long> {

    Optional<TaskSubmission> findByUserClassIdAndTaskId(
            Long userClassId,
            Long taskId
    );

    List<TaskSubmission> findByTaskId(Long taskId);

    List<TaskSubmission> findByUserClassId(Long userClassId);

}