package id.adrianz.ruangkelas.controller;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import id.adrianz.ruangkelas.dto.CalendarDay;
import id.adrianz.ruangkelas.dto.CreateScheduleDto;
import id.adrianz.ruangkelas.dto.UpdateScheduleDto;
import id.adrianz.ruangkelas.model.Class;
import id.adrianz.ruangkelas.model.Schedule;
import id.adrianz.ruangkelas.model.UserPrincipal;
import id.adrianz.ruangkelas.service.ClassService;
import id.adrianz.ruangkelas.service.ScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/class/{classCode}/schedule")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final ClassService classService;

    // ================= LIST (kalender bulanan) =================
    // "schedule list on class/schedule page"

    @GetMapping
    public String index(@PathVariable String classCode,
                         @RequestParam(required = false) Integer year,
                         @RequestParam(required = false) Integer month,
                         @AuthenticationPrincipal UserPrincipal principal,
                         Model model) {

        Class kelas = classService.getByCode(classCode);
        boolean isAdmin = classService.isAdmin(kelas.getId(), principal.getUser().getId());

        YearMonth current = (year != null && month != null)
                ? YearMonth.of(year, month)
                : YearMonth.now();

        List<Schedule> schedules = scheduleService.getSchedulesByClassCodeAndMonth(classCode, current);

        model.addAttribute("classs", kelas);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("currentYearMonth", current);
        model.addAttribute("currentMonthLabel", formatMonthLabel(current));
        model.addAttribute("prevYearMonth", current.minusMonths(1));
        model.addAttribute("nextYearMonth", current.plusMonths(1));
        model.addAttribute("calendarWeeks", buildCalendarWeeks(current, schedules));

        return "pages/Schedule/Index";
    }

    // Label "Juni 2026" dalam Bahasa Indonesia, tidak bergantung pada Locale server
    private String formatMonthLabel(YearMonth yearMonth) {
        String bulan = yearMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("id-ID"));
        // capitalize manual sebagai jaga-jaga kalau Locale "id" tidak tersedia di JVM
        bulan = bulan.substring(0, 1).toUpperCase() + bulan.substring(1);
        return bulan + " " + yearMonth.getYear();
    }

    // Membangun grid 7 kolom (Senin-Minggu) x N baris untuk satu bulan,
    // dengan sel kosong (CalendarDay.empty()) untuk padding di luar bulan
    private List<List<CalendarDay>> buildCalendarWeeks(YearMonth yearMonth, List<Schedule> schedules) {
        LocalDate firstDay = yearMonth.atDay(1);
        LocalDate lastDay = yearMonth.atEndOfMonth();

        // offset dari Senin (DayOfWeek.MONDAY = 1 ... SUNDAY = 7)
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

        // genapkan baris terakhir jadi kelipatan 7
        while (days.size() % 7 != 0) {
            days.add(CalendarDay.empty());
        }

        List<List<CalendarDay>> weeks = new ArrayList<>();
        for (int i = 0; i < days.size(); i += 7) {
            weeks.add(days.subList(i, i + 7));
        }

        return weeks;
    }

    // ================= DETAIL =================
    // "schedule detail on schedule page"

    @GetMapping("/{scheduleId}")
    public String detail(@PathVariable String classCode,
                          @PathVariable Long scheduleId,
                          @AuthenticationPrincipal UserPrincipal principal,
                          Model model) {

        Class kelas = classService.getByCode(classCode);
        Schedule schedule = scheduleService.getByIdAndClassCode(scheduleId, classCode);
        boolean isAdmin = classService.isAdmin(kelas.getId(), principal.getUser().getId());

        model.addAttribute("classs", kelas);
        model.addAttribute("schedule", schedule);
        model.addAttribute("isAdmin", isAdmin);

        return "pages/Schedule/Detail";
    }

    // ================= CREATE =================

    @GetMapping("/create")
    public String createForm(@PathVariable String classCode, Model model) {
        Class kelas = classService.getByCode(classCode);
        model.addAttribute("classs", kelas);
        model.addAttribute("createScheduleDto", new CreateScheduleDto());
        return "pages/Schedule/Create";
    }

    @PostMapping("/create")
    public String create(@PathVariable String classCode,
                          @Valid @ModelAttribute("createScheduleDto") CreateScheduleDto request,
                          BindingResult result,
                          Model model,
                          @AuthenticationPrincipal UserPrincipal principal,
                          RedirectAttributes redirectAttributes) {

        Class kelas = classService.getByCode(classCode);

        if (result.hasErrors()) {
            model.addAttribute("classs", kelas);
            model.addAttribute("errors", result.getFieldErrors());
            return "pages/Schedule/Create";
        }

        try {
            scheduleService.create(
                    classCode,
                    request.getStartTime(),
                    request.getEndTime(),
                    request.getPlace(),
                    principal.getUser().getId());
        } catch (RuntimeException e) {
            model.addAttribute("classs", kelas);
            model.addAttribute("error", e.getMessage());
            return "pages/Schedule/Create";
        }

        redirectAttributes.addFlashAttribute("success", "Jadwal berhasil ditambahkan");
        return "redirect:/class/" + classCode + "#schedules";
    }

    // ================= EDIT =================

    @GetMapping("/edit/{scheduleId}")
    public String editForm(@PathVariable String classCode,
                            @PathVariable Long scheduleId,
                            Model model) {

        Class kelas = classService.getByCode(classCode);
        Schedule schedule = scheduleService.getByIdAndClassCode(scheduleId, classCode);

        UpdateScheduleDto dto = new UpdateScheduleDto();
        dto.setStartTime(schedule.getStartTime());
        dto.setEndTime(schedule.getEndTime());
        dto.setPlace(schedule.getPlace());

        model.addAttribute("classs", kelas);
        model.addAttribute("scheduleId", scheduleId);
        model.addAttribute("updateScheduleDto", dto);

        return "pages/Schedule/Edit";
    }

    @PostMapping("/edit/{scheduleId}")
    public String edit(@PathVariable String classCode,
                        @PathVariable Long scheduleId,
                        @Valid @ModelAttribute("updateScheduleDto") UpdateScheduleDto request,
                        BindingResult result,
                        Model model,
                        @AuthenticationPrincipal UserPrincipal principal,
                        RedirectAttributes redirectAttributes) {

        Class kelas = classService.getByCode(classCode);

        if (result.hasErrors()) {
            model.addAttribute("classs", kelas);
            model.addAttribute("scheduleId", scheduleId);
            model.addAttribute("errors", result.getFieldErrors());
            return "pages/Schedule/Edit";
        }

        try {
            scheduleService.update(
                    scheduleId,
                    classCode,
                    request.getStartTime(),
                    request.getEndTime(),
                    request.getPlace(),
                    principal.getUser().getId());
        } catch (RuntimeException e) {
            model.addAttribute("classs", kelas);
            model.addAttribute("scheduleId", scheduleId);
            model.addAttribute("error", e.getMessage());
            return "pages/Schedule/Edit";
        }

        redirectAttributes.addFlashAttribute("success", "Jadwal berhasil diedit");
        return "redirect:/class/" + classCode + "/schedule/" + scheduleId;
    }

    // ================= DELETE =================

    @PostMapping("/delete/{scheduleId}")
    public String delete(@PathVariable String classCode,
                          @PathVariable Long scheduleId,
                          @AuthenticationPrincipal UserPrincipal principal,
                          RedirectAttributes redirectAttributes) {

        scheduleService.delete(scheduleId, classCode, principal.getUser().getId());
        redirectAttributes.addFlashAttribute("success", "Jadwal berhasil dihapus");
        return "redirect:/class/" + classCode + "#schedules";
    }
}