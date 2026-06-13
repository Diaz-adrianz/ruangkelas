package id.adrianz.ruangkelas.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterDto {

    @NotBlank(message = "Username wajib diisi")
    @Size(min = 3, max = 30, message = "Username harus terdiri dari 3 - 30 karakter")
    @Pattern(
            regexp = "^[A-Za-z][A-Za-z0-9_]{2,29}$",
            message = "Username hanya boleh berisi huruf, angka, atau underscore (_), dan harus diawali huruf"
    )
    private String username;

    @NotBlank(message = "Email wajib diisi")
    @Email(message = "Format email tidak valid")
    private String email;

    @NotBlank(message = "Password wajib diisi")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,16}$",
            message = "Password harus terdiri dari 8 - 16 karakter serta mengandung minimal 1 huruf besar, 1 huruf kecil, dan 1 angka"
    )
    private String password;

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
