package id.adrianz.ruangkelas.dto;

import java.time.LocalDate;
import java.util.List;

import id.adrianz.ruangkelas.model.Schedule;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Representasi satu sel pada grid kalender bulanan di halaman Jadwal.
 * date == null artinya sel kosong (padding di luar bulan yang ditampilkan).
 */
@Getter
@AllArgsConstructor
public class CalendarDay {
    private final LocalDate date;
    private final List<Schedule> schedules;

    public static CalendarDay empty() {
        return new CalendarDay(null, List.of());
    }
}