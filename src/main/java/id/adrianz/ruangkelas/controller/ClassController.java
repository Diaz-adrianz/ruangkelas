package id.adrianz.ruangkelas.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import id.adrianz.ruangkelas.model.Class;
import id.adrianz.ruangkelas.model.Course;
import id.adrianz.ruangkelas.model.UserPrincipal;
import id.adrianz.ruangkelas.service.ClassService;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

@Controller
@RequestMapping("/class")
@RequiredArgsConstructor
public class ClassController {

    private final ClassService classService;

    @GetMapping
    public String index(Model model) {
        List<Class> classes = classService.getAll();
        model.addAttribute("classes", classes);
        return "Class/Index";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Class kelas = classService.getById(id);
        model.addAttribute("kelas", kelas);
        return "Class/Detail";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        List<Course> courses = classService.getAllCourses();
        model.addAttribute("courses", courses);
        model.addAttribute("semesters", Class.Semester.values());
        return "/Class/Create";
    }

    @PostMapping("/create")
    public String create(
            @RequestParam String name,
            @RequestParam String year,
            @RequestParam Class.Semester semester,
            @RequestParam Long courseId,
            @AuthenticationPrincipal UserPrincipal principal) {

        classService.create(name, year, semester, courseId, principal.getUser());
        return "redirect:/class";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        Class classe = classService.getById(id);
        List<Course> courses = classService.getAllCourses();
        model.addAttribute("kelas", classe);
        model.addAttribute("courses", courses);
        model.addAttribute("semesters", Class.Semester.values());
        return "Class/Edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam String year,
            @RequestParam Class.Semester semester,
            @RequestParam Long courseId) {

        classService.update(id, name, year, semester, courseId);
        return "redirect:/class";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        classService.delete(id);
        return "redirect:/class";
    }
}