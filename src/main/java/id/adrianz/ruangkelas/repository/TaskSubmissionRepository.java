package id.adrianz.ruangkelas.repository;

import id.adrianz.ruangkelas.model.TaskSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository 
public interface TaskSubmissionRepository extends JpaRepository<TaskSubmission, Long>{
}
