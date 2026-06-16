package id.adrianz.ruangkelas.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

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

    @GetMapping("/profil")
    public String profile(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            Model model) {

        model.addAttribute("user", userPrincipal.getUser());

        return "pages/profile";
    }

    @GetMapping("/profil/edit")
    public String editProfile(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            Model model) {

        UpdateProfileDto dto = new UpdateProfileDto();
        dto.setName(userPrincipal.getUser().getName());
        dto.setNim(userPrincipal.getUser().getNim());
        model.addAttribute("updateProfileDto", dto);

        return "pages/EditProfile";
    }

    @PostMapping("/profil/edit")
    public String registerSubmit(@Valid @ModelAttribute("updateProfileDto") UpdateProfileDto request,
            BindingResult result,
            Model model,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (result.hasErrors()) {
            model.addAttribute("errors", result.getFieldErrors());
            return "pages/EditProfile";
        }

        User user = userPrincipal.getUser();
        user.setName(request.getName());
        user.setNim(request.getNim());

        try {
            userService.save(user);
            return "redirect:/profil";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "pages/EditProfile";
        }
    }
}
