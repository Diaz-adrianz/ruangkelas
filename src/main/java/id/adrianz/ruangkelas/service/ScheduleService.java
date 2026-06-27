package id.adrianz.ruangkelas.service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

import org.springframework.stereotype.Service;

import id.adrianz.ruangkelas.model.Class;
import id.adrianz.ruangkelas.model.Schedule;
import id.adrianz.ruangkelas.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ClassService classService;

    // ================= READ =================

    public List<Schedule> getSchedulesByClassCode(String classCode) {
        Class kelas = classService.getByCode(classCode);
        return scheduleRepository.findByClasseIdOrderByStartTimeAsc(kelas.getId());
    }

    // Dipakai untuk mengisi kalender bulanan di halaman index
    public List<Schedule> getSchedulesByClassCodeAndMonth(String classCode, YearMonth yearMonth) {
        Class kelas = classService.getByCode(classCode);
        LocalDateTime from = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime to = yearMonth.plusMonths(1).atDay(1).atStartOfDay();
        return scheduleRepository.findByClasseIdAndStartTimeBetween(kelas.getId(), from, to);
    }

    public Schedule getByIdAndClassCode(Long id, String classCode) {
        return scheduleRepository.findByIdAndClasseClassCode(id, classCode)
                .orElseThrow(() -> new RuntimeException("Jadwal tidak ditemukan"));
    }

    // ================= CREATE =================

    public Schedule create(String classCode, LocalDateTime startTime, LocalDateTime endTime, String place,
            Long userId) {

        Class kelas = classService.getByCode(classCode);
        classService.ensureAdmin(kelas.getId(), userId);
        validateTimeRange(startTime, endTime);

        Schedule schedule = Schedule.builder()
                .classe(kelas)
                .startTime(startTime)
                .endTime(endTime)
                .place(place)
                .build();

        return scheduleRepository.save(schedule);
    }

    // ================= UPDATE =================

    public Schedule update(Long id, String classCode, LocalDateTime startTime, LocalDateTime endTime, String place,
            Long userId) {

        Schedule existing = getByIdAndClassCode(id, classCode);
        classService.ensureAdmin(existing.getClasse().getId(), userId);
        validateTimeRange(startTime, endTime);

        existing.setStartTime(startTime);
        existing.setEndTime(endTime);
        existing.setPlace(place);

        return scheduleRepository.save(existing);
    }

    // ================= DELETE =================

    public void delete(Long id, String classCode, Long userId) {
        Schedule existing = getByIdAndClassCode(id, classCode);
        classService.ensureAdmin(existing.getClasse().getId(), userId);
        scheduleRepository.deleteById(existing.getId());
    }

    // ================= VALIDATION =================

    private void validateTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        if (!startTime.toLocalDate().isEqual(endTime.toLocalDate())) {
            throw new RuntimeException("Jam mulai dan jam selesai harus di hari yang sama");
        }
        if (!endTime.isAfter(startTime)) {
            throw new RuntimeException("Jam selesai harus setelah jam mulai");
        }
    }
}