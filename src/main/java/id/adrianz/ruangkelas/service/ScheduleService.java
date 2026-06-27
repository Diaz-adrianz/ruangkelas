package id.adrianz.ruangkelas.service;

import id.adrianz.ruangkelas.model.Schedule;
import id.adrianz.ruangkelas.repository.ClassRepository;
import id.adrianz.ruangkelas.repository.ScheduleRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ClassRepository classRepository;

    public Schedule createSchedule(Long classId, LocalDateTime dateTime, LocalDateTime endDateTime, String place) {
        id.adrianz.ruangkelas.model.Class kelas = classRepository.findByIdWithCourse(classId)
                .orElseThrow(() -> new RuntimeException("Kelas tidak ditemukan"));

        Schedule schedule = new Schedule();
        schedule.setKelas(kelas);
        schedule.setDateTime(dateTime);
        schedule.setEndDateTime(endDateTime);
        schedule.setPlace(place);
        return scheduleRepository.save(schedule);
    }

    public List<Schedule> getAllSchedules() {
        return scheduleRepository.findAll();
    }

    public Optional<Schedule> getScheduleById(Long id) {
        return scheduleRepository.findById(id);
    }

    public Schedule updateSchedule(Long id, LocalDateTime dateTime, LocalDateTime endDateTime, String place) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Jadwal tidak ditemukan dengan id: " + id));

        schedule.setDateTime(dateTime);
        schedule.setEndDateTime(endDateTime);
        schedule.setPlace(place);

        return scheduleRepository.save(schedule);
    }

    public void deleteSchedule(Long id) {
        scheduleRepository.deleteById(id);
    }

    public List<Schedule> getSchedulesByClassCode(Long classId) {
        return scheduleRepository.findByKelas_IdOrderByDateTimeAsc(classId);
    }
}