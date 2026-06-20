package id.adrianz.ruangkelas.service;

import java.util.List;
import org.springframework.stereotype.Service;
import id.adrianz.ruangkelas.model.SubTask;
import id.adrianz.ruangkelas.repository.SubTaskRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SubTaskService {

    private final SubTaskRepository subtaskRepository;

    /**
     * Ambil semua subtask
     */
    public List<SubTask> getAllSubtasks() {
        return subtaskRepository.findAll();
    }

    /**
     * Ambil subtask berdasarkan ID
     */
    public SubTask getSubtaskById(Long id) {
        return subtaskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Subtask dengan ID " + id + " tidak ditemukan"));
    }

    /**
     * Simpan subtask baru
     */
    public SubTask createSubtask(SubTask subtask) {
        return subtaskRepository.save(subtask);
    }

    /**
     * Update subtask
     */
    public SubTask updateSubtask(SubTask subtask) {
        return subtaskRepository.save(subtask);
    }

    /**
     * Hapus subtask
     */
    public void deleteSubtask(Long id) {
        subtaskRepository.deleteById(id);
    }

    /**
     * Ambil semua subtask milik task tertentu
     */
    public List<SubTask> getSubtasksByTaskId(Long taskId) {
        return subtaskRepository.findByTaskId(taskId);
    }
}