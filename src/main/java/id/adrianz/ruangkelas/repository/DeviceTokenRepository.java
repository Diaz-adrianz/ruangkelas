package id.adrianz.ruangkelas.repository;

import id.adrianz.ruangkelas.model.DeviceToken;
import id.adrianz.ruangkelas.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional; // TAMBAHAN IMPORT

@Repository
public interface DeviceTokenRepository extends JpaRepository<DeviceToken, Long> {
    boolean existsByToken(String token);
    List<DeviceToken> findByUser(User user);

    // TAMBAHAN: Untuk mengecek token dan mengupdate kepemilikan user di controller
    Optional<DeviceToken> findByToken(String token);

    @Modifying
    @Transactional
    void deleteByToken(String token);
}