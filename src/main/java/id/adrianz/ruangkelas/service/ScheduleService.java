package id.adrianz.ruangkelas.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import id.adrianz.ruangkelas.dto.CalendarDay;
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

    // ================= HELPER =================

    public String formatMonthLabel(YearMonth yearMonth) {
        String bulan = yearMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("id-ID"));
        bulan = bulan.substring(0, 1).toUpperCase() + bulan.substring(1);
        return bulan + " " + yearMonth.getYear();
    }

    public List<List<CalendarDay>> buildCalendarWeeks(YearMonth yearMonth, List<Schedule> schedules) {
        LocalDate firstDay = yearMonth.atDay(1);
        LocalDate lastDay = yearMonth.atEndOfMonth();
        int leadingEmpty = firstDay.getDayOfWeek().getValue() - 1;

        List<CalendarDay> days = new ArrayList<>();
        for (int i = 0; i < leadingEmpty; i++) {
            days.add(CalendarDay.empty());
        }

        for (LocalDate date = firstDay; !date.isAfter(lastDay); date = date.plusDays(1)) {
            final LocalDate currentDate = date;
            List<Schedule> schedulesOnDate = schedules.stream()
                    .filter(s -> s.getStartTime().toLocalDate().isEqual(currentDate))
                    .collect(Collectors.toList());
            days.add(new CalendarDay(currentDate, schedulesOnDate));
        }

        while (days.size() % 7 != 0) {
            days.add(CalendarDay.empty());
        }

        List<List<CalendarDay>> weeks = new ArrayList<>();
        for (int i = 0; i < days.size(); i += 7) {
            weeks.add(days.subList(i, i + 7));
        }

        return weeks;
    }
}