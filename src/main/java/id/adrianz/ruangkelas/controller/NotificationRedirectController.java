package id.adrianz.ruangkelas.controller;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional; // TAMBAHAN
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import id.adrianz.ruangkelas.model.Task;
import id.adrianz.ruangkelas.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // TAMBAHAN

@Slf4j // TAMBAHAN
@Controller
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationRedirectController {

    private final TaskService taskService;

    @GetMapping("/redirect")
    @Transactional // TAMBAHAN PENTING UNTUK MENCEGAH LAZY LOADING ERROR
    public String handleRedirect(
            @RequestParam(required = false) String type, 
            @RequestParam(required = false) Long id) {
        
        log.info(">>> Menerima request redirect - Type: {}, ID: {}", type, id);

        if (type == null || id == null) {
            log.warn(">>> Parameter kosong, kembali ke home");
            return "redirect:/";
        }

        if ("TASK".equalsIgnoreCase(type)) {
            try {
                log.info(">>> Mencari task di database dengan ID: {}", id);
                Task task = taskService.getTaskById(id);
                
                if (task != null && task.getClasse() != null) {
                    String classCode = task.getClasse().getClassCode();
                    String redirectUrl = "/class/" + classCode + "/tasks/" + id;
                    
                    log.info(">>> Sukses! Mengarahkan URL ke: {}", redirectUrl);
                    return "redirect:" + redirectUrl;
                } else {
                    log.warn(">>> Task atau relasi Kelas tidak ditemukan!");
                }
            } catch (Exception e) {
                log.error(">>> Error saat memproses task: {}", e.getMessage(), e);
                return "redirect:/";
            }
        }
        
        return "redirect:/"; 
    }
}