package vn.hust.social.backend.entity.post;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import vn.hust.social.backend.entity.Base;
import vn.hust.social.backend.entity.comment.Comment;
import vn.hust.social.backend.entity.enums.post.PostStatus;
import vn.hust.social.backend.entity.enums.post.PostVisibility;
import vn.hust.social.backend.entity.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "post")
@Getter @Setter
public class Post extends Base {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "BINARY(16)", updatable = false, nullable = false)
    private UUID postId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Size(min = 1, max = 5000)
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PostStatus status = PostStatus.DRAFT;

    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", nullable = false)
    private PostVisibility visibility;

    @Column(name = "likes_count")
    private Integer likesCount = 0;

    @Column(name = "comments_count")
    private Integer commentsCount = 0;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostMedia> mediaList =  new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    protected Post () {
        // Default constructor
    }

    public Post(User user, String content, PostVisibility visibility) {
        this.status = PostStatus.ACTIVE;
        this.user = user;
        this.content = content;
        this.visibility = visibility;
    }
}
