package id.adrianz.ruangkelas.controller;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import id.adrianz.ruangkelas.dto.ForgotPasswordDto;
import id.adrianz.ruangkelas.dto.RegisterDto;
import id.adrianz.ruangkelas.dto.ResetPasswordDto;
import id.adrianz.ruangkelas.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@Controller
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private final UserService userService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    @GetMapping("/login")
    public String login() {
        return "pages/Login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("registerDto", new RegisterDto());
        return "pages/Register";
    }

    @PostMapping("/register")
    public String registerSubmit(@Valid @ModelAttribute("registerDto") RegisterDto request,
            BindingResult result,
            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("errors", result.getFieldErrors());
            return "pages/Register";
        }

        try {
            userService.register(request);
            model.addAttribute("registerDto", new RegisterDto());
            model.addAttribute("success", "Pendaftaran akun berhasil. Cek email untuk verifikasi");
            return "pages/Register";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "pages/Register";
        }
    }

    @GetMapping("/verify")
    public String verifyEmail(@RequestParam String token, Model model) {
        try {
            userService.verifyEmail(token);
            model.addAttribute("success", "Akun berhasil diverifikasi. Silakan login.");
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "pages/Verification";
    }

    @GetMapping("/forgot-password")
    public String forgotPassword(Model model) {
        model.addAttribute("forgotPasswordDto", new ForgotPasswordDto());
        return "pages/ForgotPassword";
    }

    @PostMapping("/forgot-password")
    public String forgotPasswordSubmit(@Valid @ModelAttribute("forgotPasswordDto") ForgotPasswordDto request,
            BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("errors", result.getFieldErrors());
            return "pages/ForgotPassword";
        }

        try {
            userService.forgotPassword(request.getEmail());
            model.addAttribute("success", "Kode OTP dikirim ke email");
            model.addAttribute("resetPasswordDto", new ResetPasswordDto());
            return "pages/ResetPassword";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "pages/ForgotPassword";
        }
    }

    @PostMapping("/reset-password")
    public String resetPasswordSubmit(@Valid @ModelAttribute("resetPasswordDto") ResetPasswordDto request,
            BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("errors", result.getFieldErrors());
            return "pages/ResetPassword";
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            model.addAttribute("error", "Password tidak cocok");
            return "pages/ResetPassword";
        }

        try {
            userService.resetPassword(request.getOtp(), request.getPassword());
            redirectAttributes.addFlashAttribute("success", "Reset password berhasil. Silakan login");
            return "redirect:/auth/login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "pages/ResetPassword";
        }
    }
}
