package id.adrianz.ruangkelas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileDto {

    @NotBlank(message = "Nama lengkap wajib diisi")
    @Size(min = 3, max = 50, message = "Nama lengkap harus terdiri dari 3 - 50 karakter")
    @Pattern(
            regexp = "^[A-Za-zÀ-ÿ][A-Za-zÀ-ÿ' .]{2,49}$",
            message = "Nama lengkap hanya boleh berisi huruf, spasi, titik, dan apostrof"
    )
    private String name;

    @Size(
            min = 10, max = 10, message = "NIM harus terdiri dari 10 digit angka"
    )
    private String nim;
}
