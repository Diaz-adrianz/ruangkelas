package id.adrianz.ruangkelas.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import id.adrianz.ruangkelas.model.UserPrincipal;

@Controller
public class ProfileController {

    @GetMapping("/profil")
    public String profile(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            Model model) {

        model.addAttribute("user", userPrincipal.getUser());

        return "pages/profile";
    }
}