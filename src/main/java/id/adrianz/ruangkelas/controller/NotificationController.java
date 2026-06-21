package id.adrianz.ruangkelas.controller;

import id.adrianz.ruangkelas.model.DeviceToken;
import id.adrianz.ruangkelas.model.Notification;
import id.adrianz.ruangkelas.model.User;
import id.adrianz.ruangkelas.repository.DeviceTokenRepository;
import id.adrianz.ruangkelas.repository.UserRepository;
import id.adrianz.ruangkelas.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final UserRepository userRepository;
    private final DeviceTokenRepository deviceTokenRepository; // Wajib ditambahkan
    
    @GetMapping
    public ResponseEntity<List<Notification>> getAllUserNotifications(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }

        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));
        
        List<Notification> notifications = notificationService.getAllNotificationsByUserId(user.getId());
        return ResponseEntity.ok(notifications);
    }

    @PutMapping("/status")
    public ResponseEntity<String> markAllAsRead(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }

        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        notificationService.markAllNotificationsAsRead(user.getId());
        return ResponseEntity.ok("Semua notifikasi berhasil ditandai sebagai sudah dibaca");
    }

    // Endpoint baru untuk menerima token dari Frontend HTML
    @PostMapping("/register-token")
    public ResponseEntity<?> registerDeviceToken(Authentication authentication, @RequestBody Map<String, String> payload) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }

        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        String token = payload.get("token");
        
        // Simpan ke database jika token belum pernah didaftarkan
        if (token != null && !deviceTokenRepository.existsByToken(token)) {
            DeviceToken deviceToken = new DeviceToken();
            deviceToken.setToken(token);
            deviceToken.setUser(user);
            deviceTokenRepository.save(deviceToken);
        }

        return ResponseEntity.ok().build();
    }
}