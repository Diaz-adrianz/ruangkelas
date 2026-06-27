package id.adrianz.ruangkelas.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "schedules")
@Data
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    private Class kelas;

    @Column(name = "datetime")
    private LocalDateTime dateTime;

    @Column(name = "end_datetime")
    private LocalDateTime endDateTime;

    @Column(name = "place", length = 100)
    private String place;

    @Column(name = "schedule_date", insertable = false, updatable = false)
    private LocalDate legacyDate;

    @Column(name = "start_time", insertable = false, updatable = false)
    private LocalTime legacyStartTime;

    @Column(name = "room", insertable = false, updatable = false)
    private String legacyRoom;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public LocalDateTime getEffectiveDateTime() {
        if (dateTime != null) {
            return dateTime;
        }

        if (legacyDate != null && legacyStartTime != null) {
            return LocalDateTime.of(legacyDate, legacyStartTime);
        }

        return null;
    }

    public String getEffectivePlace() {
        if (place != null && !place.isBlank()) {
            return place;
        }

        if (legacyRoom != null && !legacyRoom.isBlank()) {
            return legacyRoom;
        }

        return "-";
    }
}