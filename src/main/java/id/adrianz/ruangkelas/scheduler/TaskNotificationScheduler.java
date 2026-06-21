package id.adrianz.ruangkelas.scheduler;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.annotation.PostConstruct;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import id.adrianz.ruangkelas.model.Task;
import id.adrianz.ruangkelas.model.User;
import id.adrianz.ruangkelas.model.UserClass;
import id.adrianz.ruangkelas.model.NotificationType;
import id.adrianz.ruangkelas.repository.TaskRepository;
import id.adrianz.ruangkelas.repository.UserClassRepository;
import id.adrianz.ruangkelas.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskNotificationScheduler {

    private final TaskRepository taskRepository;
    private final NotificationService notificationService;
    private final UserClassRepository userClassRepository;

    @PostConstruct
    public void init() {
        log.info(">>> TaskNotificationScheduler BEAN BERHASIL DI-LOAD <<<");
    }

    @Scheduled(cron = "0 0 7 * * ?")
    public void checkUpcomingDeadlines() {
        log.info(">>> SCHEDULER checkUpcomingDeadlines TRIGGERED <<<");

        LocalDate today = LocalDate.now();
        LocalDate hMinus3 = today.plusDays(3);
        LocalDate hMinus1 = today.plusDays(1);

        processDeadline(hMinus3, "Pengingat: Deadline Tugas H-3", NotificationType.DEADLINE_REMINDER);
        processDeadline(hMinus1, "Peringatan: Deadline Tugas H-1", NotificationType.DEADLINE_REMINDER);
    }

    private void processDeadline(LocalDate targetDate, String title, NotificationType type) {
    LocalDateTime startOfDay = targetDate.atStartOfDay();
    LocalDateTime endOfDay = targetDate.atTime(23, 59, 59);

    List<Task> tasks = taskRepository.findByDeadlineBetween(startOfDay, endOfDay);
    log.info(">>> Tasks ditemukan untuk tanggal {}: {}", targetDate, tasks.size());

    for (Task task : tasks) {
        List<UserClass> members = userClassRepository.findByClasseId(task.getClasse().getId());
        String body = "Halo, tugas '" + task.getTitle() + "' akan berakhir pada " + task.getDeadline().toString() + ". Segera selesaikan!";

        for (UserClass member : members) {
            User user = member.getUser();

            if (notificationService.alreadyNotifiedToday(user.getId(), task.getId(), "TASK", type)) {
                log.info(">>> User {} sudah dinotif hari ini untuk task {}, skip", user.getId(), task.getId());
                continue;
            }

            notificationService.createAndSendPushNotification(
                    user,
                    title,
                    body,
                    type,
                    task.getId(),
                    "TASK"
            );
        }
    }
}
}