package id.adrianz.ruangkelas.dto;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateScheduleDto {

    @NotNull(message = "Jam mulai wajib diisi")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime startTime;

    @NotNull(message = "Jam selesai wajib diisi")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime endTime;

    @NotBlank(message = "Lokasi wajib diisi")
    @Size(max = 150, message = "Lokasi maksimal 150 karakter")
    private String place;
}