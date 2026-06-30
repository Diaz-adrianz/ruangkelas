package id.adrianz.ruangkelas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordDto {
    @NotBlank(message = "Kode OTP wajib diisi")
    @Size(min = 6, max = 6, message = "Kode OTP harus terdiri dari 6 karakter")
    private String otp;

    @NotBlank(message = "Password wajib diisi")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,16}$", message = "Password harus terdiri dari 8 - 16 karakter serta mengandung minimal 1 huruf besar, 1 huruf kecil, dan 1 angka")
    private String password;

    @NotBlank(message = "Konfirmasi password wajib diisi")
    private String confirmPassword;
}
