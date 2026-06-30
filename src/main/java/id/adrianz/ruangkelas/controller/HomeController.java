package id.adrianz.ruangkelas.controller;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import id.adrianz.ruangkelas.model.Class;
import id.adrianz.ruangkelas.model.UserClass;
import id.adrianz.ruangkelas.model.UserPrincipal;
import id.adrianz.ruangkelas.service.ClassService;
import id.adrianz.ruangkelas.service.DashboardService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ClassService classService;
    private final DashboardService dashboardService;

    @GetMapping
    public String index(Model model, @AuthenticationPrincipal UserPrincipal principal) {
        try {
            Long userId = principal.getUser().getId();
            
            // Ambil data kelas
            List<Class> classes = classService.getAllForUser(userId);
            List<UserClass> rejections = classService.getRejectedForUser(userId);

            model.addAttribute("classes", classes);
            model.addAttribute("rejections", rejections);

            // ================= START: DASHBOARD WIDGETS =================
            Map<String, Object> widgets = dashboardService.getDashboardWidgetsData(userId);
            model.addAllAttributes(widgets);
            // ================= END: DASHBOARD WIDGETS =================

        } catch (Exception e) {
            // Jika terjadi error (misal query gagal), tangkap dan kirim ke frontend
            model.addAttribute("error", "Gagal memuat data dasbor: " + e.getMessage());
        }

        return "pages/Home";
    }
}