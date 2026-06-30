package id.adrianz.ruangkelas.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import id.adrianz.ruangkelas.model.Schedule;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    // Semua jadwal di suatu kelas, diurutkan dari yang paling dekat
    List<Schedule> findByClasseIdOrderByStartTimeAsc(Long classId);

    // Jadwal di suatu kelas dalam rentang waktu tertentu, untuk mengisi kalender bulanan
    @Query("SELECT s FROM Schedule s WHERE s.classe.id = :classId "
            + "AND s.startTime >= :from AND s.startTime < :to "
            + "ORDER BY s.startTime ASC")
    List<Schedule> findByClasseIdAndStartTimeBetween(
            @Param("classId") Long classId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);

    // Dipakai untuk validasi kepemilikan saat akses by classCode
    @Query("SELECT s FROM Schedule s WHERE s.id = :id AND s.classe.classCode = :classCode")
    Optional<Schedule> findByIdAndClasseClassCode(
            @Param("id") Long id,
            @Param("classCode") String classCode);
}