package id.adrianz.ruangkelas.controller;

import id.adrianz.ruangkelas.model.Schedule;
import id.adrianz.ruangkelas.repository.ClassRepository;
import id.adrianz.ruangkelas.model.Class;
import id.adrianz.ruangkelas.service.ScheduleService;
import id.adrianz.ruangkelas.service.ClassService;
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

    // READ: Tampil detail satu jadwal
    @GetMapping("/{id}")
    public String showDetailJadwal(@PathVariable Long id, Model model) {
        Schedule schedule = scheduleService.getScheduleById(id)
                .orElseThrow(() -> new RuntimeException("Jadwal tidak ditemukan"));
        model.addAttribute("schedule", schedule);
        return "schedules/detail_jadwal";
    }

    // CREATE: Tampilkan halaman form tambah jadwal
    @GetMapping("/create/{classCode}")
    public String createJadwalForm(@PathVariable String classCode, Model model) {
        Class kelas = classService.getByCode(classCode);
        model.addAttribute("classs", kelas);
        return "schedules/tambah_jadwal";
    }

    // CREATE: Proses simpan jadwal baru
    @PostMapping("/save")
    public String saveJadwal(
            @RequestParam Long classId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime dateTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime endDateTime,
            @RequestParam String place) {

        Class kelas = classRepository.findByIdWithCourse(classId)
                .orElseThrow(() -> new RuntimeException("Kelas tidak ditemukan"));

        scheduleService.createSchedule(classId, dateTime, endDateTime, place);

        return "redirect:/class/" +
                kelas.getClassCode() +
                "/jadwal";
    }

    // UPDATE: Proses simpan perubahan
    @PostMapping("/update/{id}")
    public String updateJadwal(@PathVariable Long id,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime dateTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime endDateTime,
            @RequestParam String place) {

        Schedule oldSchedule = scheduleService.getScheduleById(id)
                .orElseThrow(() ->
                        new RuntimeException("Jadwal tidak ditemukan"));

        String classCode = oldSchedule.getKelas().getClassCode();

        scheduleService.updateSchedule(id, dateTime, endDateTime, place);

        return "redirect:/class/" + classCode + "/jadwal";
    }

    @GetMapping("/edit/{id}")
    public String editJadwal(@PathVariable Long id, Model model) {

        Schedule schedule = scheduleService.getScheduleById(id)
                .orElseThrow(() ->
                        new RuntimeException("Jadwal tidak ditemukan"));

        model.addAttribute("schedule", schedule);

        return "schedules/edit_jadwal";
    }

    // DELETE: Proses hapus
    @GetMapping("/delete/{id}")
    public String deleteJadwal(@PathVariable Long id) {

        Schedule schedule = scheduleService.getScheduleById(id)
                .orElseThrow(() -> new RuntimeException("Jadwal tidak ditemukan"));

        String classCode = schedule.getKelas().getClassCode();

        scheduleService.deleteSchedule(id);

        return "redirect:/class/" + classCode + "/jadwal";
    }

}