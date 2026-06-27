package id.adrianz.ruangkelas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentUpdateDto {

    @NotBlank(message = "Komentar tidak boleh kosong")
    @Size(min = 1, max = 1000, message = "Komentar minimal 1 karakter dan maksimal 1000 karakter")
    private String content;
}