package vn.hust.social.backend.entity.like;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import vn.hust.social.backend.entity.post.Post;
import vn.hust.social.backend.entity.user.User;

import java.util.UUID;

@Data
@Entity
@Table(name = "post_like", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "post_id"}))
@Getter
@Setter
public class PostLike {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    protected PostLike() {}

    public PostLike(User user, Post post) {
        this.user = user;
        this.post = post;
    }
}
