package id.adrianz.ruangkelas.controller;

import id.adrianz.ruangkelas.model.Schedule;
import id.adrianz.ruangkelas.repository.ClassRepository;
import id.adrianz.ruangkelas.model.Class;
import id.adrianz.ruangkelas.model.UserPrincipal;
import id.adrianz.ruangkelas.service.ScheduleService;
import id.adrianz.ruangkelas.service.ClassService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/admin/jadwal")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final ClassRepository classRepository;
    private final ClassService classService;

    // READ: Tampil semua jadwal (bisa untuk member & admin)
    @GetMapping
    public String showJadwal(Model model) {
        model.addAttribute("schedules", scheduleService.getAllSchedules());
        return "schedules/index";
    }

    // READ: Tampil detail satu jadwal (bisa untuk member & admin)
    @GetMapping("/{id}")
    public String showDetailJadwal(@PathVariable Long id,
                                   @AuthenticationPrincipal UserPrincipal principal,
                                   Model model) {
        Schedule schedule = scheduleService.getScheduleById(id)
                .orElseThrow(() -> new RuntimeException("Jadwal tidak ditemukan"));
        boolean isAdmin = classService.isAdmin(schedule.getKelas().getId(), principal.getUser().getId());
        model.addAttribute("schedule", schedule);
        model.addAttribute("isAdmin", isAdmin);
        return "schedules/detail_jadwal";
    }

    // CREATE: Tampilkan form tambah jadwal — hanya admin kelas
    @GetMapping("/create/{classCode}")
    public String createJadwalForm(@PathVariable String classCode,
                                   @AuthenticationPrincipal UserPrincipal principal,
                                   Model model) {
        Class kelas = classService.getByCode(classCode);
        classService.ensureAdmin(kelas.getId(), principal.getUser().getId());
        model.addAttribute("classs", kelas);
        return "schedules/tambah_jadwal";
    }

    // CREATE: Proses simpan jadwal baru — hanya admin kelas
    @PostMapping("/save")
    public String saveJadwal(
            @RequestParam Long classId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime dateTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime endDateTime,
            @RequestParam String place,
            @AuthenticationPrincipal UserPrincipal principal) {

        Class kelas = classRepository.findByIdWithCourse(classId)
                .orElseThrow(() -> new RuntimeException("Kelas tidak ditemukan"));

        classService.ensureAdmin(kelas.getId(), principal.getUser().getId());

        scheduleService.createSchedule(classId, dateTime, endDateTime, place);

        return "redirect:/class/" + kelas.getClassCode() + "/jadwal";
    }

    // UPDATE: Tampilkan form edit — hanya admin kelas
    @GetMapping("/edit/{id}")
    public String editJadwal(@PathVariable Long id,
                             @AuthenticationPrincipal UserPrincipal principal,
                             Model model) {
        Schedule schedule = scheduleService.getScheduleById(id)
                .orElseThrow(() -> new RuntimeException("Jadwal tidak ditemukan"));

        classService.ensureAdmin(schedule.getKelas().getId(), principal.getUser().getId());

        model.addAttribute("schedule", schedule);
        return "schedules/edit_jadwal";
    }

    // UPDATE: Proses simpan perubahan — hanya admin kelas
    @PostMapping("/update/{id}")
    public String updateJadwal(@PathVariable Long id,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime dateTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime endDateTime,
            @RequestParam String place,
            @AuthenticationPrincipal UserPrincipal principal) {

        Schedule oldSchedule = scheduleService.getScheduleById(id)
                .orElseThrow(() -> new RuntimeException("Jadwal tidak ditemukan"));

        classService.ensureAdmin(oldSchedule.getKelas().getId(), principal.getUser().getId());

        String classCode = oldSchedule.getKelas().getClassCode();
        scheduleService.updateSchedule(id, dateTime, endDateTime, place);

        return "redirect:/class/" + classCode + "/jadwal";
    }

    // DELETE: Proses hapus — hanya admin kelas
    @GetMapping("/delete/{id}")
    public String deleteJadwal(@PathVariable Long id,
                               @AuthenticationPrincipal UserPrincipal principal) {
        Schedule schedule = scheduleService.getScheduleById(id)
                .orElseThrow(() -> new RuntimeException("Jadwal tidak ditemukan"));

        classService.ensureAdmin(schedule.getKelas().getId(), principal.getUser().getId());

        String classCode = schedule.getKelas().getClassCode();
        scheduleService.deleteSchedule(id);

        return "redirect:/class/" + classCode + "/jadwal";
    }
}