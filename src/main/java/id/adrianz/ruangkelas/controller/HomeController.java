package id.adrianz.ruangkelas.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import id.adrianz.ruangkelas.model.Class;
import id.adrianz.ruangkelas.model.UserClass;
import id.adrianz.ruangkelas.model.UserPrincipal;
import id.adrianz.ruangkelas.service.ClassService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ClassService classService;

    @GetMapping
    public String index(Model model, @AuthenticationPrincipal UserPrincipal principal) {
        List<Class> classes = classService.getAllForUser(principal.getUser().getId());
        List<UserClass> rejections = classService.getRejectedForUser(principal.getUser().getId());

        model.addAttribute("classes", classes);
        model.addAttribute("rejections", rejections);

        return "pages/Home";
    }
}