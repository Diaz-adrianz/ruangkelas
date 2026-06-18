package id.adrianz.ruangkelas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import id.adrianz.ruangkelas.model.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByClasseId(Long classId);

}