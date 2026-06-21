package id.adrianz.ruangkelas.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import id.adrianz.ruangkelas.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    Optional<User> findByUsernameOrEmail(String username, String email);

    Optional<User> findByVerificationToken(String verificationToken);

    Optional<User> findByResetOtp(String resetOtp);
}
