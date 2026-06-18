package id.adrianz.ruangkelas.repository;

import id.adrianz.ruangkelas.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    // Kosongkan saja bagian dalam kurung kurawal ini, Spring Boot sudah otomatis mengurusnya!
}