package id.adrianz.ruangkelas.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import id.adrianz.ruangkelas.dto.CreateTaskDto;
import id.adrianz.ruangkelas.dto.UpdateTaskDto;
import id.adrianz.ruangkelas.model.Class;
import id.adrianz.ruangkelas.model.Task;
import id.adrianz.ruangkelas.model.UserClass;
import id.adrianz.ruangkelas.model.UserPrincipal;
import id.adrianz.ruangkelas.service.ClassService;
import id.adrianz.ruangkelas.service.SubTaskService;
import id.adrianz.ruangkelas.service.TaskService;
import id.adrianz.ruangkelas.service.CommentService;
import id.adrianz.ruangkelas.service.TaskSubmissionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/class/{classCode}/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final ClassService classService;
    private final SubTaskService subTaskService;
    private final CommentService commentService;
    private final TaskSubmissionService taskSubmissionService;

    @GetMapping("/create")
    public String showCreateForm(@PathVariable String classCode, Model model) {
        Class classs = classService.getByCode(classCode);
        model.addAttribute("classs", classs);
        model.addAttribute("createTaskDto", new CreateTaskDto());
        return "pages/Task/Create";
    }

    @PostMapping("/create")
    public String createTask(@PathVariable String classCode,
            @Valid @ModelAttribute("createTaskDto") CreateTaskDto request,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes,
            @AuthenticationPrincipal UserPrincipal principal) {
        Class classs = classService.getByCode(classCode);

        if (result.hasErrors()) {
            model.addAttribute("classs", classs);
            model.addAttribute("errors", result.getFieldErrors());
            return "pages/Task/create";
        }

        try {
            classService.ensureAdmin(classs.getId(), principal.getUser().getId());

            Task task = Task.builder()
                    .classe(classs)
                    .title(request.getTitle())
                    .description(request.getDescription())
                    .status("PENDING")
                    .deadline(request.getDeadline())
                    .createdBy(principal.getUser())
                    .build();

            taskService.createTask(task);
        } catch (Exception e) {
            model.addAttribute("classs", classs);
            model.addAttribute("error", e.getMessage());
            return "pages/Task/create";
        }

        redirectAttributes.addFlashAttribute("success", "Tugas berhasil ditambahkan");
        return "redirect:/class/" + classCode + "#tasks";
    }

    @PostMapping("/{taskId}/submit")
public String submitTask(
        @PathVariable String classCode,
        @PathVariable Long taskId,
        @AuthenticationPrincipal UserPrincipal principal,
        RedirectAttributes redirectAttributes) {

    try {

        Class classs = classService.getByCode(classCode);

        Task task = taskService.getTaskById(taskId);

        UserClass userClass = classService.getUserClass(
                classs.getId(),
                principal.getUser().getId());

        taskSubmissionService.submitTask(task, userClass);

        redirectAttributes.addFlashAttribute(
                "success",
                "Tugas berhasil disubmit.");

    } catch (Exception e) {

        redirectAttributes.addFlashAttribute(
                "error",
                e.getMessage());

    }

    return "redirect:/class/" + classCode + "/tasks/" + taskId;
}

    @GetMapping("/{taskId}/edit")
    public String showEditForm(@PathVariable String classCode, @PathVariable Long taskId, Model model) {
        Class classs = classService.getByCode(classCode);
        Task task = taskService.getTaskById(taskId);

        UpdateTaskDto updateTaskDto = new UpdateTaskDto();
        updateTaskDto.setTitle(task.getTitle());
        updateTaskDto.setDescription(task.getDescription());
        updateTaskDto.setDeadline(task.getDeadline());

        model.addAttribute("classs", classs);
        model.addAttribute("task", task);
        model.addAttribute("updateTaskDto", updateTaskDto);
        return "pages/Task/Edit";
    }

    @PostMapping("/{taskId}/edit")
    public String updateTask(@PathVariable String classCode,
            @PathVariable Long taskId,
            @Valid @ModelAttribute("updateTaskDto") UpdateTaskDto request,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes,
            @AuthenticationPrincipal UserPrincipal principal) {
        Class classs = classService.getByCode(classCode);
        Task task = taskService.getTaskById(taskId);

        if (result.hasErrors()) {
            model.addAttribute("classs", classs);
            model.addAttribute("task", task);
            model.addAttribute("errors", result.getFieldErrors());
            return "pages/Task/Edit";
        }

        try {
            classService.ensureAdmin(classs.getId(), principal.getUser().getId());

            Task updatedTaskData = new Task();
            updatedTaskData.setTitle(request.getTitle());
            updatedTaskData.setDescription(request.getDescription());
            updatedTaskData.setDeadline(request.getDeadline());

            taskService.updateTask(taskId, updatedTaskData);
        } catch (Exception e) {
            model.addAttribute("classs", classs);
            model.addAttribute("task", task);
            model.addAttribute("error", e.getMessage());
            return "pages/Task/Edit";
        }

        redirectAttributes.addFlashAttribute("success", "Tugas berhasil diedit");
        return "redirect:/class/" + classCode + "/tasks/" + taskId;
    }

    @PostMapping("/{taskId}/delete")
    public String deleteTask(@PathVariable String classCode,
            @PathVariable Long taskId,
            RedirectAttributes redirectAttributes,
            Model model,
            @AuthenticationPrincipal UserPrincipal principal) {
        try {
            Class classs = classService.getByCode(classCode);

            classService.ensureAdmin(classs.getId(), principal.getUser().getId());
            taskService.deleteTask(taskId);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/class/" + classCode + "/tasks/" + taskId;
        }

        redirectAttributes.addFlashAttribute("success", "Tugas berhasil dihapus");
        return "redirect:/class/" + classCode + "#tasks";
    }

    @GetMapping("/{taskId}")
    public String getMethodName(@PathVariable String classCode,
            @PathVariable Long taskId,
            Model model,
            @AuthenticationPrincipal UserPrincipal principal) {
        Class classs = classService.getByCode(classCode);
        boolean isAdmin = classService.isAdmin(classs.getId(), principal.getUser().getId());
        model.addAttribute("classs", classs);
        model.addAttribute("isAdmin", isAdmin);

        Task task = taskService.getTaskById(taskId);

        model.addAttribute("task", task);

        model.addAttribute("subtasks", subTaskService.getSubtasksByTaskId(taskId));

        // 🌟 DI SINI PERUBAHAN UTAMANYA: Menggunakan method penampung baru
        var comments = commentService.getComments(taskId, principal.getUser());
        model.addAttribute("comments", comments);
        model.addAttribute("currentUserId", principal.getUser().getId());

        model.addAttribute("submissions", taskSubmissionService.getSubmissionByTask(taskId));

        return "pages/Task/Detail";
    }
}