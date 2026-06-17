package id.adrianz.ruangkelas.service;

import id.adrianz.ruangkelas.model.Schedule;
import id.adrianz.ruangkelas.repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    // Menampilkan semua data jadwal kuliah
    public List<Schedule> getAllSchedules() {
        return scheduleRepository.findAll();
    }

    // Menyimpan data jadwal kuliah baru
    public Schedule saveSchedule(Schedule schedule) {
        return scheduleRepository.save(schedule);
    }
}