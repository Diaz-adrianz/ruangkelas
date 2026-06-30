package id.adrianz.ruangkelas.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import id.adrianz.ruangkelas.model.SubmissionStatus;
import id.adrianz.ruangkelas.model.Task;
import id.adrianz.ruangkelas.model.TaskSubmission;
import id.adrianz.ruangkelas.model.UserClass;
import id.adrianz.ruangkelas.repository.TaskSubmissionRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaskSubmissionService {

    private final TaskSubmissionRepository submissionRepository;

    public TaskSubmission submitTask(Task task, UserClass userClass) {

        if (submissionRepository.findByTaskAndUserClass(task, userClass).isPresent()) {
            throw new RuntimeException("Kamu sudah submit tugas ini.");
        }

        SubmissionStatus status;

        if (LocalDateTime.now().isAfter(task.getDeadline())) {
            status = SubmissionStatus.LATE;
        } else {
            status = SubmissionStatus.ON_TIME;
        }

        TaskSubmission submission = TaskSubmission.builder()
                .task(task)
                .userClass(userClass)
                .status(status)
                .submittedAt(LocalDateTime.now())
                .build();

        return submissionRepository.save(submission);
    }

    public List<TaskSubmission> getAllSubmissions() {
        return submissionRepository.findAll();
    }

    public List<TaskSubmission> getSubmissionByTask(Long taskId) {
        return submissionRepository.findByTask(
                Task.builder().id(taskId).build()
        );
    }
}