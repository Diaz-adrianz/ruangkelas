package id.adrianz.ruangkelas.service;

import id.adrianz.ruangkelas.model.EmailNotification;
import id.adrianz.ruangkelas.model.NotificationType;
import id.adrianz.ruangkelas.repository.NotificationRepository;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;

    public NotificationService(NotificationRepository notificationRepository, EmailService emailService) {
        this.notificationRepository = notificationRepository;
        this.emailService = emailService;
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
}