package id.adrianz.ruangkelas.controller;

import id.adrianz.ruangkelas.dto.CommentCreateDto;
import id.adrianz.ruangkelas.dto.CommentUpdateDto;
import id.adrianz.ruangkelas.model.User;
import id.adrianz.ruangkelas.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
        return "task/comment-list";
    }

    @PostMapping
    public String createComment(@PathVariable Long taskId,
                                @ModelAttribute CommentCreateDto dto,
                                @AuthenticationPrincipal User user) {
        commentService.createComment(taskId, dto, user);
        return "redirect:/tasks/" + taskId + "/comments";
    }

    @PostMapping("/{commentId}/update")
    public String updateComment(@PathVariable Long taskId,
                                @PathVariable Long commentId,
                                @ModelAttribute CommentUpdateDto dto) {
        commentService.updateComment(commentId, dto);
        return "redirect:/tasks/" + taskId + "/comments";
    }

    @PostMapping("/{commentId}/delete")
    public String deleteComment(@PathVariable Long taskId,
                                @PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return "redirect:/tasks/" + taskId + "/comments";
    }
}