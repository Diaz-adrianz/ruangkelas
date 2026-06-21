package id.adrianz.ruangkelas.service;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final DeviceTokenRepository deviceTokenRepository;
    private final NotificationRepository notificationRepository;
    private final EmailService emailService;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

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
            FirebaseMessaging.getInstance().sendEachForMulticast(message);
        } catch (FirebaseMessagingException e) {
            log.error(e.getMessage());
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
            FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException e) {
            log.error(e.getMessage());
        }
    }

    public void createAndSendEmailNotification(Integer userId, String emailTarget, String subject, String messageContent, NotificationType type, Integer referenceId, String referenceType) {
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

        String redirectUrl = baseUrl + "/notifications/redirect?type=" + referenceType + "&id=" + referenceId;
        String htmlTemplate = loadHtmlTemplate(subject, messageContent, redirectUrl);

        emailService.sendHtmlMessage(emailTarget, subject, htmlTemplate);
    }

    private String loadHtmlTemplate(String title, String content, String redirectUrl) {
        try {
            ClassPathResource resource = new ClassPathResource("templates/email/email-template.html");
            String html = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            return html.replace("{{TITLE}}", title)
                       .replace("{{CONTENT}}", content)
                       .replace("{{REDIRECT_URL}}", redirectUrl);
        } catch (Exception e) {
            return content; 
        }
    }

    public List<Notification> getAllNotificationsByUserId(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public void markAllNotificationsAsRead(Long userId) {
        notificationRepository.markAllAsReadByUserId(userId);
    }
}