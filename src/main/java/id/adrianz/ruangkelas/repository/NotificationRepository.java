package id.adrianz.ruangkelas.repository;

import java.time.LocalDateTime;
import java.util.List;

import id.adrianz.ruangkelas.model.Notification;
import id.adrianz.ruangkelas.model.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.userId = :userId AND n.isRead = false")
    void markAllAsReadByUserId(Long userId);

    boolean existsByUserIdAndReferenceIdAndReferenceTypeAndTypeAndCreatedAtBetween(
            Long userId, Long referenceId, String referenceType, NotificationType type,
            LocalDateTime start, LocalDateTime end);
}