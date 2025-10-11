package vn.hust.social.backend.config;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_auths",
        uniqueConstraints = @UniqueConstraint(columnNames = {"provider", "provider_id"}))
public class UserAuth {

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

    @Column(name = "provider_id", nullable = false)
    private String providerId; // email cho LOCAL, M365 id cho M365

    @Column(name = "password")
    private String password; // nullable nếu OAuth

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected UserAuth() {
        // Constructor mặc định cho JPA
    }

    public UserAuth(User user, AuthProvider provider, String providerId, String password) {
        this.id = UUID.randomUUID();
        this.user = user;
        this.provider = provider;
        this.providerId = providerId;
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
        return providerId;
    }

    public String getPassword() {
        return password;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // ===== Setters =====
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return String.format(
                "UserAuth[id=%s, userId=%s, provider=%s, providerId=%s]",
                id, user.getId(), provider, providerId
        );
    }

    // ===== Enum cho provider =====
    public enum AuthProvider {
        LOCAL,
        M365
    }
}

