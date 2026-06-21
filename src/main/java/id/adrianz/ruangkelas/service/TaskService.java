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

    public List<Task> getTasksByClassCode(String classCode) {
    return taskRepository.findByClasseClassCodeOrderByDeadlineAsc(classCode);
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
                    "Ada tugas baru '" + savedTask.getTitle() + "' di kelas Anda. Segera cek sistem!",
                    NotificationType.TASK_CREATED, // Pastikan ini ada di Enum
                    Math.toIntExact(savedTask.getId()),
                    "TASK"
                );
            }
        } catch (Exception e) {
            System.out.println("Gagal mengirim notifikasi tugas baru: " + e.getMessage());
        }

        return savedTask;
    }

   
  public Task updateTask(Long id, Task updatedTask) {
        Task existingTask = getTaskById(id);

        existingTask.setTitle(updatedTask.getTitle());
        existingTask.setDescription(updatedTask.getDescription());
        existingTask.setDeadline(updatedTask.getDeadline());

        Task savedTask = taskRepository.save(existingTask);

        try {
            List<UserClass> members = userClassRepository.findByClasseId(savedTask.getClasse().getId());
            
            for (UserClass member : members) {
                User user = member.getUser();
                notificationService.createAndSendEmailNotification(
                    Math.toIntExact(user.getId()),
                    user.getEmail(),
                    "Update Tugas: " + savedTask.getTitle(),
                    "Informasi tugas '" + savedTask.getTitle() + "' telah diperbarui.",
                    NotificationType.TASK_UPDATED, // Pastikan ini sesuai dengan Enum
                    Math.toIntExact(savedTask.getId()),
                    "TASK"
                );
            }
        } catch (Exception e) {
            System.out.println("Gagal mengirim notifikasi email update: " + e.getMessage());
        }

        return savedTask;
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }
    

}