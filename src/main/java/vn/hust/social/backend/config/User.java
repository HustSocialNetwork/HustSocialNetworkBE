package vn.hust.social.backend.config;

//

import jakarta.persistence.*;

import java.util.UUID;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "BINARY(16)") // lưu UUID dưới dạng binary 16 byte, tối ưu DB
    private UUID id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    protected User() {
        // Default JPA constructor
    }

    public User(String firstName, String lastName, String displayName, String avatarUrl) {
        this.id = UUID.randomUUID(); // tạo UUID khi khởi tạo
        this.firstName = firstName;
        this.lastName = lastName;
        this.displayName = displayName;
        this.avatarUrl = avatarUrl;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // ===== Getters =====
    public UUID getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // ===== Setters =====
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        this.updatedAt = LocalDateTime.now();
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return String.format(
                "User[id=%s, firstName='%s', lastName='%s', displayName='%s']",
                id, firstName, lastName, displayName
        );
    }
}
