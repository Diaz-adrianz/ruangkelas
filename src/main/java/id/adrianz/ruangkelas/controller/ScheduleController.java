package id.adrianz.ruangkelas.controller;

import id.adrianz.ruangkelas.model.Schedule;
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

    // READ: Tampil semua jadwal (bisa untuk member & admin)
    @GetMapping
    public String showJadwal(Model model) {
        model.addAttribute("schedules", scheduleService.getAllSchedules());
        return "jadwal_list"; // Nama file HTML kamu
    }

    // CREATE: Proses simpan jadwal baru
    @PostMapping("/save")
    public String saveJadwal(@ModelAttribute Schedule schedule) {
        scheduleService.saveSchedule(schedule);
        return "redirect:/admin/jadwal";
    }

    // UPDATE: Proses simpan perubahan
    @PostMapping("/update/{id}")
    public String updateJadwal(@PathVariable Long id, @ModelAttribute Schedule schedule) {
        scheduleService.updateSchedule(id, schedule);
        return "redirect:/admin/jadwal";
    }

    // DELETE: Proses hapus
    @GetMapping("/delete/{id}")
    public String deleteJadwal(@PathVariable Long id) {
        scheduleService.deleteSchedule(id);
        return "redirect:/admin/jadwal";
    }
}