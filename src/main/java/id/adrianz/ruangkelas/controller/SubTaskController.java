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
import id.adrianz.ruangkelas.dto.UpdateSubTaskDto;
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
                return "redirect:/class/" + classCode + "/tasks/" + taskId + "#subtasks";
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

                return "redirect:/class/" + classCode + "/tasks/" + taskId + "#subtasks";
        }

        @GetMapping("/{subTaskId}/edit")
        public String updateForm(@PathVariable String classCode,
                        @PathVariable Long taskId,
                        @PathVariable Long subTaskId,
                        Model model) {

                Class classs = classService.getByCode(classCode);
                Task task = taskService.getTaskById(taskId);
                SubTask subTask = subTaskService.getSubtaskById(subTaskId);

                UpdateSubTaskDto dto = new UpdateSubTaskDto();
                dto.setTitle(subTask.getTitle());
                dto.setDescription(subTask.getDescription());
                dto.setDeadline(subTask.getDeadline());

                model.addAttribute("classs", classs);
                model.addAttribute("task", task);
                model.addAttribute("subtask", subTask);
                model.addAttribute("updateSubTaskDto", dto);

                return "pages/Subtask/Edit";
        }

        @PostMapping("/{subTaskId}/update")
        public String edit(@PathVariable String classCode,
                        @PathVariable Long taskId,
                        @PathVariable Long subTaskId,
                        @Valid @ModelAttribute("updateSubTaskDto") UpdateSubTaskDto request,
                        BindingResult result,
                        Model model,
                        @AuthenticationPrincipal UserPrincipal principal,
                        RedirectAttributes redirectAttributes) {

                Class kelas = classService.getByCode(classCode);
                Task task = taskService.getTaskById(taskId);
                SubTask subTask = subTaskService.getSubtaskById(subTaskId);

                if (result.hasErrors()) {
                        model.addAttribute("classs", kelas);
                        model.addAttribute("task", task);
                        model.addAttribute("subtask", subTask);
                        model.addAttribute("errors", result.getFieldErrors());
                        return "pages/Subtask/Edit";
                }

                try {
                        subTask.setTitle(request.getTitle());
                        subTask.setDescription(request.getDescription());
                        subTask.setDeadline(request.getDeadline());
                        subTaskService.updateSubtask(subTask);
                } catch (RuntimeException e) {
                        model.addAttribute("classs", kelas);
                        model.addAttribute("task", task);
                        model.addAttribute("subtask", subTask);
                        model.addAttribute("error", e.getMessage());
                        return "pages/Subtask/Edit";
                }

                redirectAttributes.addFlashAttribute("success", "Sub tugas berhasil diedit");
                return "redirect:/class/" + classCode + "/tasks/" + taskId + "#subtasks";
        }

}