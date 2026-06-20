package id.adrianz.ruangkelas.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MulticastMessage;

import id.adrianz.ruangkelas.model.DeviceToken;
import id.adrianz.ruangkelas.model.EmailNotification;
import id.adrianz.ruangkelas.model.Notification;
import id.adrianz.ruangkelas.model.NotificationType;
import id.adrianz.ruangkelas.model.User;
import id.adrianz.ruangkelas.repository.DeviceTokenRepository;
import id.adrianz.ruangkelas.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final DeviceTokenRepository deviceTokenRepository;
    private final NotificationRepository notificationRepository;
    private final EmailService emailService;

    public void sendToUser(User user, String title, String body) {
        List<DeviceToken> tokens = deviceTokenRepository.findByUser(user);
        if (tokens.isEmpty()) {
            return;
        }

        List<String> tokenStrings = tokens.stream()
                .map(DeviceToken::getToken)
                .toList();

        if (tokenStrings.size() == 1) {
            sendToToken(tokenStrings.get(0), title, body);
        } else {
            sendMulticastNotification(tokenStrings, title, body);
        }
    }

    private void sendMulticastNotification(List<String> tokens, String title, String body) {
        MulticastMessage message = MulticastMessage.builder()
                .setNotification(com.google.firebase.messaging.Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .addAllTokens(tokens)
                .build();

        try {
            BatchResponse response = FirebaseMessaging.getInstance().sendEachForMulticast(message);
            log.info("Notifikasi multicast terkirim. Sukses: {}, Gagal: {}", 
                    response.getSuccessCount(), response.getFailureCount());
        } catch (FirebaseMessagingException e) {
            log.error("Gagal kirim notifikasi multicast: {}", e.getMessage());
        }
    }

    public void sendToToken(String token, String title, String body) {
        Message message = Message.builder()
                .setNotification(com.google.firebase.messaging.Notification.builder()
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

    public void createAndSendEmailNotification(Integer userId, String emailTarget, String subject, String message, NotificationType type, Integer referenceId, String referenceType) {
        EmailNotification notification = new EmailNotification();
        notification.setUserId(userId);
        notification.setEmail(emailTarget);
        notification.setSubject(subject);
        notification.setMessage(message);
        notification.setType(type);
        notification.setReferenceId(referenceId);
        notification.setReferenceType(referenceType);
        notification.setIsRead(false);

        notificationRepository.save(notification);

        emailService.sendSimpleMessage(emailTarget, subject, message);
    }

    public List<Notification> getAllNotificationsByUserId(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public void markAllNotificationsAsRead(Long userId) {
        notificationRepository.markAllAsReadByUserId(userId);
    }
}