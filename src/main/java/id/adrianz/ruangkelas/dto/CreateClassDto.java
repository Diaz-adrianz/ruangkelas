package id.adrianz.ruangkelas.dto;

import id.adrianz.ruangkelas.model.Class;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateClassDto {

    @NotNull(message = "Mata kuliah wajib dipilih")
    private Long courseId;

    @NotBlank(message = "Nama wajib diisi")
    @Size(min = 3, max = 100, message = "Nama harus terdiri dari 3 - 100 karakter")
    private String name;

    @NotBlank(message = "Tahun ajaran wajib diisi")
    @Pattern(
            regexp = "^\\d{4}/\\d{4}$",
            message = "Format tahun ajaran harus YYYY/YYYY"
    )
    private String year;

    @NotNull(message = "Semester wajib dipilih")
    private Class.Semester semester;

    @Size(max = 100, message = "Nama dosen maksimal 100 karakter")
    private String lecturerName;
}