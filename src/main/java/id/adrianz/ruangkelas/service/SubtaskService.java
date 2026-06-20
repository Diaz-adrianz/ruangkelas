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

    public List<SubTask> getAllSubtasks() {
        return subtaskRepository.findAll();
    }

    public SubTask getSubtaskById(Long id) {
        return subtaskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Subtask dengan ID " + id + " tidak ditemukan"));
    }

    public SubTask createSubtask(SubTask subtask) {
        return subtaskRepository.save(subtask);
    }

    public SubTask updateSubtask(SubTask subtask) {
        return subtaskRepository.save(subtask);
    }

    public void deleteSubtask(Long id) {
        subtaskRepository.deleteById(id);
    }

    public List<SubTask> getSubtasksByTaskId(Long taskId) {
        return subtaskRepository.findByTaskId(taskId);
    }
}