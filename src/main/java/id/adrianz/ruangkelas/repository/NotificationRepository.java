package id.adrianz.ruangkelas.repository;

import id.adrianz.ruangkelas.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findByUserIdAndIsReadFalse(Integer userId);
}