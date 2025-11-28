package vn.hust.social.backend.entity.post;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import vn.hust.social.backend.entity.enums.media.MediaType;

@Entity
@Table(name = "post_media")
@Getter
@Setter
@EqualsAndHashCode(of = {"postMediaId"})
public class PostMedia {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "BINARY(16)", updatable = false, nullable = false)
    private Long postMediaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(name = "type", nullable = false)
    private MediaType type;

    @Column(name = "object_key", nullable = false)
    private String objectKey;

    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;

    protected PostMedia() {}

    public PostMedia(Post post, MediaType type, String objectKey, int orderIndex) {
        this.post = post;
        this.type = type;
        this.objectKey = objectKey;
        this.orderIndex = orderIndex;
    }
}
