package id.adrianz.ruangkelas.controller.api;

import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import id.adrianz.ruangkelas.config.FirebaseConfigProperties;
import id.adrianz.ruangkelas.model.DeviceToken;
import id.adrianz.ruangkelas.model.User;
import id.adrianz.ruangkelas.repository.DeviceTokenRepository;
import id.adrianz.ruangkelas.repository.UserRepository;
import id.adrianz.ruangkelas.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
public class NotificationApiController {

    private final FirebaseConfigProperties firebaseConfig;
    private final DeviceTokenRepository deviceTokenRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    
    @GetMapping("/firebase-messaging-sw.js")
    public ResponseEntity<String> firebaseSwJs() {
        String js = """
                importScripts('https://www.gstatic.com/firebasejs/10.12.0/firebase-app-compat.js');
                importScripts('https://www.gstatic.com/firebasejs/10.12.0/firebase-messaging-compat.js');

                firebase.initializeApp({
                    apiKey: "%s",
                    authDomain: "%s",
                    projectId: "%s",
                    storageBucket: "%s",
                    messagingSenderId: "%s",
                    appId: "%s"
                });

                const messaging = firebase.messaging();

                messaging.onBackgroundMessage((payload) => {
                    const { title, body } = payload.notification;
                    self.registration.showNotification(title, {
                        body,
                        icon: '/images/Logo.svg',
                        badge: '/images/Logo.svg',
                    });
                });
                """.formatted(
                firebaseConfig.getApiKey(),
                firebaseConfig.getAuthDomain(),
                firebaseConfig.getProjectId(),
                firebaseConfig.getStorageBucket(),
                firebaseConfig.getMessagingSenderId(),
                firebaseConfig.getAppId()
        );

        // Header Service-Worker-Allowed sangat penting agar scope '/' diizinkan meski file ada di /api/notification/
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/javascript"))
                .header("Service-Worker-Allowed", "/")
                .body(js);
    }

    @PostMapping("/register-token")
    public ResponseEntity<?> registerToken(
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal UserDetails userDetails) {

        String token = body.get("token");

        if (token == null || token.isBlank()) {
            return ResponseEntity.badRequest().body("Token tidak boleh kosong");
        }

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        // Cek apakah token sudah ada di database
        // Asumsi kamu punya method findByToken di DeviceTokenRepository
        // Jika tidak punya, buat method: Optional<DeviceToken> findByToken(String token);
        DeviceToken existingToken = deviceTokenRepository.findByToken(token).orElse(null);

        if (existingToken == null) {
            // Jika token benar-benar baru
            DeviceToken deviceToken = new DeviceToken();
            deviceToken.setToken(token);
            deviceToken.setUser(user);
            deviceTokenRepository.save(deviceToken);
            log.info("Token baru berhasil didaftarkan untuk user: {}", user.getUsername());
        } else if (!existingToken.getUser().getId().equals(user.getId())) {
            // Jika token sudah ada tapi dimiliki user lain (misal pindah akun di device yang sama)
            existingToken.setUser(user);
            deviceTokenRepository.save(existingToken);
            log.info("Kepemilikan token diupdate untuk user: {}", user.getUsername());
        }

        return ResponseEntity.ok(Map.of("message", "Token berhasil disimpan"));
    }

    @PostMapping("/test-push")
    public ResponseEntity<?> sendNotification(@RequestBody Map<String, String> body) {
        String token = body.get("token");
        String title = body.get("title");
        String message = body.get("message");

        notificationService.sendToToken(token, title, message);
        return ResponseEntity.ok(Map.of("message", "Notifikasi terkirim"));
    }
}