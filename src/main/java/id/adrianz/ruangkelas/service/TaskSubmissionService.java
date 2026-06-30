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

        return submissionRepository.findByTaskId(taskId);
    }

}