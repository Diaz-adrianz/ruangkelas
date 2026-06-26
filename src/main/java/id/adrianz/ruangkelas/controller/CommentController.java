package id.adrianz.ruangkelas.controller;

import id.adrianz.ruangkelas.dto.CommentCreateDto;
import id.adrianz.ruangkelas.model.UserPrincipal;
import id.adrianz.ruangkelas.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/class/{classCode}/tasks/{taskId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/create")
    public String showCreateForm(@PathVariable String classCode, 
                                 @PathVariable Long taskId, 
                                 Model model) {
        model.addAttribute("taskId", taskId);
        model.addAttribute("classCode", classCode);
        model.addAttribute("commentCreateDto", new CommentCreateDto());
        return "pages/Task/CommentCreate";
    }

    @PostMapping
    public String createComment(@PathVariable String classCode,
                                @PathVariable Long taskId,
                                @Valid @ModelAttribute("commentCreateDto") CommentCreateDto dto,
                                BindingResult bindingResult,
                                @AuthenticationPrincipal UserPrincipal principal,
                                Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("taskId", taskId);
            model.addAttribute("classCode", classCode);
            return "pages/Task/CommentCreate";
        }
        
        commentService.createComment(taskId, dto, principal.getUser());
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
                                @AuthenticationPrincipal UserPrincipal principal) {
        commentService.deleteComment(commentId, principal.getUser());
        return "redirect:/class/" + classCode + "/tasks/" + taskId + "#comments";
    }
}