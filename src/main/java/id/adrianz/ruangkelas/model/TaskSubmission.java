package id.adrianz.ruangkelas.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity 
@Table(name = "task_submissions")
public class TaskSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @Column(nullable = false)
    private String studentName;

    @Column(nullable = false)
    private String fileUrl;

    private LocalDateTime submittedAt;

    @PrePersist
    protected void onCreate(){
        submittedAt = LocalDateTime.now();
     }
}
