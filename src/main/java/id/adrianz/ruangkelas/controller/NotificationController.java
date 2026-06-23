package id.adrianz.ruangkelas.controller;

import id.adrianz.ruangkelas.model.Notification;
import id.adrianz.ruangkelas.model.User;
import id.adrianz.ruangkelas.service.NotificationService;
import id.adrianz.ruangkelas.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;
    private final id.adrianz.ruangkelas.service.TaskService taskService;

    @GetMapping
    public String getNotificationsPage(Principal principal, Model model) {
        User user = userService.findByUsernameOrEmail(principal.getName());
        List<Notification> notifications = notificationService.getAllNotificationsByUserId(user.getId());
        model.addAttribute("notifications", notifications);
        return "pages/Notification/Index";
    }

    @PostMapping("/{id}/read")
    public String markAsRead(@PathVariable Integer id, Model model) {
        try {
            notificationService.markAsRead(id);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "pages/Notification/Index";
        }
        return "redirect:/notification";
    }

    @PostMapping("/{id}/delete")
    public String deleteNotification(@PathVariable Integer id, Model model) {
        try {
            notificationService.deleteNotification(id);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "pages/Notification/Index";
        }
        return "redirect:/notification";
    }

    @GetMapping("/redirect/{id}")
    public String redirectNotification(@PathVariable Integer id) {
        Notification notif = notificationService.getNotificationById(id);

        if (!notif.getIsRead()) {
            notificationService.markAsRead(id);
        }

        String refType = notif.getReferenceType();
        Number refId = notif.getReferenceId();

        if ("TASK".equals(refType)) {
            try {

                id.adrianz.ruangkelas.model.Task task = taskService.getTaskById(refId.longValue());
                String classCode = task.getClasse().getClassCode();

                return "redirect:/class/" + classCode + "/tasks/" + refId;
            } catch (Exception e) {
                return "redirect:/notification";
            }
        }

        return "redirect:/notification";
    }
}