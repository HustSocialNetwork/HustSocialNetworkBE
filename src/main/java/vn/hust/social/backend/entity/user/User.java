package vn.hust.social.backend.entity.user;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import vn.hust.social.backend.entity.Base;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "users")
@Getter @Setter
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
        this.emailVerified = false;
    }

    @Override
    public String toString() {
        return String.format(
                "User[id=%s, firstName='%s', lastName='%s', displayName='%s']",
                id, firstName, lastName, displayName
        );
    }
}
