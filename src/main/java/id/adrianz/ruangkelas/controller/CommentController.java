package id.adrianz.ruangkelas.controller;

import id.adrianz.ruangkelas.dto.CommentCreateDto;
import id.adrianz.ruangkelas.dto.CommentUpdateDto;
import id.adrianz.ruangkelas.model.User;
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

    @GetMapping
    public String getComments(@PathVariable String classCode, @PathVariable Long taskId, Model model) {
        model.addAttribute("comments", commentService.getCommentsByTaskId(taskId));
        model.addAttribute("taskId", taskId);
        model.addAttribute("classCode", classCode); 
        
        model.addAttribute("commentCreateDto", new CommentCreateDto());
        model.addAttribute("commentUpdateDto", new CommentUpdateDto());
        return "task/comment-list";
    }

    @PostMapping
    public String createComment(@PathVariable String classCode,
                                @PathVariable Long taskId,
                                @Valid @ModelAttribute("commentCreateDto") CommentCreateDto dto,
                                BindingResult bindingResult,
                                @AuthenticationPrincipal User user,
                                Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("comments", commentService.getCommentsByTaskId(taskId));
            model.addAttribute("taskId", taskId);
            model.addAttribute("classCode", classCode);
            model.addAttribute("commentUpdateDto", new CommentUpdateDto());
            return "task/comment-list";
        }
        
        commentService.createComment(taskId, dto, user);
        return "redirect:/class/" + classCode + "/tasks/" + taskId + "/comments";
    }

    @PostMapping("/{commentId}/update")
    public String updateComment(@PathVariable String classCode,
                                @PathVariable Long taskId,
                                @PathVariable Long commentId,
                                @Valid @ModelAttribute("commentUpdateDto") CommentUpdateDto dto,
                                BindingResult bindingResult,
                                @AuthenticationPrincipal User user,
                                Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("comments", commentService.getCommentsByTaskId(taskId));
            model.addAttribute("taskId", taskId);
            model.addAttribute("classCode", classCode);
            model.addAttribute("commentCreateDto", new CommentCreateDto());
            return "task/comment-list";
        }

        commentService.updateComment(commentId, dto, user);
        return "redirect:/class/" + classCode + "/tasks/" + taskId + "/comments";
    }

    @PostMapping("/{commentId}/delete")
    public String deleteComment(@PathVariable String classCode,
                                @PathVariable Long taskId,
                                @PathVariable Long commentId,
                                @AuthenticationPrincipal User user) {
        commentService.deleteComment(commentId, user);
        return "redirect:/class/" + classCode + "/tasks/" + taskId + "/comments";
    }
}