package id.adrianz.ruangkelas.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import id.adrianz.ruangkelas.model.Task;
import id.adrianz.ruangkelas.service.TaskService;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationRedirectController {

    private final TaskService taskService;

    @GetMapping("/redirect")
    public String handleRedirect(@RequestParam String type, @RequestParam Integer id) {
        if ("TASK".equalsIgnoreCase(type)) {
            Task task = taskService.getTaskById(Long.valueOf(id));
            String classCode = task.getClasse().getClassCode();
            return "redirect:/class/" + classCode + "/tasks/" + id;
        }
        
        return "redirect:/"; 
    }
}