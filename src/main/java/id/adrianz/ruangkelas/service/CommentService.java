package id.adrianz.ruangkelas.service;

import id.adrianz.ruangkelas.model.Comment;
import id.adrianz.ruangkelas.model.User;
import id.adrianz.ruangkelas.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    public List<Comment> getCommentsByTaskId(Long taskId) {
        return commentRepository.findByTaskId(taskId);
    }

    public Comment createComment(Long taskId, String content, User user) {
        Comment comment = Comment.builder()
                .taskId(taskId)
                .content(content)
                .user(user)
                .build();
        return commentRepository.save(comment);
    }

    public Comment updateComment(Long commentId, String content) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        comment.setContent(content);
        return commentRepository.save(comment);
    }

    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }
}
