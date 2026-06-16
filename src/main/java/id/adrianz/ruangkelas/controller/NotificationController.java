package id.adrianz.ruangkelas.controller;

import id.adrianz.ruangkelas.service.EmailService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NotificationController {

    private final EmailService emailService;

    public NotificationController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping("/test-email")
    public String testEmail() {
        String to = "azkiyafauzanengginering@gmail.com";
        String subject = "Test Email Spring Boot UAS";
        String text = "Halo! Ini adalah email percobaan dari aplikasi Deadlin kelompok 3.";

        emailService.sendSimpleMessage(to, subject, text);

        return "Email berhasil dikirim ke " + to;
    }
}