package id.adrianz.ruangkelas.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${app.base-url}")
    private String baseUrl;

    public void sendSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    // templatePath should located under resources/templates/email/
    public void sendTemplateMessage(String to, String subject, String templatePath, Context context) {
        String html = templateEngine.process(templatePath, context);
        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Gagal mengirim email: " + subject, e);
        }
    }

    // USE CASES
    public void sendVerificationEmail(String to, String token, LocalDateTime expiresAt) {
        String link = baseUrl + "/auth/verify?token=" + token;
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

        Context ctx = new Context();
        ctx.setVariable("link", link);
        ctx.setVariable("expiry", expiresAt.format(fmt));

        sendTemplateMessage(to, "Verifikasi Akun", "email/verification", ctx);
    }

    public void sendResetPasswordEmail(String to, String otp, LocalDateTime expiresAt) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

        Context ctx = new Context();
        ctx.setVariable("otp", otp);
        ctx.setVariable("expiry", expiresAt.format(fmt));

        sendTemplateMessage(to, "Kode OTP Reset Password", "email/resetpassword", ctx);
    }
}