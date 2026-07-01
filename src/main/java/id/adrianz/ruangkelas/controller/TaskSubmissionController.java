package id.adrianz.ruangkelas.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import id.adrianz.ruangkelas.model.UserPrincipal;
import id.adrianz.ruangkelas.service.TaskSubmissionService;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/class/{classCode}/tasks/{taskId}/submissions")
@RequiredArgsConstructor
public class TaskSubmissionController {

    private final TaskSubmissionService submissionService;

    @PostMapping("/submit")
    public String submit(
            @PathVariable String classCode,
            @PathVariable Long taskId,
            @AuthenticationPrincipal UserPrincipal principal,
            RedirectAttributes redirectAttributes) {

        try {

            submissionService.submitTask(
                    taskId,
                    principal.getUser().getId());

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Tugas berhasil disubmit.");

        } catch (Exception e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    e.getMessage());

        }

        return "redirect:/class/" + classCode + "/tasks/" + taskId + "#submissions";
    }

    @PostMapping("/{id}/retract")
    public String retract(
            @PathVariable String classCode,
            @PathVariable Long taskId,
            @PathVariable Long id,
            RedirectAttributes redirectAttributes

    ) {

        try {

            submissionService.retractSubmission(id);

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Submission berhasil ditarik.");

        } catch (Exception e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    e.getMessage());

        }

        return "redirect:/class/" + classCode + "/tasks/" + taskId + "#submissions";
    }

}