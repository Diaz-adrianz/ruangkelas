package id.adrianz.ruangkelas.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskDto {

    private String title;

    private String description;

    private String status;

    private LocalDateTime deadline;

    private Long classId;
}