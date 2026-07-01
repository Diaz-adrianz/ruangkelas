package id.adrianz.ruangkelas.dto;

import java.time.LocalDateTime;

import id.adrianz.ruangkelas.model.SubmissionStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TaskSubmissionView {

    private Long userId;
    private String name;
    private SubmissionStatus status;
    private LocalDateTime submittedAt;

}