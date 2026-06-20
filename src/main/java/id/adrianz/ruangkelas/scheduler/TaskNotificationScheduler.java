package id.adrianz.ruangkelas.scheduler;

import java.time.LocalDate;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import id.adrianz.ruangkelas.service.EmailService;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class TaskNotificationScheduler {

    private final EmailService emailService;

    @Scheduled(cron = "0 0 8 * * ?")
    public void checkUpcomingDeadlines() {
        LocalDate today = LocalDate.now();
        LocalDate hMinus3 = today.plusDays(3);
        LocalDate hMinus1 = today.plusDays(1);

        processDeadline(hMinus3, "Pengingat: Deadline Tugas H-3");
        processDeadline(hMinus1, "Peringatan: Deadline Tugas H-1");
    }

    private void processDeadline(LocalDate targetDate, String subject) {
        String dummyEmail = "siswa@contoh.com";
        String text = "Harap perhatikan tugas kelas Anda dengan tenggat waktu pada: " + targetDate.toString();

        emailService.sendSimpleMessage(dummyEmail, subject, text);
    }
}
