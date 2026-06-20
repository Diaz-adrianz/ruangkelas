package id.adrianz.ruangkelas.controller;

import id.adrianz.ruangkelas.model.TaskSubmission;
import id.adrianz.ruangkelas.service.TaskSubmissionService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@RestController
@RequestMapping("/api/submissions")
public class TaskSubmissionController {

    @Autowired 
    private TaskSubmissionService submissionService;

    @PostMapping
    public TaskSubmission submitTask(@RequestBody TaskSubmission submission) {
        return submissionService.submitTask(submission);
    }

    @GetMapping 
    public List<TaskSubmission> getAllSubmissions(){
        return submissionService.getAllSubmissions();
    }
}