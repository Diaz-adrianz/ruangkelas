package id.adrianz.ruangkelas.controller;

import id.adrianz.ruangkelas.model.Schedule;
import id.adrianz.ruangkelas.repository.ClassRepository;
import id.adrianz.ruangkelas.model.Class;
import id.adrianz.ruangkelas.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/jadwal")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;
    @Autowired
    private ClassRepository classRepository;

    // READ: Tampil semua jadwal (bisa untuk member & admin)
    @GetMapping
    public String showJadwal(Model model) {
        model.addAttribute("schedules", scheduleService.getAllSchedules());
        return "schedules/jadwal_list";
    }

    // CREATE: Proses simpan jadwal baru
   @PostMapping("/save")
    public String saveJadwal(@ModelAttribute Schedule schedule) {

        Long classId = schedule.getKelas().getId();

        Class kelas = classRepository.findById(classId)
                .orElseThrow(() ->
                        new RuntimeException("Kelas tidak ditemukan"));

        schedule.setKelas(kelas);

        scheduleService.saveSchedule(schedule);

        return "redirect:/class/" +
                kelas.getClassCode() +
                "/jadwal";
    }
    // UPDATE: Proses simpan perubahan
    @PostMapping("/update/{id}")
    public String updateJadwal(@PathVariable Long id,
                            @ModelAttribute Schedule schedule) {

        Schedule oldSchedule = scheduleService.getScheduleById(id)
                .orElseThrow(() ->
                        new RuntimeException("Jadwal tidak ditemukan"));

        String classCode = oldSchedule.getKelas().getClassCode();

        scheduleService.updateSchedule(id, schedule);

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