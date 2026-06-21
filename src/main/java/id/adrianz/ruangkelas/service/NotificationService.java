package id.adrianz.ruangkelas.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
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
import org.thymeleaf.context.Context;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final DeviceTokenRepository deviceTokenRepository;
    private final NotificationRepository notificationRepository;
    private final EmailService emailService;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    // --- PUSH NOTIFICATION (Fokus Faiz) ---
    public void sendToUser(User user, String title, String body) {
        List<DeviceToken> tokens = deviceTokenRepository.findByUser(user);
        if (tokens.isEmpty()) return;

        List<String> tokenStrings = tokens.stream().map(DeviceToken::getToken).toList();

        if (tokenStrings.size() == 1) {
            sendToToken(tokenStrings.get(0), title, body);
        } else {
            sendMulticastNotification(tokenStrings, title, body);
        }
    }

    private void sendMulticastNotification(List<String> tokens, String title, String body) {
        MulticastMessage message = MulticastMessage.builder()
                .setNotification(com.google.firebase.messaging.Notification.builder()
                        .setTitle(title).setBody(body).build())
                .addAllTokens(tokens).build();

        try {
            FirebaseMessaging.getInstance().sendEachForMulticast(message);
        } catch (FirebaseMessagingException e) {
            log.error(e.getMessage());
        }
    }

    public void sendToToken(String token, String title, String body) {
        Message message = Message.builder()
                .setNotification(com.google.firebase.messaging.Notification.builder()
                        .setTitle(title).setBody(body).build())
                .setToken(token).build();

        try {
            FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException e) {
            log.error(e.getMessage());
        }
    }

    // --- EMAIL NOTIFICATION (Fokus Kamu) ---
    public void createAndSendEmailNotification(Integer userId, String emailTarget, String subject, String messageContent, NotificationType type, Integer referenceId, String referenceType) {
        // 1. Simpan ke Database
        EmailNotification notification = new EmailNotification();
        notification.setUserId(userId);
        notification.setEmail(emailTarget);
        notification.setSubject(subject);
        notification.setMessage(messageContent);
        notification.setType(type);
        notification.setReferenceId(referenceId);
        notification.setReferenceType(referenceType);
        notification.setIsRead(false);

        notificationRepository.save(notification);

        // 2. Setup Context untuk Thymeleaf
        Context context = new Context();
        context.setVariable("title", subject);
        context.setVariable("content", messageContent);
        context.setVariable("redirectUrl", baseUrl + "/notifications/redirect?type=" + referenceType + "&id=" + referenceId);

        // 3. Kirim via EmailService yang menggunakan TemplateEngine
        emailService.sendTemplateMessage(emailTarget, subject, "email/email-template", context);
    }

    // --- UTILITIES ---
    public List<Notification> getAllNotificationsByUserId(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public void markAllNotificationsAsRead(Long userId) {
        notificationRepository.markAllAsReadByUserId(userId);
    }
}