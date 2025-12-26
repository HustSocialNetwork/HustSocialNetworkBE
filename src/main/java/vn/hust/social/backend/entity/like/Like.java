package vn.hust.social.backend.entity.like;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import vn.hust.social.backend.entity.Base;
import vn.hust.social.backend.entity.enums.like.TargetType;
import vn.hust.social.backend.entity.user.User;

import java.util.UUID;

@Entity
@Table(
        name = "likes",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "target_id", "target_type"})
        }
)
@Getter
@Setter
public class Like extends Base {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "target_id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID targetId;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false)
    private TargetType targetType;

    protected Like() {}

    public Like(User user, UUID targetId, TargetType targetType) {
        this.user = user;
        this.targetId = targetId;
        this.targetType = targetType;
    }
}
