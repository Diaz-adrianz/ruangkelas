package id.adrianz.ruangkelas.service;

import id.adrianz.ruangkelas.model.Task;
import id.adrianz.ruangkelas.model.TaskSubmission;
import id.adrianz.ruangkelas.repository.TaskSubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

import id.adrianz.ruangkelas.model.SubmissionStatus;
import id.adrianz.ruangkelas.model.UserClass;

import java.util.List;

@Service
public class TaskSubmissionService {

    @Autowired
    private TaskSubmissionRepository submissionRepository;

    @Autowired
    private TaskService taskService;

    public TaskSubmission submitTask(TaskSubmission submission) {
        return submissionRepository.save(submission);
    }

    public List<TaskSubmission> getAllSubmissions() {
        return submissionRepository.findAll();
    }

    public List<TaskSubmission> getSubmissionByTask(Long taskId) {

        Task task = taskService.getTaskById(taskId);

        return submissionRepository.findByTask(task);
    }
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
}