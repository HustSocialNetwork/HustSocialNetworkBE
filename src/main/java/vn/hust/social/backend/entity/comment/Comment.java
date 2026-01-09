package vn.hust.social.backend.entity.comment;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import vn.hust.social.backend.entity.Base;
import vn.hust.social.backend.entity.post.Post;
import vn.hust.social.backend.entity.user.User;

import java.util.UUID;

@Entity
@Table(name = "comment")
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class Comment extends Base {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    private UUID id;

    /* ===== AUTHOR ===== */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /* ===== POST ===== */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    /* ===== THREAD ===== */
    // null = root comment
    // not null = reply to another comment
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    /* ===== CONTENT ===== */
    @Size(min = 1, max = 5000)
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    /* ===== META ===== */
    @Column(name = "likes_count", nullable = false)
    private Integer likesCount = 0;

    // Number of direct replies to this comment
    @Column(name = "replies_count", nullable = false)
    private Integer repliesCount = 0;

    protected Comment() {
    }

    public Comment(User user, Post post, Comment parent, String content) {
        this.user = user;
        this.post = post;
        this.parent = parent;
        this.content = content;
    }
}
