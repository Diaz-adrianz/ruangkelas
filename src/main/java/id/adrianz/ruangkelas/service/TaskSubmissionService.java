package id.adrianz.ruangkelas.service;

import id.adrianz.ruangkelas.model.Task;
import id.adrianz.ruangkelas.model.TaskSubmission;
import id.adrianz.ruangkelas.repository.TaskSubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TaskSubmissionService {

    @Autowired
    private TaskSubmissionRepository submissionRepository;

    @Autowired 
    private TaskService taskService;

    public TaskSubmission submitTask(TaskSubmission submission, Long taskId){
        Task task = taskService.getTaskById(taskId);

        submission.setTask(task);

        return submissionRepository.save(submission);
    }

    public List<TaskSubmission> getAllSubmissions(){
        return submissionRepository.findAll();
    }
    
}
