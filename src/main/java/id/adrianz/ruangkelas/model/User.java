package id.adrianz.ruangkelas.model;

import java.beans.Transient;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private boolean enabled;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = true, length = 10)
    private String nim;

    @Column(nullable = true, length = 255)
    private String profilePicture;

    @Column(nullable = true)
    private String verificationToken;

    @Column(nullable = true)
    private LocalDateTime tokenExpiresAt;

    @Column(nullable = true)
    private String resetOtp;

    @Column(nullable = true)
    private LocalDateTime resetOtpExpiresAt;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Transient
    public String getInitials() {
        String[] parts = this.getName().trim().split("\\s+");

        if (parts.length == 1) {
            return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        }

        return (parts[0].charAt(0) + "" + parts[1].charAt(0)).toUpperCase();
    }
}