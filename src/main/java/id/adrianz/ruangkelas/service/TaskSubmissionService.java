package id.adrianz.ruangkelas.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import id.adrianz.ruangkelas.dto.TaskSubmissionView;
import id.adrianz.ruangkelas.model.SubmissionStatus;
import id.adrianz.ruangkelas.model.Task;
import id.adrianz.ruangkelas.model.TaskSubmission;
import id.adrianz.ruangkelas.model.UserClass;
import id.adrianz.ruangkelas.repository.TaskRepository;
import id.adrianz.ruangkelas.repository.TaskSubmissionRepository;
import id.adrianz.ruangkelas.repository.UserClassRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaskSubmissionService {

    private final TaskSubmissionRepository submissionRepository;
    private final TaskRepository taskRepository; // Tambahan untuk mencari Task
    private final UserClassRepository userClassRepository; // Tambahan untuk mencari UserClass

    // Parameter diubah menjadi (Long taskId, Long userId) agar sesuai dengan
    // Controller
    public TaskSubmission submitTask(Long taskId, Long userId) {

        // 1. Cari Task berdasarkan ID
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Tugas tidak ditemukan"));

        // 2. Cari UserClass berdasarkan userId dan ID kelas dari tugas tersebut
        UserClass userClass = userClassRepository.findByUserIdAndClasseId(userId, task.getClasse().getId())
                .orElseThrow(() -> new RuntimeException("Kamu bukan anggota kelas ini"));

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

    // --- TAMBAHAN: Fungsi untuk membatalkan submission yang dipanggil Controller
    // ---
    public void retractSubmission(Long id) {
        if (!submissionRepository.existsById(id)) {
            throw new RuntimeException("Submission tidak ditemukan.");
        }
        submissionRepository.deleteById(id);
    }

    public List<TaskSubmission> getAllSubmissions() {

        return submissionRepository.findAll();
    }

    public TaskSubmission getSubmissionById(Long id) {

        return submissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Submission tidak ditemukan."));
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
                            .orElseThrow(() -> new RuntimeException("UserClass tidak ditemukan."));

                    return new TaskSubmissionView(
                            userClass.getUser().getName(),
                            submission.getStatus(),
                            submission.getSubmittedAt());

                })
                .collect(Collectors.toList());
    }

}