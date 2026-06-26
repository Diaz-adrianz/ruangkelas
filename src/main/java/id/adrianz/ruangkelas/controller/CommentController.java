package id.adrianz.ruangkelas.controller;

import id.adrianz.ruangkelas.dto.CommentCreateDto;
import id.adrianz.ruangkelas.model.UserPrincipal;
import id.adrianz.ruangkelas.model.Class;
import id.adrianz.ruangkelas.model.Task;
import id.adrianz.ruangkelas.service.ClassService;
import id.adrianz.ruangkelas.service.CommentService;
import id.adrianz.ruangkelas.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/class/{classCode}/tasks/{taskId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final ClassService classService;
    private final TaskService taskService;

    @GetMapping("/create")
    public String showCreateForm(@PathVariable String classCode, 
                                 @PathVariable Long taskId, 
                                 Model model) {
        Class classs = classService.getByCode(classCode);
        Task task = taskService.getTaskById(taskId);

        model.addAttribute("classs", classs);
        model.addAttribute("task", task);
        model.addAttribute("commentCreateDto", new CommentCreateDto());
        return "pages/Comment/Create";
    }

    @PostMapping
    public String createComment(@PathVariable String classCode,
                                @PathVariable Long taskId,
                                @Valid @ModelAttribute("commentCreateDto") CommentCreateDto dto,
                                BindingResult bindingResult,
                                @AuthenticationPrincipal UserPrincipal principal,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        if (bindingResult.hasErrors()) {
            Class classs = classService.getByCode(classCode);
            Task task = taskService.getTaskById(taskId);

            model.addAttribute("classs", classs);
            model.addAttribute("task", task);
            return "pages/Comment/Create";
        }

        try {
            commentService.createComment(taskId, dto, principal.getUser());    
        } catch (RuntimeException e) {
            Class classs = classService.getByCode(classCode);
            Task task = taskService.getTaskById(taskId);

            model.addAttribute("classs", classs);
            model.addAttribute("task", task);
            return "pages/Comment/Create";
        }

        redirectAttributes.addFlashAttribute("success", "Komentar berhasil ditambahkan");
        return "redirect:/class/" + classCode + "/tasks/" + taskId + "#comments";
    }

    @PostMapping("/{commentId}/reply")
    public String replyComment(@PathVariable String classCode,
                               @PathVariable Long taskId,
                               @PathVariable Long commentId,
                               @Valid @ModelAttribute("commentCreateDto") CommentCreateDto dto,
                               BindingResult bindingResult,
                               @AuthenticationPrincipal UserPrincipal principal,
                               Model model) {
        if (bindingResult.hasErrors()) {
            return "redirect:/class/" + classCode + "/tasks/" + taskId;
        }

        dto.setParentId(commentId);
        commentService.createComment(taskId, dto, principal.getUser());
        return "redirect:/class/" + classCode + "/tasks/" + taskId;
    }

    @PostMapping("/{commentId}/delete")
    public String deleteComment(@PathVariable String classCode,
                                @PathVariable Long taskId,
                                @PathVariable Long commentId,
                                RedirectAttributes redirectAttributes,
                                @AuthenticationPrincipal UserPrincipal principal) {
        try {
            commentService.deleteComment(commentId, principal.getUser());
            redirectAttributes.addFlashAttribute("success", "Komentar berhasil dihapus");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/class/" + classCode + "/tasks/" + taskId + "#comments";
    }
}