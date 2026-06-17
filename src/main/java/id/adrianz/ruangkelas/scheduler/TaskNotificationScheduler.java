package id.adrianz.ruangkelas.scheduler;

import id.adrianz.ruangkelas.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskNotificationScheduler {

    private final NotificationService notificationService;

    // TODO: Aktifkan setelah entity Tugas & JadwalKuliah dibuat

    // @Scheduled(cron = "0 0 7 * * *")
    // public void notifikasiDeadlineH3() { ... }

    // @Scheduled(cron = "0 * * * * *")
    // public void notifikasiMataKuliah() { ... }
}