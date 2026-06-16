package id.adrianz.ruangkelas.controller;

import id.adrianz.ruangkelas.model.User;
import id.adrianz.ruangkelas.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/tasks/{taskId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping
    public String getComments(@PathVariable Long taskId, Model model) {
        model.addAttribute("comments", commentService.getCommentsByTaskId(taskId));
        model.addAttribute("taskId", taskId);
        return "comment/list";
    }

    @PostMapping
    public String createComment(@PathVariable Long taskId,
                                @RequestParam String content,
                                @AuthenticationPrincipal User user) {
        commentService.createComment(taskId, content, user);
        return "redirect:/tasks/" + taskId + "/comments";
    }

    @PostMapping("/{commentId}/update")
    public String updateComment(@PathVariable Long taskId,
                                @PathVariable Long commentId,
                                @RequestParam String content) {
        commentService.updateComment(commentId, content);
        return "redirect:/tasks/" + taskId + "/comments";
    }

    @PostMapping("/{commentId}/delete")
    public String deleteComment(@PathVariable Long taskId,
                                @PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return "redirect:/tasks/" + taskId + "/comments";
    }
}
