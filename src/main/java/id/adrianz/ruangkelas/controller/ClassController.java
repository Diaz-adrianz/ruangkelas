package id.adrianz.ruangkelas.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import id.adrianz.ruangkelas.dto.CreateClassDto;
import id.adrianz.ruangkelas.model.Class;
import id.adrianz.ruangkelas.model.Course;
import id.adrianz.ruangkelas.model.Task;
import id.adrianz.ruangkelas.model.UserClass;
import id.adrianz.ruangkelas.model.UserPrincipal;
import id.adrianz.ruangkelas.service.ClassService;
import id.adrianz.ruangkelas.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/class")
@RequiredArgsConstructor
public class ClassController {

    private final ClassService classService;

    // TAMBAHAN UNTUK FITUR TASK
    private final TaskService taskService;

    @GetMapping("/join")
    public String joinForm(Model model, @AuthenticationPrincipal UserPrincipal principal) {
        model.addAttribute("classes", classService.getAllForUser(principal.getUser().getId()));
        return "pages/Class/Join";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {

        Class classs = classService.getById(id);
        model.addAttribute("classs", classs);

        // members
        List<UserClass> members = classService.getMembers(id);
        List<UserClass> pendingMembers = classService.getPendingRequests(id);

        model.addAttribute("members", members);
        model.addAttribute("pendingMembers", pendingMembers);

        // ====================================
        // TAMBAHAN FITUR TASK LIST (#29)
        // ====================================
        List<Task> tasks = taskService.getTasksByClassId(id);
        model.addAttribute("tasks", tasks);

        return "pages/Class/Detail";
    }

    @PostMapping("/{id}/join")
    public String join(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal principal,
            RedirectAttributes redirectAttributes) {
        try {
            classService.join(id, principal.getUser());
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/class/join";
        }
        return "redirect:/class";
    }

    @PostMapping("/member/{userClassId}/approve")
    public String approve(@PathVariable Long userClassId, @RequestParam Long classId) {
        classService.approve(userClassId);
        return "redirect:/class/" + classId;
    }

    @PostMapping("/member/{userClassId}/reject")
    public String reject(@PathVariable Long userClassId, @RequestParam Long classId) {
        classService.reject(userClassId);
        return "redirect:/class/" + classId;
    }

    @PostMapping("/member/{userClassId}/kick")
    public String kick(@PathVariable Long userClassId, @RequestParam Long classId) {
        classService.kick(userClassId);
        return "redirect:/class/" + classId;
    }

    @PostMapping("/member/{userClassId}/promote")
    public String promote(@PathVariable Long userClassId, @RequestParam Long classId) {
        classService.promote(userClassId);
        return "redirect:/class/" + classId;
    }

    @PostMapping("/member/{userClassId}/demote")
    public String demote(@PathVariable Long userClassId, @RequestParam Long classId) {
        classService.demote(userClassId);
        return "redirect:/class/" + classId;
    }

    // ======= CREATE =======
    private void createFormData(Model model) {
        model.addAttribute("courses", classService.getAllCourses());
        model.addAttribute("semesters", Class.Semester.values());
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        createFormData(model);

        CreateClassDto createClassDto = new CreateClassDto();
        model.addAttribute("createClassDto", createClassDto);

        return "pages/Class/Create";
    }

    @PostMapping("/create")
    public String create(
            @Valid @ModelAttribute("createClassDto") CreateClassDto request,
            BindingResult result,
            Model model,
            @AuthenticationPrincipal UserPrincipal principal) {

        if (result.hasErrors()) {
            createFormData(model);
            model.addAttribute("errors", result.getFieldErrors());
            return "pages/Class/create";
        }

        try {
            classService.create(
                    request.getName(),
                    request.getYear(),
                    request.getSemester(),
                    request.getCourseId(),
                    principal.getUser());

        } catch (RuntimeException e) {
            createFormData(model);
            model.addAttribute("error", e.getMessage());
            return "pages/Class/create";
        }

        return "redirect:/class";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {

        Class kelas = classService.getById(id);
        List<Course> courses = classService.getAllCourses();

        model.addAttribute("kelas", kelas);
        model.addAttribute("courses", courses);
        model.addAttribute("semesters", Class.Semester.values());

        return "pages/Class/Edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam String year,
            @RequestParam Class.Semester semester,
            @RequestParam Long courseId,
            @AuthenticationPrincipal UserPrincipal principal,
            RedirectAttributes redirectAttributes) {

        try {

            classService.update(
                    id,
                    name,
                    year,
                    semester,
                    courseId,
                    principal.getUser().getId());

        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/class/edit/" + id;
        }

        return "redirect:/class";
    }

    @PostMapping("/delete/{id}")
    public String delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {

        classService.delete(id, principal.getUser().getId());

        return "redirect:/class";
    }
}