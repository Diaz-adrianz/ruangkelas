package id.adrianz.ruangkelas.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import id.adrianz.ruangkelas.service.EmailService;
import lombok.AllArgsConstructor;

@Controller
@RequestMapping("/notification")
@AllArgsConstructor
public class NotificationController {

    private final EmailService emailService;

    @GetMapping("/tes-email")
    @ResponseBody
    public String testEmail(@RequestParam(required = false) String to) {
        if (to == null || to.isBlank()) {
            return "Email penerima diperlukan";
        }

        String subject = "Test Email Spring Boot UAS";
        String text = "Halo! Ini adalah email percobaan dari aplikasi Deadlin kelompok 3.";

        emailService.sendSimpleMessage(to, subject, text);

        return "Email berhasil dikirim ke " + to;
    }
}
