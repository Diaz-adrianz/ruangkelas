package id.adrianz.ruangkelas.controller;

import id.adrianz.ruangkelas.model.DeviceToken;
import id.adrianz.ruangkelas.model.User;
import id.adrianz.ruangkelas.repository.DeviceTokenRepository;
import id.adrianz.ruangkelas.repository.UserRepository;
import id.adrianz.ruangkelas.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final DeviceTokenRepository deviceTokenRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @PostMapping("/register-token")
    public ResponseEntity<?> registerToken(
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal UserDetails userDetails) {

        String token = body.get("token");

        if (token == null || token.isBlank()) {
            return ResponseEntity.badRequest().body("Token tidak boleh kosong");
        }

        if (!deviceTokenRepository.existsByToken(token)) {
            User user = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

            DeviceToken deviceToken = new DeviceToken();
            deviceToken.setToken(token);
            deviceToken.setUser(user);
            deviceTokenRepository.save(deviceToken);
        }

        return ResponseEntity.ok(Map.of("message", "Token berhasil disimpan"));
    }

    @PostMapping("/send")
    public ResponseEntity<?> sendNotification(@RequestBody Map<String, String> body) {
        String token = body.get("token");
        String title = body.get("title");
        String message = body.get("message");

        notificationService.sendToToken(token, title, message);
        return ResponseEntity.ok(Map.of("message", "Notifikasi terkirim"));
    }
}