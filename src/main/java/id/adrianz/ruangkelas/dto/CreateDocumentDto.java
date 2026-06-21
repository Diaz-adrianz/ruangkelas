package id.adrianz.ruangkelas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import org.springframework.web.multipart.MultipartFile;

@Data
public class CreateDocumentDto {

    @NotNull(message = "Kelas wajib dipilih")
    private Long classId;

    @NotNull(message = "File wajib diupload")
    private MultipartFile file;

    @NotBlank(message = "Judul wajib diisi")
    @Size(min = 3, max = 50, message = "Judul harus terdiri dari 3 - 50 karakter")
    private String title;
}