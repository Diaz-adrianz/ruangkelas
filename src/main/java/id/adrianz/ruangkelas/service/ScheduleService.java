package id.adrianz.ruangkelas.service;

import id.adrianz.ruangkelas.model.Schedule;
import id.adrianz.ruangkelas.repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    // Menambah jadwal baru
    public Schedule saveSchedule(Schedule schedule) {
        return scheduleRepository.save(schedule);
    }

    // Mengambil semua jadwal
    public List<Schedule> getAllSchedules() {
        return scheduleRepository.findAll();
    }

    // Mengambil jadwal berdasarkan ID
    public Optional<Schedule> getScheduleById(Long id) {
        return scheduleRepository.findById(id);
    }

    // Mengupdate jadwal
    public Schedule updateSchedule(Long id, Schedule scheduleDetails) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Jadwal tidak ditemukan dengan id: " + id));

        schedule.setNamaMatkul(scheduleDetails.getNamaMatkul());
        schedule.setTanggalKuliah(scheduleDetails.getTanggalKuliah());
        schedule.setHari(scheduleDetails.getHari());
        schedule.setJamMulai(scheduleDetails.getJamMulai());
        schedule.setJamSelesai(scheduleDetails.getJamSelesai());
        schedule.setRuangan(scheduleDetails.getRuangan());
        schedule.setNamaDosen(scheduleDetails.getNamaDosen());

        return scheduleRepository.save(schedule);
    }

    // Menghapus jadwal
    public void deleteSchedule(Long id) {
        scheduleRepository.deleteById(id);
    }
}