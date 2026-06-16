package id.adrianz.ruangkelas.service;

import id.adrianz.ruangkelas.model.Task;
import id.adrianz.ruangkelas.model.SubTask;
import id.adrianz.ruangkelas.repository.TaskRepository;
import id.adrianz.ruangkelas.repository.SubTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private SubTaskRepository subTaskRepository;

    // 1. Mengambil semua data Task
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    // 2. Membuat Task baru / Menyimpan perubahan Task
    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    // 3. Menghapus Task berdasarkan ID
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    // TAMBAHKAN INI: Mengambil 1 data Task berdasarkan ID untuk keperluan Edit
    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task dengan ID " + id + " tidak ditemukan"));
    }

    // 4. Menambahkan SubTask ke dalam Task tertentu
    public SubTask addSubTask(Long taskId, SubTask subTask) {
        return taskRepository.findById(taskId).map(task -> {
            subTask.setTask(task); 
            return subTaskRepository.save(subTask);
        }).orElseThrow(() -> new RuntimeException("Task dengan ID " + taskId + " tidak ditemukan"));
    }
}