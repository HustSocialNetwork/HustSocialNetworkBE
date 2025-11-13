package vn.hust.social.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "post")
@Getter @Setter
public class Post {

    // ===== COLUMNS =====
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

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt =  LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.DRAFT;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Visibility visibility;

    @Column(name = "likes_count")
    private Integer likesCount = 0;

    @Column(name = "comments_count")
    private Integer commentsCount = 0;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostMedia> mediaList =  new ArrayList<>();

    // ===== CONSTRUCTOR =====
    protected Post () {
        // Default constructor
    }

    public Post(User user, String content, Visibility visibility) {
        this.user = user;
        this.content = content;
        this.visibility = visibility;
    }
    // ===== ENUM =====
    public enum Status {
        ACTIVE, // hiện lên tường
        DRAFT // không hiện lên cả tường của mình, phải có một mục bài viết ẩn, xóa sau 10 ngày
    }

    public enum Visibility {
        PUBLIC,
        FRIENDS,
        PRIVATE
    }
}
