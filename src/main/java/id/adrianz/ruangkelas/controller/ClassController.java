package id.adrianz.ruangkelas.controller;

import java.util.ArrayList;
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
import id.adrianz.ruangkelas.dto.CreateDocumentDto;
import id.adrianz.ruangkelas.dto.JoinClassDto;
import id.adrianz.ruangkelas.dto.UpdateClassDto;
import id.adrianz.ruangkelas.model.Class;
import id.adrianz.ruangkelas.model.Schedule;
import id.adrianz.ruangkelas.model.UserClass;
import id.adrianz.ruangkelas.model.UserPrincipal;
import id.adrianz.ruangkelas.service.ClassService;
import id.adrianz.ruangkelas.service.ScheduleService; // Tambahkan import ini
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/class")
@RequiredArgsConstructor
public class ClassController {

    private final ClassService classService;
    private final ScheduleService scheduleService; // Tambahkan ini

    // ================= INDEX =================

    @GetMapping
    public String index(@AuthenticationPrincipal UserPrincipal principal, Model model) {
        List<Class> classes = classService.getAllForUser(principal.getUser().getId());
        model.addAttribute("classes", classes);
        return "pages/Class/Index";
    }


    // ================= DETAIL =================

    @GetMapping("/{classCode}")
    public String detail(@PathVariable String classCode,
                         @AuthenticationPrincipal UserPrincipal principal,
                         Model model) {

        Class classs = classService.getByCode(classCode);
        List<UserClass> members = classService.getMembers(classs.getId());
        List<UserClass> pending = classService.getPendingRequests(classs.getId());
        boolean isAdmin = classService.isAdmin(classs.getId(), principal.getUser().getId());

        // --- Bagian yang ditambahkan ---
        List<Schedule> schedules = scheduleService.getSchedulesByClassCode(classs.getId());
        model.addAttribute("schedules", schedules != null ? schedules : new ArrayList<Schedule>());
        // -------------------------------

        model.addAttribute("classs", classs);
        model.addAttribute("members", members);
        model.addAttribute("pendingRequests", pending);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("currentUserId", principal.getUser().getId());

        model.addAttribute(
            "documents",
            documentService.getDocumentsByClass(classs.getId())
        );
        List<Task> tasks = taskService.getTasksByClassCode(classCode);
        model.addAttribute("tasks", tasks);


        return "pages/Class/Detail";
    }

    // ================= CREATE, EDIT, DELETE, JOIN, LEAVE, APPROVE, KICK, PROMOTE =================
    // (Method lainnya dibiarkan sama seperti milik Anda sebelumnya)
    
    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("createClassDto", new CreateClassDto());
        model.addAttribute("courses", classService.getAllCourses());
        model.addAttribute("semesters", Class.Semester.values());
        return "pages/Class/Create";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("createClassDto") CreateClassDto request,
                         BindingResult result,
                         Model model,
                         @AuthenticationPrincipal UserPrincipal principal) {

        if (result.hasErrors()) {
            model.addAttribute("courses", classService.getAllCourses());
            model.addAttribute("semesters", Class.Semester.values());
            model.addAttribute("errors", result.getFieldErrors());
            return "pages/Class/Create";
        }

        try {
            classService.create(
                    request.getName(),
                    request.getYear(),
                    request.getSemester(),
                    request.getCourseId(),
                    request.getLecturerName(),
                    principal.getUser()
            );
        } catch (RuntimeException e) {
            model.addAttribute("courses", classService.getAllCourses());
            model.addAttribute("semesters", Class.Semester.values());
            model.addAttribute("error", e.getMessage());
            return "pages/Class/Create";
        }

        return "redirect:/";
    }

    @GetMapping("/edit/{classCode}")
    public String editForm(@PathVariable String classCode, Model model) {
        Class kelas = classService.getByCode(classCode);
        UpdateClassDto dto = new UpdateClassDto();
        dto.setName(kelas.getName());
        dto.setYear(kelas.getYear());
        dto.setSemester(kelas.getSemester());
        dto.setCourseId(kelas.getCourse().getId());
        dto.setLecturerName(kelas.getLecturerName());

        model.addAttribute("classCode", classCode);
        model.addAttribute("updateClassDto", dto);
        model.addAttribute("courses", classService.getAllCourses());
        model.addAttribute("semesters", Class.Semester.values());
        return "pages/Class/Edit";
    }

    @PostMapping("/edit/{classCode}")
    public String edit(@PathVariable String classCode,
                       @Valid @ModelAttribute("updateClassDto") UpdateClassDto request,
                       BindingResult result,
                       Model model,
                       @AuthenticationPrincipal UserPrincipal principal,
                        RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("classCode", classCode);
            model.addAttribute("courses", classService.getAllCourses());
            model.addAttribute("semesters", Class.Semester.values());
            model.addAttribute("errors", result.getFieldErrors());
            return "pages/Class/Edit";
        }

        try {
            classService.update(classCode, request.getName(), request.getYear(), request.getSemester(),
                    request.getCourseId(), request.getLecturerName(), principal.getUser().getId());
        } catch (RuntimeException e) {
            model.addAttribute("classCode", classCode);
            model.addAttribute("courses", classService.getAllCourses());
            model.addAttribute("semesters", Class.Semester.values());
            model.addAttribute("error", e.getMessage());
            return "pages/Class/Edit";
        }

        redirectAttributes.addFlashAttribute("success", "Kelas berhasil diedit");
        return "redirect:/class/" + classCode;
    }

    @PostMapping("/delete/{classCode}")
    public String delete(@PathVariable String classCode, @AuthenticationPrincipal UserPrincipal principal) {
        classService.delete(classCode, principal.getUser().getId());
        return "redirect:/";
    }

    @GetMapping("/{classCode}/jadwal")
