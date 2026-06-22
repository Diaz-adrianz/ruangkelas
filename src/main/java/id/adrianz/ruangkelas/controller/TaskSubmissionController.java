package id.adrianz.ruangkelas.controller;

import id.adrianz.ruangkelas.model.TaskSubmission;
import id.adrianz.ruangkelas.model.SubmissionStatus;
import id.adrianz.ruangkelas.service.TaskSubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/submissions")
public class TaskSubmissionController {

    @Autowired
    private TaskSubmissionService submissionService;

    @PostMapping("/submit")
    public ResponseEntity<TaskSubmission> submit(
            @RequestParam Long taskId,
            @RequestParam Long userId,
            @RequestParam String note
    ) {
        return ResponseEntity.ok(submissionService.submitTask(taskId, userId, note));
    }

    @PutMapping("/{id}/note")
    public ResponseEntity<TaskSubmission> updateNote(
            @PathVariable Long id,
            @RequestParam String note
    ) {
        return ResponseEntity.ok(submissionService.updateNote(id, note));
    }

    @PutMapping("/{id}/mark-late")
    public ResponseEntity<TaskSubmission> markLate(@PathVariable Long id) {
        return ResponseEntity.ok(submissionService.markLate(id));
    }

    @DeleteMapping("/{id}/retract")
    public ResponseEntity<Boolean> retract(@PathVariable Long id) {
        return ResponseEntity.ok(submissionService.retractSubmission(id));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Boolean> transitionStatus(
            @PathVariable Long id,
            @RequestParam SubmissionStatus status
    ) {
        return ResponseEntity.ok(submissionService.transitionStatus(id, status));
    }

    @GetMapping
    public ResponseEntity<List<TaskSubmission>> getAll() {
        return ResponseEntity.ok(submissionService.getAllSubmissions());
    }
}