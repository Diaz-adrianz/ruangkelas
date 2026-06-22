package id.adrianz.ruangkelas.controller;

import id.adrianz.ruangkelas.config.FirebaseProperties;
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
@RequestMapping("/api/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final UserRepository userRepository;
    private final DeviceTokenRepository deviceTokenRepository;
    private final FirebaseProperties firebaseProperties;

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

    @PostMapping("/register-token")
    public ResponseEntity<?> registerDeviceToken(Authentication authentication, @RequestBody Map<String, String> payload) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));
        String token = payload.get("token");
        if (token != null && !deviceTokenRepository.existsByToken(token)) {
            DeviceToken deviceToken = new DeviceToken();
            deviceToken.setToken(token);
            deviceToken.setUser(user);
            deviceTokenRepository.save(deviceToken);
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/firebase-messaging-sw.js", produces = "application/javascript")
    public ResponseEntity<String> firebaseSwJs() {
        String script = """
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
                firebaseProperties.getApiKey(),
                firebaseProperties.getAuthDomain(),
                firebaseProperties.getProjectId(),
                firebaseProperties.getStorageBucket(),
                firebaseProperties.getMessagingSenderId(),
                firebaseProperties.getAppId()
        );
        return ResponseEntity.ok(script);
    }
}