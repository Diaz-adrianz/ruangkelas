package id.adrianz.ruangkelas.controller.api;

import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import id.adrianz.ruangkelas.config.FirebaseConfigProperties;
import id.adrianz.ruangkelas.model.DeviceToken;
import id.adrianz.ruangkelas.model.Notification;
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
  console.log('[firebase-messaging-sw.js] Pesan masuk di background:', payload);
  const title = payload.notification?.title || payload.data?.title || 'Notifikasi Ruangkelas';
  const options = {
    body: payload.notification?.body || payload.data?.body || '',
    icon: '/icons/icon-192.png',
    data: payload
  };
  self.registration.showNotification(title, options);
});

self.addEventListener('notificationclick', (event) => {
  event.notification.close();
  let targetUrl = '/';
  const payload = event.notification.data || {};
  console.log('[notificationclick] full payload:', JSON.stringify(payload));
  const dataBlock = payload.data
    || payload.FCM_MSG?.data
    || payload.FCM_MSG?.notification?.data
    || {};
  console.log('[notificationclick] dataBlock:', JSON.stringify(dataBlock));
  if (payload.fcmOptions?.link) {
      targetUrl = payload.fcmOptions.link;
  } else if (payload.FCM_MSG?.fcmOptions?.link) {
      targetUrl = payload.FCM_MSG.fcmOptions.link;
  } else if (dataBlock.referenceType && dataBlock.referenceId) {
      targetUrl = `/notifications/redirect?type=${dataBlock.referenceType}&id=${dataBlock.referenceId}`;
  }
  console.log('[notificationclick] targetUrl:', targetUrl);
  const finalUrl = new URL(targetUrl, self.location.origin).href;
  event.waitUntil(
      clients.matchAll({ type: 'window', includeUncontrolled: true }).then((clientList) => {
          for (let i = 0; i < clientList.length; i++) {
              let client = clientList[i];
              if (client.url.startsWith(self.location.origin) && 'focus' in client) {
                  client.focus();
                  if ('navigate' in client) {
                      return client.navigate(finalUrl);
                  }
              }
          }
          if (clients.openWindow) {
              return clients.openWindow(finalUrl);
          }
      })
  );
});
""".formatted(
                firebaseConfig.getApiKey(),
                firebaseConfig.getAuthDomain(),
                firebaseConfig.getProjectId(),
                firebaseConfig.getStorageBucket(),
                firebaseConfig.getMessagingSenderId(),
                firebaseConfig.getAppId()
        );

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

        DeviceToken existingToken = deviceTokenRepository.findByToken(token).orElse(null);

        if (existingToken == null) {
            DeviceToken deviceToken = new DeviceToken();
            deviceToken.setToken(token);
            deviceToken.setUser(user);
            deviceTokenRepository.save(deviceToken);
            log.info("Token baru berhasil didaftarkan untuk user: {}", user.getUsername());
        } else if (!existingToken.getUser().getId().equals(user.getId())) {
            existingToken.setUser(user);
            deviceTokenRepository.save(existingToken);
            log.info("Kepemilikan token diupdate untuk user: {}", user.getUsername());
        }

        return ResponseEntity.ok(Map.of("message", "Token berhasil disimpan"));
    }

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

    @PostMapping("/test-push")
    public ResponseEntity<?> sendNotification(@RequestBody Map<String, String> body) {
        String token = body.get("token");
        String title = body.get("title");
        String message = body.get("message");
        notificationService.sendToToken(token, title, message);
        return ResponseEntity.ok(Map.of("message", "Notifikasi terkirim"));
    }
}