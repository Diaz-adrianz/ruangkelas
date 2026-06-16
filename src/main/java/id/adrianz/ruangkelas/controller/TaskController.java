package id.adrianz.ruangkelas.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import id.adrianz.ruangkelas.model.SubTask;
import id.adrianz.ruangkelas.model.Task;
import id.adrianz.ruangkelas.service.TaskService;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*") // Mengizinkan akses dari frontend manapun
public class TaskController {

    @Autowired
    private TaskService taskService;

    // Endpoint untuk mendapatkan semua task (GET http://localhost:8080/api/tasks)
    @GetMapping
    public List<Task> getAllTasks() {
        return taskService.getAllTasks();
    }

    // Endpoint untuk membuat task baru (POST http://localhost:8080/api/tasks)
    @PostMapping
    public Task createTask(@RequestBody Task task) {
        return taskService.createTask(task);
    }

    // Endpoint untuk menghapus task (DELETE http://localhost:8080/api/tasks/{id})
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.ok().body("Task berhasil dihapus");
    }

    // Endpoint untuk menambah subtask ke dalam task tertentu (POST http://localhost:8080/api/tasks/{taskId}/subtasks)
    @PostMapping("/{taskId}/subtasks")
    public ResponseEntity<SubTask> addSubTask(@PathVariable Long taskId, @RequestBody SubTask subTask) {
        SubTask savedSubTask = taskService.addSubTask(taskId, subTask);
        return ResponseEntity.ok(savedSubTask);
    }
}