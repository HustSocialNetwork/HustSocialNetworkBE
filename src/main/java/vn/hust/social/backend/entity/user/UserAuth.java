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
@Table(name = "user_auths",
        uniqueConstraints = @UniqueConstraint(columnNames = {"provider", "email"}))
@Getter
@Setter
public class UserAuth extends Base {

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
    @Column(name = "provider", nullable = false)
    private AuthProvider provider; // LOCAL, M365

    @Column(name = "email", nullable = false)
    private String email; // email cho LOCAL, M365 id cho M365

    @Column(name = "password")
    private String password; // nullable nếu OAuth


    protected UserAuth() {
        // Constructor mặc định cho JPA
    }

    public UserAuth(User user, AuthProvider provider, String email, String password) {
        this.user = user;
        this.provider = provider;
        this.email = email;
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

