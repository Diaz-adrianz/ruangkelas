package id.adrianz.ruangkelas.dto;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateTaskDto {

    @NotBlank(message = "Judul wajib diisi")
    @Size(min = 3, max = 100, message = "Judul harus terdiri dari 3 - 100 karakter")
    private String title;

    @NotBlank(message = "Deskripsi wajib diisi")
    @Size(min = 3, max = 500, message = "Deskripsi harus terdiri dari 3 - 500 karakter")
    private String description;

    @NotNull(message = "Batas waktu wajib diisi")
    @Future(message = "Batas waktu harus setelah waktu saat ini")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime deadline;
}