public String jadwalDetail(@PathVariable String classCode, Model model) {
    Class classs = classService.getByCode(classCode);
    List<Schedule> schedules = scheduleService.getSchedulesByClassCode(classs.getId());
    
    model.addAttribute("classs", classs);
    model.addAttribute("schedules", schedules);
    
    // Ganti dengan nama file HTML halaman jadwal Anda
    return "schedules/jadwal_list";
}



    @GetMapping("/join")
    public String joinForm(Model model) {
        model.addAttribute("joinClassDto", new JoinClassDto());
        return "pages/Class/Join";
    }

    @PostMapping("/join")
    public String join(@Valid @ModelAttribute("joinClassDto") JoinClassDto request,
                       BindingResult result,
                       Model model,
                       @AuthenticationPrincipal UserPrincipal principal, 
                       RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("errors", result.getFieldErrors());
            return "pages/Class/Join";
        }
        try {
            classService.joinByCode(request.getClassCode(), principal.getUser());
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "pages/Class/Join";
        }
        redirectAttributes.addFlashAttribute("success", "Permintaan bergabung telah dikirim");
        return "redirect:/";
    }

    @PostMapping("/leave/{classCode}")
    public String leaveClass(@PathVariable String classCode, @AuthenticationPrincipal UserPrincipal principal, RedirectAttributes redirectAttributes) {        
        Class kelas = classService.getByCode(classCode);
        classService.leaveClass(kelas.getId(), principal.getUser().getId());
        redirectAttributes.addFlashAttribute("success", "Kamu telah keluar dari kelas \"" + kelas.getName() + "\"");
        return "redirect:/";
    }

    @PostMapping("/member/{userClassId}/approve")
    public String approve(@PathVariable Long userClassId, @RequestParam String classCode, RedirectAttributes redirectAttributes) {
        classService.approve(userClassId);
        redirectAttributes.addFlashAttribute("success", "Permintaan bergabung berhasil diterima");
        return "redirect:/class/" + classCode + "#members";
    }

    @PostMapping("/member/{userClassId}/reject")
    public String reject(@PathVariable Long userClassId, @RequestParam String classCode, RedirectAttributes redirectAttributes) {
        classService.reject(userClassId);
        redirectAttributes.addFlashAttribute("success", "Permintaan bergabung berhasil ditolak");
        return "redirect:/class/" + classCode + "#members";
    }

    @PostMapping("/member/{userClassId}/kick")
    public String kick(@PathVariable Long userClassId, @RequestParam String classCode, RedirectAttributes redirectAttributes) {
        classService.kick(userClassId);
        redirectAttributes.addFlashAttribute("success", "Anggota berhasil dikeluarkan");
        return "redirect:/class/" + classCode + "#members";
    }

    @PostMapping("/member/{userClassId}/promote")
    public String promote(@PathVariable Long userClassId, @RequestParam String classCode, RedirectAttributes redirectAttributes) {
        classService.promote(userClassId);
        redirectAttributes.addFlashAttribute("success", "Admin berhasil ditambahkan");
        return "redirect:/class/" + classCode + "#members";
    }

    @PostMapping("/member/{userClassId}/demote")
    public String demote(@PathVariable Long userClassId, @RequestParam String classCode, RedirectAttributes redirectAttributes) {
        classService.demote(userClassId);
        redirectAttributes.addFlashAttribute("success", "Admin berhasil dihapus");
        return "redirect:/class/" + classCode + "#members";
    }

    // ================= CREATE (Relations) =================
    @GetMapping("/{classCode}/document/upload")
    public String showCreateForm(@PathVariable String classCode, Model model) {
        Class classs = classService.getByCode(classCode);
        model.addAttribute("classs", classs);
        model.addAttribute("createDocumentDto", new CreateDocumentDto());
        return "pages/Document/Create";
    }
}