package id.adrianz.ruangkelas.repository;

import id.adrianz.ruangkelas.model.DeviceToken;
import id.adrianz.ruangkelas.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceTokenRepository extends JpaRepository<DeviceToken, Long> {
    boolean existsByToken(String token);
    List<DeviceToken> findByUser(User user);
}