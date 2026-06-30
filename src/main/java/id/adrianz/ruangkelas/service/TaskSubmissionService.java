package id.adrianz.ruangkelas.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

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

    // Parameter diubah menjadi (Long taskId, Long userId) agar sesuai dengan Controller
    public TaskSubmission submitTask(Long taskId, Long userId) {

        // 1. Cari Task berdasarkan ID
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Tugas tidak ditemukan"));

        // 2. Cari UserClass berdasarkan userId dan ID kelas dari tugas tersebut
        UserClass userClass = userClassRepository.findByUserIdAndClasseId(userId, task.getClasse().getId())
                .orElseThrow(() -> new RuntimeException("Kamu bukan anggota kelas ini"));

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

    // --- TAMBAHAN: Fungsi untuk membatalkan submission yang dipanggil Controller ---
    public void retractSubmission(Long id) {
        if (!submissionRepository.existsById(id)) {
            throw new RuntimeException("Submission tidak ditemukan.");
        }
        submissionRepository.deleteById(id);
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