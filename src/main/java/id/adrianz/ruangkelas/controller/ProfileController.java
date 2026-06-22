package id.adrianz.ruangkelas.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import id.adrianz.ruangkelas.dto.UpdateProfileDto;
import id.adrianz.ruangkelas.model.User;
import id.adrianz.ruangkelas.model.UserPrincipal;
import id.adrianz.ruangkelas.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;

    @GetMapping("/profile")
    public String profile(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            Model model) {

        model.addAttribute("user", userPrincipal.getUser());

        return "pages/profile";
    }

    @GetMapping("/profile/edit")
    public String editProfile(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            Model model) {

        UpdateProfileDto dto = new UpdateProfileDto();
        dto.setName(userPrincipal.getUser().getName());
        dto.setNim(userPrincipal.getUser().getNim());

        model.addAttribute("updateProfileDto", dto);

        return "pages/EditProfile";
    }

    @PostMapping("/profile/edit")
    public String registerSubmit(
            @Valid @ModelAttribute("updateProfileDto") UpdateProfileDto request,
            BindingResult result,
            Model model,
            @RequestParam(required = false) MultipartFile photo,
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("errors", result.getFieldErrors());
            return "pages/EditProfile";
        }

        User user = userPrincipal.getUser();

        user.setName(request.getName());
        user.setNim(request.getNim());

        try {

            if (photo != null && !photo.isEmpty()) {

                String uploadDir = "uploads/profile/";

                Files.createDirectories(Paths.get(uploadDir));

                String fileName = UUID.randomUUID() + "_" + photo.getOriginalFilename();

                Path path = Paths.get(uploadDir, fileName);

                Files.copy(
                        photo.getInputStream(),
                        path,
                        StandardCopyOption.REPLACE_EXISTING);

                user.setProfilePicture(
                        "/uploads/profile/" + fileName);
            }

            userService.save(user);

            redirectAttributes.addFlashAttribute("success", "Profil berhasil di edit");
            return "redirect:/profile";

        } catch (IOException e) {

            model.addAttribute(
                    "error",
                    "Gagal mengedit profil");

            return "pages/EditProfile";

        } catch (IllegalArgumentException e) {

            model.addAttribute("error", e.getMessage());

            return "pages/EditProfile";
        }
    }

    @PostMapping("/profile/delete-photo")
    public String deletePhoto(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            Model model,
            RedirectAttributes redirectAttributes) {

        User user = userPrincipal.getUser();

        try {

            if (user.getProfilePicture() != null) {

                String filePath = user.getProfilePicture()
                        .replace("/uploads/", "uploads/");

                Path path = Paths.get(filePath);

                Files.deleteIfExists(path);
            }

            user.setProfilePicture(null);

            userService.save(user);

            redirectAttributes.addFlashAttribute("success", "Foto profil berhasil dihapus");
        } catch (IOException e) {
            model.addAttribute("error", e.getMessage());
            return "pages/EditProfile";
        }

        return "redirect:/profile";
    }
}