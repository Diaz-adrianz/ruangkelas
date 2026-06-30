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

import id.adrianz.ruangkelas.dto.CreateSubTaskDto;
import id.adrianz.ruangkelas.model.Class;
import id.adrianz.ruangkelas.model.SubTask;
import id.adrianz.ruangkelas.model.Task;
import id.adrianz.ruangkelas.model.UserPrincipal;
import id.adrianz.ruangkelas.service.ClassService;
import id.adrianz.ruangkelas.service.SubTaskService;
import id.adrianz.ruangkelas.service.TaskService;
import jakarta.validation.Valid;
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

                Class classs = classService.getByCode(classCode);
                Task task = taskService.getTaskById(taskId);

                model.addAttribute("classs", classs);
                model.addAttribute("task", task);
                model.addAttribute("createSubTaskDto", new CreateSubTaskDto());

                return "pages/Subtask/Create";
        }

        @PostMapping("/create")
        public String create(
                        @PathVariable String classCode,
                        @PathVariable Long taskId,
                        @Valid @ModelAttribute("createSubTaskDto") CreateSubTaskDto dto,
                        BindingResult bindingResult,
                        @AuthenticationPrincipal UserPrincipal principal,
                        RedirectAttributes redirectAttributes,
                        Model model) {
                Class classs = classService.getByCode(classCode);
                Task task = taskService.getTaskById(taskId);

                if (bindingResult.hasErrors()) {
                        model.addAttribute("classs", classs);
                        model.addAttribute("task", task);
                        return "pages/Subtask/Create";
                }

                try {
                        SubTask subtask = SubTask.builder()
                                        .title(dto.getTitle())
                                        .description(dto.getDescription())
                                        .deadline(dto.getDeadline())
                                        .task(task)
                                        .createdBy(principal.getUser())
                                        .build();

                        subTaskService.createSubtask(subtask);
                } catch (RuntimeException e) {
                        model.addAttribute("classs", classs);
                        model.addAttribute("task", task);
                        return "pages/Subtask/Create";
                }

                redirectAttributes.addFlashAttribute("success", "Sub tugas berhasil ditambahkan");
                return "redirect:/class/" + classCode + "/tasks/" + taskId + "/subtasks";
        }

        @PostMapping("/{subtaskId}/delete")
        public String delete(
                        @PathVariable String classCode,
                        @PathVariable Long taskId,
                        @PathVariable Long subtaskId,
                        RedirectAttributes redirectAttributes) {

                try {
                        subTaskService.deleteSubtask(subtaskId);
                        redirectAttributes.addFlashAttribute("success", "Sub tugas berhasil dihapus");
                } catch (RuntimeException e) {
                        redirectAttributes.addFlashAttribute("error", e.getMessage());
                }

                return "redirect:/class/" + classCode + "/tasks/" + taskId + "/subtasks";
        }
}