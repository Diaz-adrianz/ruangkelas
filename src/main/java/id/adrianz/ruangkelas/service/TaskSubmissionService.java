package id.adrianz.ruangkelas.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import id.adrianz.ruangkelas.model.SubmissionStatus;
import id.adrianz.ruangkelas.model.Task;
import id.adrianz.ruangkelas.model.TaskSubmission;
import id.adrianz.ruangkelas.model.UserClass;
import id.adrianz.ruangkelas.repository.TaskSubmissionRepository;
import id.adrianz.ruangkelas.repository.UserClassRepository;
import lombok.RequiredArgsConstructor;
import java.util.stream.Collectors;
import id.adrianz.ruangkelas.dto.TaskSubmissionView;

@Service
@RequiredArgsConstructor
public class TaskSubmissionService {

    private final TaskSubmissionRepository submissionRepository;
    private final TaskService taskService;
    private final UserClassRepository userClassRepository;

    public TaskSubmission submitTask(Long taskId, Long userId) {

        Task task = taskService.getTaskById(taskId);

        UserClass userClass = userClassRepository
                .findByUserIdAndClasseId(
                        userId,
                        task.getClasse().getId())
                .orElseThrow(() ->
                        new RuntimeException("Kamu bukan anggota kelas."));

        if (submissionRepository
                .findByUserClassIdAndTaskId(
                        userClass.getId(),
                        taskId)
                .isPresent()) {

            throw new RuntimeException("Tugas sudah pernah disubmit.");
        }

        TaskSubmission submission = new TaskSubmission();

        submission.setTaskId(taskId);
        submission.setUserClassId(userClass.getId());
        submission.setSubmittedAt(LocalDateTime.now());

        if (LocalDateTime.now().isAfter(task.getDeadline())) {
            submission.setStatus(SubmissionStatus.LATE);
        } else {
            submission.setStatus(SubmissionStatus.ON_TIME);
        }

        return submissionRepository.save(submission);
    }

    public void retractSubmission(Long id) {

        TaskSubmission submission = getSubmissionById(id);

        submissionRepository.delete(submission);
    }

    public List<TaskSubmission> getAllSubmissions() {

        return submissionRepository.findAll();
    }

    public TaskSubmission getSubmissionById(Long id) {

        return submissionRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Submission tidak ditemukan."));
    }

    public List<TaskSubmission> getSubmissionByTask(Long taskId) {

        return submissionRepository.findByTaskIdOrderBySubmittedAtDesc(taskId);
    }
    

    public List<TaskSubmissionView> getSubmissionViews(Long taskId) {

    return submissionRepository
            .findByTaskIdOrderBySubmittedAtDesc(taskId)
            .stream()
            .map(submission -> {

                UserClass userClass = userClassRepository
                        .findById(submission.getUserClassId())
                        .orElseThrow(() ->
                                new RuntimeException("UserClass tidak ditemukan."));

                return new TaskSubmissionView(
                        userClass.getUser().getName(),
                        submission.getStatus(),
                        submission.getSubmittedAt());

            })
            .collect(Collectors.toList());
}

}