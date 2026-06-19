package id.adrianz.ruangkelas.repository;

import id.adrianz.ruangkelas.model.Schedule;
import id.adrianz.ruangkelas.model.Class; // Pastikan import class model-mu
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    // 1. Digunakan untuk anggota melihat jadwal berdasarkan kelasnya
    List<Schedule> findByKelas(Class kelas);

    // 2. Digunakan untuk admin mencari jadwal di ruangan tertentu (mencegah bentrok)
    List<Schedule> findByRuangan(String ruangan);

    // 3. Digunakan untuk admin mencari jadwal berdasarkan dosen
    List<Schedule> findByNamaDosen(String namaDosen);

    // 4. (Opsional) Mengurutkan jadwal berdasarkan waktu secara otomatis
    // Jika kamu menambahkan field 'jamMulai' di model, ini sangat berguna
    List<Schedule> findAllByOrderByJamMulaiAsc();

   List<Schedule> findByKelas_Id(Long classId);

}