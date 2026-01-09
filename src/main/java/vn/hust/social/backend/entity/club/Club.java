package vn.hust.social.backend.entity.club;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import vn.hust.social.backend.entity.Base;

import java.util.UUID;

@Entity
@Table(name = "club")
@Getter
@Setter
public class Club extends Base {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "avatar_key")
    private String avatarKey;

    @Column(name = "background_key")
    private String backgroundKey;

    @Column(name = "follower_count", nullable = false)
    private Integer followerCount = 0;

    protected Club() {
    }

    public Club(String name, String description) {
        this.name = name;
        this.description = description;
        this.avatarKey = "/club-avatars/default.png";
        this.backgroundKey = "/club-backgrounds/default.png";
    }
}
