package vn.hust.social.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "post_media")
@Getter
@Setter
public class PostMedia {
    // post_id, type, url
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(name = "type", nullable = false)
    private Type type;

    @Id
    @Column(name = "object_key", nullable = false)
    private String objectKey;

    @Column(name = "order_index", nullable = false)
    private int orderIndex;

    // ===== CONSTRUCTOR =====
    protected PostMedia() {}

    public PostMedia(Post post, Type type, String objectKey, int orderIndex) {
        this.post = post;
        this.type = type;
        this.objectKey = objectKey;
        this.orderIndex = orderIndex;
    }
    // ===== ENUM =====
    public enum Type {
        IMAGE,
        VIDEO
    }
}
