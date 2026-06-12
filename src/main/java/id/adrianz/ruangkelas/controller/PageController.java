package id.adrianz.ruangkelas.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/login")
    public String login() {
        return "pages/Login";
    }

    @GetMapping("/")
    public String home() {
        return "pages/Home";
    }
}
