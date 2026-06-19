package id.adrianz.ruangkelas.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import id.adrianz.ruangkelas.model.Task;
import id.adrianz.ruangkelas.repository.TaskRepository;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Task dengan ID " + id + " tidak ditemukan"));
    }

    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    public List<Task> getTasksByClassId(Long classId) {
        return taskRepository.findByClasseIdOrderByDeadlineAsc(classId);
    }

    public Long getClassId(Long taskId) {

        Task task = getTaskById(taskId);

        return task.getClasse().getId();
    }
    
}