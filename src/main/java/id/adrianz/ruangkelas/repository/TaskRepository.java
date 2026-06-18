package id.adrianz.ruangkelas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import id.adrianz.ruangkelas.model.Task;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    // Tambahkan ini untuk mengambil tugas berdasarkan ID kelasnya
    List<Task> findByClasseId(Long classId);
}