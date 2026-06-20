package id.adrianz.ruangkelas.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

import id.adrianz.ruangkelas.model.Task;
import id.adrianz.ruangkelas.model.NotificationType;
import id.adrianz.ruangkelas.model.UserClass;
import id.adrianz.ruangkelas.model.User;
import id.adrianz.ruangkelas.repository.TaskRepository;
import id.adrianz.ruangkelas.repository.UserClassRepository;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final NotificationService notificationService;
    private final UserClassRepository userClassRepository;

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task dengan ID " + id + " tidak ditemukan"));
    }

    public Task createTask(Task task) {
        Task savedTask = taskRepository.save(task);

        try {
            List<UserClass> members = userClassRepository.findByClasseId(savedTask.getClasse().getId());
            
            for (UserClass member : members) {
                User user = member.getUser();
                notificationService.createAndSendEmailNotification(
                    Math.toIntExact(user.getId()),
                    user.getEmail(),
                    "Tugas Baru: " + savedTask.getTitle(),
                    "Ada tugas baru yang ditambahkan di kelas Anda. Segera cek sistem!",
                    NotificationType.TASK_CREATED,
                    Math.toIntExact(savedTask.getId()),
                    "TASK"
                );
            }
        } catch (Exception e) {
            System.out.println("Gagal mengirim notifikasi email: " + e.getMessage());
        }

        return savedTask;
    }

    public Task updateTask(Long id, Task updatedTask) {
        Task existingTask = getTaskById(id);

        boolean isDeadlineChanged = false;
        if (existingTask.getDeadline() != null && updatedTask.getDeadline() != null) {
            isDeadlineChanged = !existingTask.getDeadline().equals(updatedTask.getDeadline());
        } else if (existingTask.getDeadline() == null && updatedTask.getDeadline() != null) {
            isDeadlineChanged = true;
        }

        existingTask.setTitle(updatedTask.getTitle());
        existingTask.setDescription(updatedTask.getDescription());
        existingTask.setDeadline(updatedTask.getDeadline());

        Task savedTask = taskRepository.save(existingTask);

        if (isDeadlineChanged) {
            try {
                List<UserClass> members = userClassRepository.findByClasseId(savedTask.getClasse().getId());
                
                for (UserClass member : members) {
                    User user = member.getUser();
                    notificationService.createAndSendEmailNotification(
                        Math.toIntExact(user.getId()),
                        user.getEmail(),
                        "Perubahan Deadline: " + savedTask.getTitle(),
                        "Deadline untuk tugas '" + savedTask.getTitle() + "' telah diubah menjadi: " + savedTask.getDeadline().toString(),
                        NotificationType.DEADLINE_REMINDER,
                        Math.toIntExact(savedTask.getId()),
                        "TASK"
                    );
                }
            } catch (Exception e) {
                System.out.println("Gagal mengirim notifikasi email perubahan deadline: " + e.getMessage());
            }
        }

        return savedTask;
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    public List<Task> getTasksByClassCode(String classCode) {
        return taskRepository.findByClasseClassCodeOrderByDeadlineAsc(classCode);
    }

    public Long getClassId(Long taskId) {
        Task task = getTaskById(taskId);
        return task.getClasse().getId();
    }
}