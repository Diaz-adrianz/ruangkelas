package id.adrianz.ruangkelas.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import id.adrianz.ruangkelas.repository.TaskRepository;
import id.adrianz.ruangkelas.repository.ScheduleRepository;
import id.adrianz.ruangkelas.model.Task;
import id.adrianz.ruangkelas.model.Schedule;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final TaskRepository taskRepository;
    private final ScheduleRepository scheduleRepository;

    public Map<String, Object> getDashboardWidgetsData(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextWeek = now.plusDays(7);

        long overdueCount = taskRepository.countOverdueTasksForUser(userId, now);

        List<Task> upcomingDeadlines = taskRepository.findUpcomingDeadlinesForUser(userId, now, nextWeek);

        Schedule nextSchedule = scheduleRepository.findNextScheduleForUser(userId, now).orElse(null);
        String nextScheduleText = "-";
        if (nextSchedule != null) {
            long hoursBetween = ChronoUnit.HOURS.between(now, nextSchedule.getStartTime());
            long daysBetween = ChronoUnit.DAYS.between(now, nextSchedule.getStartTime());
            
            if (daysBetween > 0) {
                nextScheduleText = "Dalam " + daysBetween + " hari";
            } else if (hoursBetween > 0) {
                nextScheduleText = "Dalam " + hoursBetween + " jam";
            } else {
                nextScheduleText = "Kurang dari 1 jam";
            }
        }

        long totalTasks = taskRepository.countTotalTasksForUser(userId);
        long submittedTasks = taskRepository.countTotalSubmittedTasksForUser(userId);
        double completionRate = 0.0;
        if (totalTasks > 0) {
            completionRate = ((double) submittedTasks / totalTasks) * 100;
        }

        Map<String, Object> widgets = new HashMap<>();
        widgets.put("overdueCount", overdueCount);
        widgets.put("upcomingDeadlines", upcomingDeadlines);
        widgets.put("nextSchedule", nextSchedule);
        widgets.put("nextScheduleText", nextScheduleText);
        widgets.put("completionRate", Math.round(completionRate));

        return widgets;
    }
}