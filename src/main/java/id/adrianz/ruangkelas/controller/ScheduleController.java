package id.adrianz.ruangkelas.controller;

import id.adrianz.ruangkelas.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/schedules")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    // Mengarahkan browser ke halaman daftar jadwal saat mengetik link localhost:8080/schedules
    @GetMapping
    public String listSchedules(Model model) {
        model.addAttribute("schedules", scheduleService.getAllSchedules());
        return "schedules/index"; // Ini mengarah ke folder templates/schedules/index.html nanti
    }
}