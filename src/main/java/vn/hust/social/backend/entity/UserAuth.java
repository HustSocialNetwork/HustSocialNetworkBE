package vn.hust.social.backend.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_auths",
        uniqueConstraints = @UniqueConstraint(columnNames = {"provider", "provider_id"}))
public class UserAuth {

    public enum AuthProvider {
        LOCAL,
        M365
    }

    @Id
    @GeneratedValue
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthProvider provider; // LOCAL, M365

    @Column(name = "email", nullable = false)
    private String email; // email cho LOCAL, M365 id cho M365

    @Column(name = "password")
    private String password; // nullable nếu OAuth

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected UserAuth() {
        // Constructor mặc định cho JPA
    }

    public UserAuth(User user, AuthProvider provider, String email, String password) {
        this.id = UUID.randomUUID();
        this.user = user;
        this.provider = provider;
        this.email = email;
        this.password = password;
        this.createdAt = LocalDateTime.now();
    }

    // ===== Getters =====
    public UUID getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public AuthProvider getProvider() {
        return provider;
    }

    public String getProviderId() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getEmail() {
        return email;
    }

    // ===== Setters =====
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return String.format(
                "UserAuth[id=%s, userId=%s, provider=%s, email=%s]",
                id, user.getId(), provider, email
        );
    }
}

