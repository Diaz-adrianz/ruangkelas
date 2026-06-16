package id.adrianz.ruangkelas.controller;

import id.adrianz.ruangkelas.model.Task;
import id.adrianz.ruangkelas.model.Class;
import id.adrianz.ruangkelas.service.TaskService;
import id.adrianz.ruangkelas.service.ClassService; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/tasks")
public class TaskViewController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private ClassService classService; 

    // Menampilkan halaman daftar task
    @GetMapping
    public String index(Model model) {
        model.addAttribute("tasks", taskService.getAllTasks());
        return "task/index";
    }

    // Menampilkan form tambah task
    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("task", new Task());
        model.addAttribute("classes", classService.getAll()); 
        return "task/create";
    }

    // Memproses data dari form tambah task
    @PostMapping("/create")
    public String saveTask(@ModelAttribute("task") Task task, @RequestParam("classId") Long classId) {
        Class kelasPilihan = classService.getById(classId);
        task.setClasse(kelasPilihan);
        task.setStatus("Belum Selesai"); 
        taskService.createTask(task);
        return "redirect:/tasks"; 
    }

    // TAMBAHAN: Menampilkan form Edit Task
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable("id") Long id, Model model) {
        Task task = taskService.getTaskById(id);
        model.addAttribute("task", task);
        model.addAttribute("classes", classService.getAll()); // Untuk dropdown pilihan kelas
        return "task/edit";
    }

    // TAMBAHAN: Memproses Update data Task ke Database
    @PostMapping("/edit/{id}")
    public String updateTask(@PathVariable("id") Long id, 
                             @ModelAttribute("task") Task formTask, 
                             @RequestParam("classId") Long classId) {
        // 1. Ambil data asli dari database
        Task existingTask = taskService.getTaskById(id);
        
        // 2. Update data lama dengan data baru dari form
        existingTask.setTitle(formTask.getTitle());
        existingTask.setDescription(formTask.getDescription());
        existingTask.setClasse(classService.getById(classId));
        // status tetap dipertahankan atau bisa diubah sesuai kebutuhan nantinya

        // 3. Simpan kembali ke database
        taskService.createTask(existingTask);
        return "redirect:/tasks";
    }

    // TAMBAHAN: Menghapus Task
    @GetMapping("/delete/{id}")
    public String deleteTask(@PathVariable("id") Long id) {
        taskService.deleteTask(id);
        return "redirect:/tasks"; // Setelah dihapus, balik lagi ke daftar task
    }
}