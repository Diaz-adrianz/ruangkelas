package id.adrianz.ruangkelas.service;

import id.adrianz.ruangkelas.model.Task;
import id.adrianz.ruangkelas.model.TaskSubmission;
import id.adrianz.ruangkelas.model.SubmissionStatus;
import id.adrianz.ruangkelas.repository.TaskSubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskSubmissionService {

    @Autowired
    private TaskSubmissionRepository submissionRepository;

    @Autowired
    private TaskService taskService; 

    public TaskSubmission submitTask(Long taskId, Long userId, String note) {
        Task task = taskService.getTaskById(taskId);
        
        TaskSubmission submission = new TaskSubmission();
        submission.setTask(task);
        submission.setUserId(userId);
        submission.setNote(note);
        submission.setSubmittedAt(LocalDateTime.now());

        if (LocalDateTime.now().isAfter(task.getDeadline())) {
            submission.setStatus(SubmissionStatus.LATE);
        } else {
            submission.setStatus(SubmissionStatus.SUBMITTED);
        }

        return submissionRepository.save(submission);
    }

    public TaskSubmission updateNote(Long id, String newNote) {
        TaskSubmission submission = submissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Submission tidak ditemukan"));
        
        submission.setNote(newNote);
        submission.setUpdatedAt(LocalDateTime.now());
        return submissionRepository.save(submission);
    }

    public TaskSubmission markLate(Long id) {
        TaskSubmission submission = submissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Submission tidak ditemukan"));
        
        submission.setStatus(SubmissionStatus.LATE);
        submission.setUpdatedAt(LocalDateTime.now());
        return submissionRepository.save(submission);
    }

    public Boolean retractSubmission(Long id) {
        if (submissionRepository.existsById(id)) {
            submissionRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Boolean transitionStatus(Long id, SubmissionStatus newStatus) {
        TaskSubmission submission = submissionRepository.findById(id).orElse(null);
        if (submission != null) {
            submission.setStatus(newStatus);
            submission.setUpdatedAt(LocalDateTime.now());
            submissionRepository.save(submission);
            return true;
        }
        return false;
    }

    public List<TaskSubmission> getAllSubmissions() {
        return submissionRepository.findAll();
    }
}