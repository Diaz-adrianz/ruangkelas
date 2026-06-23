package id.adrianz.ruangkelas.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MessagingErrorCode;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.SendResponse;
import com.google.firebase.messaging.WebpushConfig;
import com.google.firebase.messaging.WebpushFcmOptions;

import id.adrianz.ruangkelas.model.DeviceToken;
import id.adrianz.ruangkelas.model.EmailNotification;
import id.adrianz.ruangkelas.model.Notification;
import id.adrianz.ruangkelas.model.NotificationType;
import id.adrianz.ruangkelas.model.PushNotification;
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

    public void createAndSendPushNotification(User user, String title, String body,
                                              NotificationType type,
                                              Long referenceId, String referenceType) {
        PushNotification notification = new PushNotification();
        notification.setUserId(user.getId());
        notification.setTitle(title);
        notification.setMessage(body);
        notification.setType(type);
        notification.setReferenceId(referenceId);
        notification.setReferenceType(referenceType);
        notification.setIsRead(false);

        notificationRepository.save(notification);

        sendToUser(user, title, body, referenceId, referenceType);
    }

    public boolean alreadyNotifiedToday(Long userId, Long referenceId, String referenceType, NotificationType type) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(23, 59, 59);

        return notificationRepository.existsByUserIdAndReferenceIdAndReferenceTypeAndTypeAndCreatedAtBetween(
                userId, referenceId, referenceType, type, startOfDay, endOfDay);
    }

    private void sendToUser(User user, String title, String body, Long referenceId, String referenceType) {
        List<DeviceToken> tokens = deviceTokenRepository.findByUser(user);
        if (tokens.isEmpty()) {
            log.warn(">>> User {} gak punya device token terdaftar, push dilewati", user.getId());
            return;
        }

        List<String> tokenStrings = tokens.stream().map(DeviceToken::getToken).toList();

        if (tokenStrings.size() == 1) {
            sendToToken(tokenStrings.get(0), title, body, referenceId, referenceType);
        } else {
            sendMulticastNotification(tokenStrings, title, body, referenceId, referenceType);
        }
    }

    private void sendMulticastNotification(List<String> tokens, String title, String body,
                                           Long referenceId, String referenceType) {
        
        // Membentuk URL redirect berdasarkan tipe dan ID
        String clickUrl = (referenceId != null && referenceType != null) 
                ? baseUrl + "/notifications/redirect?type=" + referenceType + "&id=" + referenceId 
                : baseUrl + "/";

        MulticastMessage.Builder builder = MulticastMessage.builder()
                .setNotification(com.google.firebase.messaging.Notification.builder()
                        .setTitle(title).setBody(body).build())
                // Menambahkan konfigurasi link agar notifikasi bisa diklik (khusus Web Push)
                .setWebpushConfig(WebpushConfig.builder()
                        .setFcmOptions(WebpushFcmOptions.builder().setLink(clickUrl).build())
                        .build())
                .addAllTokens(tokens);

        putReferenceData(builder, title, body, referenceId, referenceType);

        MulticastMessage message = builder.build();

        try {
            BatchResponse batch = FirebaseMessaging.getInstance().sendEachForMulticast(message);
            List<SendResponse> responses = batch.getResponses();

            for (int i = 0; i < responses.size(); i++) {
                SendResponse r = responses.get(i);
                if (r.isSuccessful()) {
                    log.info(">>> FCM multicast sukses ke token {}, messageId: {}", tokens.get(i), r.getMessageId());
                } else {
                    handleFcmError(tokens.get(i), r.getException());
                }
            }
        } catch (FirebaseMessagingException e) {
            log.error(">>> Gagal kirim multicast FCM: {}", e.getMessage());
        }
    }

    public void sendToToken(String token, String title, String body) {
        sendToToken(token, title, body, null, null);
    }

    public void sendToToken(String token, String title, String body, Long referenceId, String referenceType) {
        
        // Membentuk URL redirect berdasarkan tipe dan ID
        String clickUrl = (referenceId != null && referenceType != null) 
                ? baseUrl + "/notifications/redirect?type=" + referenceType + "&id=" + referenceId 
                : baseUrl + "/";

        Message.Builder builder = Message.builder()
                .setNotification(com.google.firebase.messaging.Notification.builder()
                        .setTitle(title).setBody(body).build())
                // Menambahkan konfigurasi link agar notifikasi bisa diklik (khusus Web Push)
                .setWebpushConfig(WebpushConfig.builder()
                        .setFcmOptions(WebpushFcmOptions.builder().setLink(clickUrl).build())
                        .build())
                .setToken(token);

        putReferenceData(builder, title, body, referenceId, referenceType);

        Message message = builder.build();

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            log.info(">>> FCM sukses, token: {}, messageId: {}", token, response);
        } catch (FirebaseMessagingException e) {
            handleFcmError(token, e);
        }
    }

    private void putReferenceData(Message.Builder builder, String title, String body,
                                  Long referenceId, String referenceType) {
        builder.putData("title", title);
        builder.putData("body", body);
        if (referenceType != null) builder.putData("referenceType", referenceType);
        if (referenceId != null) builder.putData("referenceId", referenceId.toString());
    }

    private void putReferenceData(MulticastMessage.Builder builder, String title, String body,
                                  Long referenceId, String referenceType) {
        builder.putData("title", title);
        builder.putData("body", body);
        if (referenceType != null) builder.putData("referenceType", referenceType);
        if (referenceId != null) builder.putData("referenceId", referenceId.toString());
    }

    @Transactional // Memastikan query delete custom berjalan dengan aman
    protected void handleFcmError(String token, FirebaseMessagingException e) {
        if (e == null) return;

        // FITUR UTAMA: Mempertahankan pengecekan standar FCM
        // FITUR TAMBAHAN: Antisipasi jika getMessagingErrorCode() bernilai null akibat ZipException pada HTTP 404
        if (e.getMessagingErrorCode() == MessagingErrorCode.UNREGISTERED || 
            (e.getMessage() != null && e.getMessage().contains("404"))) {
            
            log.warn(">>> Token tidak valid atau unregistered (404), menghapus dari DB: {}", token);
            deviceTokenRepository.deleteByToken(token);
        } else {
            log.error(">>> Gagal kirim FCM ke token {} ({}): {}", token, e.getMessagingErrorCode(), e.getMessage());
        }
    }

    // PERBAIKAN: Mengubah Long userId menjadi User user agar sinkron dengan fitur Push Notification di bawahnya
    public void createAndSendEmailNotification(User user, String emailTarget, String subject,
                                               String messageContent, NotificationType type,
                                               Long referenceId, String referenceType) {
        
        // 1. Proses Save & Kirim Email Notification
        EmailNotification emailNotification = new EmailNotification();
        emailNotification.setUserId(user.getId());
        emailNotification.setEmail(emailTarget);
        emailNotification.setSubject(subject);
        emailNotification.setMessage(messageContent);
        emailNotification.setType(type);
        emailNotification.setReferenceId(referenceId);
        emailNotification.setReferenceType(referenceType);
        emailNotification.setIsRead(false);
        notificationRepository.save(emailNotification);  
        
        Context context = new Context();
        context.setVariable("title", subject);
        context.setVariable("content", messageContent);

        emailService.sendTemplateMessage(emailTarget, subject, "email/Notification", context);

        // 2. Proses Save & Kirim Push Notification (Fitur dipertahankan & diperbaiki variabelnya)
        PushNotification pushNotification = new PushNotification();
        pushNotification.setUserId(user.getId());
        pushNotification.setTitle(subject); // Menyesuaikan dari parameter subject
        pushNotification.setMessage(messageContent); // Menyesuaikan dari parameter messageContent
        pushNotification.setType(type);
        pushNotification.setReferenceId(referenceId);
        pushNotification.setReferenceType(referenceType);
        pushNotification.setIsRead(false);
        
        notificationRepository.save(pushNotification);

        sendToUser(user, subject, messageContent, referenceId, referenceType);
    }

    public List<Notification> getAllNotificationsByUserId(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public void markAllNotificationsAsRead(Long userId) {
        notificationRepository.markAllAsReadByUserId(userId);
    }

    public void markAsRead(Integer id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notifikasi tidak ditemukan"));
        notification.setIsRead(true);
        notificationRepository.save(notification);  
    }

    public void deleteNotification(Integer id) {
        notificationRepository.deleteById(id);
    }

    public Notification getNotificationById(Integer id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notifikasi tidak ditemukan"));
    }
}