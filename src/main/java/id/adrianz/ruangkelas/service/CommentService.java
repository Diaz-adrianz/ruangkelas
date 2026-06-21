package id.adrianz.ruangkelas.service;

import id.adrianz.ruangkelas.dto.CommentCreateDto;
import id.adrianz.ruangkelas.dto.CommentUpdateDto;
import id.adrianz.ruangkelas.model.Comment;
import id.adrianz.ruangkelas.model.Task;
import id.adrianz.ruangkelas.model.User;
import id.adrianz.ruangkelas.model.UserClass;
import id.adrianz.ruangkelas.repository.CommentRepository;
import id.adrianz.ruangkelas.repository.TaskRepository;
import id.adrianz.ruangkelas.repository.UserClassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserClassRepository userClassRepository;

    public List<Comment> getCommentsByTaskId(Long taskId) {
        return commentRepository.findByTaskIdAndParentIsNull(taskId);
    }

    @Transactional
    public Comment createComment(Long taskId, CommentCreateDto dto, User user) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        UserClass userClass = userClassRepository.findByUserIdAndClasseId(user.getId(), task.getClasse().getId())
                .orElseThrow(() -> new RuntimeException("User is not registered in this class"));

        Comment comment = Comment.builder()
                .task(task)
                .userClass(userClass)
                .content(dto.getContent())
                .build();

        if (dto.getParentId() != null) {
            Comment parent = commentRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent comment not found"));
            comment.setParent(parent);
        }

        return commentRepository.save(comment);
    }

    @Transactional
    public Comment updateComment(Long commentId, CommentUpdateDto dto, User user) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (!comment.getUserClass().getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You are not authorized to edit this comment");
        }

        comment.setContent(dto.getContent());
        return commentRepository.save(comment);
    }

    @Transactional
    public void deleteComment(Long commentId, User user) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        boolean isOwner = comment.getUserClass().getUser().getId().equals(user.getId());
        boolean isAdmin = comment.getUserClass().getRole().toString().equalsIgnoreCase("TEACHER");

        if (!isOwner && !isAdmin) {
            throw new RuntimeException("You are not authorized to delete this comment");
        }

        commentRepository.delete(comment);
    }
}