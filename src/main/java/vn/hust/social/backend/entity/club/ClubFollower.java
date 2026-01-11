package vn.hust.social.backend.entity.club;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import vn.hust.social.backend.entity.Base;
import vn.hust.social.backend.entity.user.User;
import vn.hust.social.backend.entity.enums.club.ClubFollowerStatus;

import java.util.UUID;

@Entity
@Table(name = "club_follower", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "club_id", "user_id" })
})
@Getter
@Setter
public class ClubFollower extends Base {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id", nullable = false, columnDefinition = "BINARY(16)")
    private Club club;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, columnDefinition = "BINARY(16)")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(50)", nullable = false)
    private ClubFollowerStatus status;

    protected ClubFollower() {
    }

    public ClubFollower(Club club, User user, ClubFollowerStatus status) {
        this.club = club;
        this.user = user;
        this.status = status;
    }
}
