package id.adrianz.ruangkelas.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import id.adrianz.ruangkelas.dto.RegisterDto;
import id.adrianz.ruangkelas.model.User;
import id.adrianz.ruangkelas.model.UserPrincipal;
import id.adrianz.ruangkelas.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> new UsernameNotFoundException("Akun tidak ditemukan"));

        return new UserPrincipal(user);
    }

    public User register(RegisterDto request) {
        if (userRepository.existsByUsername(request.getEmail())) {
            throw new IllegalArgumentException("Username sudah terdaftar");
        }

        userRepository.findByEmail(request.getEmail()).ifPresent(existing -> {
            boolean unverified = !existing.isEnabled();
            boolean expired = existing.getTokenExpiresAt() != null
                    && existing.getTokenExpiresAt().isBefore(LocalDateTime.now());

            if (!unverified || !expired) {
                throw new IllegalArgumentException("Email sudah terdaftar");
            }

            userRepository.delete(existing);
        });

        String token = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(24);

        User user = userRepository.save(User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .enabled(false)
                .name(request.getName())
                .nim(request.getNim())
                .verificationToken(token)
                .tokenExpiresAt(expiresAt)
                .build());

        emailService.sendVerificationEmail(user.getEmail(), token, expiresAt);
        return user;
    }

    public User save(User user) {
        return userRepository.save(user);
    }
}
