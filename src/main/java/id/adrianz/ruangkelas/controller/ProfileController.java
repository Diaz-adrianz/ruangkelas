package id.adrianz.ruangkelas.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import id.adrianz.ruangkelas.model.UserPrincipal;
import id.adrianz.ruangkelas.service.UserService;
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

        model.addAttribute("user", userPrincipal.getUser());

        return "pages/EditProfile";
    }

    @PostMapping("/profil/edit")
    public String updateProfile(
            @RequestParam String name,
            @RequestParam String nim,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        var user = userPrincipal.getUser();

        user.setName(name);
        user.setNim(nim);

        userService.save(user);

        return "redirect:/profil";
    }
}