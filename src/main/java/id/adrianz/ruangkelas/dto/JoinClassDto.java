package id.adrianz.ruangkelas.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class JoinClassDto {

    @NotBlank(message = "Kode kelas wajib diisi")
    private String classCode;
}