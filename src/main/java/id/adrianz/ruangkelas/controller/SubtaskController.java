package id.adrianz.ruangkelas.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import id.adrianz.ruangkelas.model.Class;
import id.adrianz.ruangkelas.model.SubTask;
import id.adrianz.ruangkelas.model.Task;
import id.adrianz.ruangkelas.model.UserPrincipal;
import id.adrianz.ruangkelas.service.ClassService;
import id.adrianz.ruangkelas.service.SubTaskService;
import id.adrianz.ruangkelas.service.TaskService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/class/{classCode}/tasks/{taskId}/subtasks")
@RequiredArgsConstructor
public class SubTaskController {

    private final SubTaskService subTaskService;
    private final TaskService taskService;
    private final ClassService classService;

    @GetMapping
    public String index(
            @PathVariable String classCode,
            @PathVariable Long taskId,
            Model model) {

        Class classs = classService.getByCode(classCode);
        Task task = taskService.getTaskById(taskId);

        model.addAttribute("classs", classs);
        model.addAttribute("task", task);
        model.addAttribute(
                "subtasks",
                subTaskService.getSubtasksByTaskId(taskId));

        return "pages/Subtask/Index";
    }

    @GetMapping("/create")
    public String createForm(
            @PathVariable String classCode,
            @PathVariable Long taskId,
            Model model) {

        model.addAttribute("task",
                taskService.getTaskById(taskId));

        return "pages/Subtask/Create";
    }

    @PostMapping("/create")
    public String create(
            @PathVariable String classCode,
            @PathVariable Long taskId,
            @RequestParam String title,
            @RequestParam(required = false) String description,
            @AuthenticationPrincipal UserPrincipal principal) {

        Task task = taskService.getTaskById(taskId);

        SubTask subtask = SubTask.builder()
                .title(title)
                .description(description)
                .task(task)
                .createdBy(principal.getUser())
                .build();

        subTaskService.createSubtask(subtask);

        return "redirect:/class/" + classCode
                + "/tasks/" + taskId
                + "/subtasks";
    }

    @PostMapping("/{subtaskId}/delete")
    public String delete(
            @PathVariable String classCode,
            @PathVariable Long taskId,
            @PathVariable Long subtaskId) {

        subTaskService.deleteSubtask(subtaskId);

        return "redirect:/class/" + classCode
                + "/tasks/" + taskId
                + "/subtasks";
    }
}