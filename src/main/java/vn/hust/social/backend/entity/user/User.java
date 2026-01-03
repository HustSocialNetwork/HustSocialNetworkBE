package vn.hust.social.backend.entity.user;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import vn.hust.social.backend.entity.Base;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "users")
public class User extends Base {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    @Column(name = "avatar_key")
    private String avatarKey;

    @Column(name = "background_key")
    private String backgroundKey;

    @Column(length = 200)
    private String bio;

    @Column(name = "follower_count")
    private Integer followerCount = 0;

    @Column(name = "following")
    private Integer followingCount = 0;

    @Column(name = "friends")
    private Integer friendsCount = 0;

    @Column(name = "email_verified")
    private boolean emailVerified;

    protected User() {
        // Default JPA constructor
    }

    public User(String firstName, String lastName, String displayName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.displayName = displayName;
        this.avatarKey = "/user-avatars/default.png";
        this.backgroundKey = "/user-backgrounds/default.png";
        this.emailVerified = false;
    }

    public String getFullName() {
        return lastName + " " + firstName;
    }
}
