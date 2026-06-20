package id.adrianz.ruangkelas.scheduler;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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

@Component
@RequiredArgsConstructor
public class TaskNotificationScheduler {

    private final TaskRepository taskRepository;
    private final NotificationService notificationService;
    private final UserClassRepository userClassRepository;

    @Scheduled(cron = "0 0 8 * * ?")
    public void checkUpcomingDeadlines() {
        LocalDate today = LocalDate.now();
        LocalDate hMinus3 = today.plusDays(3);
        LocalDate hMinus1 = today.plusDays(1);

        processDeadline(hMinus3, "Pengingat: Deadline Tugas H-3", NotificationType.TASK_REMINDER);
        processDeadline(hMinus1, "Peringatan: Deadline Tugas H-1", NotificationType.TASK_REMINDER);
    }

    private void processDeadline(LocalDate targetDate, String subject, NotificationType type) {
        LocalDateTime startOfDay = targetDate.atStartOfDay();
        LocalDateTime endOfDay = targetDate.atTime(23, 59, 59);

        List<Task> tasks = taskRepository.findByDeadlineBetween(startOfDay, endOfDay);

        for (Task task : tasks) {
            List<UserClass> members = userClassRepository.findByClasseId(task.getClasse().getId());
            
            for (UserClass member : members) {
                User user = member.getUser();
                notificationService.createAndSendEmailNotification(
                    Math.toIntExact(user.getId()), 
                    user.getEmail(), 
                    subject + " - " + task.getTitle(), 
                    "Harap perhatikan, tugas '" + task.getTitle() + "' akan segera berakhir pada: " + task.getDeadline().toString(), 
                    type, 
                    Math.toIntExact(task.getId()), 
                    "TASK"
                );
            }
        }
    }
}