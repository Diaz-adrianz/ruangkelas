package id.adrianz.ruangkelas.service;

import com.google.firebase.messaging.*;
import id.adrianz.ruangkelas.model.DeviceToken;
import id.adrianz.ruangkelas.model.User;
import id.adrianz.ruangkelas.repository.DeviceTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final DeviceTokenRepository deviceTokenRepository;

    /**
     * Kirim notifikasi ke semua device milik satu user
     */
    public void sendToUser(User user, String title, String body) {
        List<DeviceToken> tokens = deviceTokenRepository.findByUser(user);
        if (tokens.isEmpty()) return;

        List<String> tokenStrings = tokens.stream()
            .map(DeviceToken::getToken)
            .toList();

        MulticastMessage message = MulticastMessage.builder()
            .setNotification(Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build())
            .addAllTokens(tokenStrings)
            .build();

        try {
            BatchResponse response = FirebaseMessaging.getInstance().sendEachForMulticast(message);
            log.info("Notifikasi terkirim: {}/{}", response.getSuccessCount(), tokens.size());
        } catch (FirebaseMessagingException e) {
            log.error("Gagal kirim notifikasi ke user {}: {}", user.getId(), e.getMessage());
        }
    }

    /**
     * Kirim ke satu token saja (misal broadcast web)
     */
    public void sendToToken(String token, String title, String body) {
        Message message = Message.builder()
            .setNotification(Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build())
            .setToken(token)
            .build();

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            log.info("Notifikasi terkirim: {}", response);
        } catch (FirebaseMessagingException e) {
            log.error("Gagal kirim notifikasi ke token {}: {}", token, e.getMessage());
        }
    }
}