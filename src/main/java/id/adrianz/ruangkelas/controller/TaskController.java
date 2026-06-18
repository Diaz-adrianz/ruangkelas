package id.adrianz.ruangkelas.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import id.adrianz.ruangkelas.dto.TaskDto;
import id.adrianz.ruangkelas.model.Class;
import id.adrianz.ruangkelas.model.Task;
import id.adrianz.ruangkelas.model.UserPrincipal;
import id.adrianz.ruangkelas.service.ClassService;
import id.adrianz.ruangkelas.service.TaskService;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/class/{classId}/tasks") 
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final ClassService classService;

    // 1. Tampilan Form Buat Task Baru
    @GetMapping("/create")
    public String showCreateForm(@PathVariable Long classId, Model model) {
        Class classs = classService.getById(classId);
        model.addAttribute("classs", classs);
        model.addAttribute("taskDto", new TaskDto());
        return "pages/Task/Create";
    }

    // 2. Proses Simpan Task Baru
    @PostMapping("/create")
    public String createTask(@PathVariable Long classId, 
                             @ModelAttribute TaskDto taskDto,
                             @AuthenticationPrincipal UserPrincipal principal) { 
        Class classs = classService.getById(classId);
        
        Task task = Task.builder()
                .classe(classs)
                .title(taskDto.getTitle())
                .description(taskDto.getDescription())
                .status("PENDING")
                .deadline(taskDto.getDeadline())
                .createdBy(principal.getUser()) 
                .build();
                
        taskService.createTask(task); 
        
        // PERBAIKAN: Memastikan rute redirect bersifat absolut dari root server
        return "redirect:/class/" + classId; 
    }

    // 3. Tampilan Form Edit Task
    @GetMapping("/{taskId}/edit")
    public String showEditForm(@PathVariable Long classId, @PathVariable Long taskId, Model model) {
        Class classs = classService.getById(classId);
        Task task = taskService.getTaskById(taskId);
        
        TaskDto taskDto = new TaskDto();
        taskDto.setClassId(classId);
        taskDto.setTitle(task.getTitle());
        taskDto.setDescription(task.getDescription());
        taskDto.setDeadline(task.getDeadline());

        model.addAttribute("classs", classs);
        model.addAttribute("taskId", taskId);
        model.addAttribute("taskDto", taskDto);
        return "pages/Task/Edit";
    }

    // 4. Proses Simpan Update Task
    @PostMapping("/{taskId}/edit")
    public String updateTask(@PathVariable Long classId, @PathVariable Long taskId, @ModelAttribute TaskDto taskDto) {
        Task task = taskService.getTaskById(taskId);
        
        task.setTitle(taskDto.getTitle());
        task.setDescription(taskDto.getDescription());
        task.setDeadline(taskDto.getDeadline());
        
        taskService.createTask(task); 
        
        // PERBAIKAN: Memastikan rute redirect bersifat absolut dari root server
        return "redirect:/class/" + classId; 
    }

    // 5. Proses Hapus Task
    @PostMapping("/{taskId}/delete")
    public String deleteTask(@PathVariable Long classId, @PathVariable Long taskId) {
        taskService.deleteTask(taskId);
        
        // PERBAIKAN: Memastikan rute redirect bersifat absolut dari root server
        return "redirect:/class/" + classId; 
    }
}