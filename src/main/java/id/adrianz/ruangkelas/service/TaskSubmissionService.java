package id.adrianz.ruangkelas.service;

import id.adrianz.ruangkelas.model.TaskSubmission;
import id.adrianz.ruangkelas.repository.TaskSubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TaskSubmissionService {

    @Autowired
    private TaskSubmissionRepository submissionRepository;

    public TaskSubmission submitTask(TaskSubmission submission){
        return submissionRepository.save(submission);
    }

    public List<TaskSubmission> getAllSubmissions(){
        return submissionRepository.findAll();
    }
    
}
