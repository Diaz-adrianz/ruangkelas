package id.adrianz.ruangkelas.controller;

import id.adrianz.ruangkelas.model.TaskSubmission;
import id.adrianz.ruangkelas.service.TaskSubmissionService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import java.util.List;

@RestController
@RequestMapping("/api/submissions")
public class TaskSubmissionController {

    @Autowired
    private TaskSubmissionService submissionService;

    @PostMapping("/submit/{taskId}")
    public ResponseEntity<TaskSubmission> submitTask (
        @RequestBody TaskSubmission submission,
        @PathVariable Long taskId
    ){
        TaskSubmission result = submissionService.submitTask(submission,taskId);
        return ResponseEntity.ok(result);
    }

    @GetMapping 
    public ResponseEntity<List<TaskSubmission>> getAllSubmissions(){
        List<TaskSubmission> submissions = submissionService.getAllSubmissions();
        return ResponseEntity.ok(submissions);
    }
}