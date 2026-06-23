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

    // 1. Ambil komentar pake filter Role
    public List<Comment> getComments(Long taskId, User user) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        
        UserClass userClass = userClassRepository.findByUserIdAndClasseId(user.getId(), task.getClasse().getId())
                .orElseThrow(() -> new RuntimeException("Not registered in this class"));

        if (userClass.getRole() == UserClass.Role.ADMIN) {
            return commentRepository.findAllMainComments(taskId);
        } else {
            return commentRepository.findCommentsForStudent(taskId, user.getId());
        }
    }

    @Transactional
    public Comment createComment(Long taskId, CommentCreateDto dto, User user) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        UserClass userClass = userClassRepository.findByUserIdAndClasseId(user.getId(), task.getClasse().getId())
                .orElseThrow(() -> new RuntimeException("Not registered in this class"));

        Comment comment = Comment.builder()
                .task(task)
                .userClass(userClass)
                .content(dto.getContent())
                .build();

        if (dto.getParentId() != null) {
            Comment parent = commentRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent comment not found"));
            
            boolean isAdmin = userClass.getRole() == UserClass.Role.ADMIN;
            boolean isOwnComment = parent.getUserClass().getUser().getId().equals(user.getId());
            
            if (!isAdmin && !isOwnComment) {
                throw new RuntimeException("Only Admin can reply to other user's comments");
            }
            comment.setParent(parent);
        }

        return commentRepository.save(comment);
    }

    @Transactional
    public Comment updateComment(Long commentId, CommentUpdateDto dto, User user) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (!comment.getUserClass().getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized to edit this comment");
        }

        comment.setContent(dto.getContent());
        return commentRepository.save(comment);
    }

    @Transactional
    public void deleteComment(Long commentId, User user) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        UserClass currentUserClass = userClassRepository.findByUserIdAndClasseId(user.getId(), comment.getTask().getClasse().getId())
                .orElseThrow(() -> new RuntimeException("Not registered in this class"));

        boolean isOwner = comment.getUserClass().getUser().getId().equals(user.getId());
        boolean isAdmin = currentUserClass.getRole() == UserClass.Role.ADMIN;

        if (!isOwner && !isAdmin) {
            throw new RuntimeException("Unauthorized to delete this comment");
        }

        commentRepository.delete(comment);
    }
}