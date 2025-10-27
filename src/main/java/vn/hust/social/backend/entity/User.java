package vn.hust.social.backend.entity;

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
    private String avatarKey;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    protected User() {
        // Default JPA constructor
    }

    public User(String firstName, String lastName, String displayName) {
        this.id = UUID.randomUUID(); // tạo UUID khi khởi tạo
        this.firstName = firstName;
        this.lastName = lastName;
        this.displayName = displayName;
        this.avatarKey = "/user-avatars/default.png";
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

    public String getAvatarKey() {
        return avatarKey;
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

    public void setAvatarKey(String avatarKey) {
        this.avatarKey = avatarKey;
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
