package vn.hust.social.backend.entity.comment;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import vn.hust.social.backend.entity.enums.media.MediaType;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false, of = "{commentMediaId}")
@Entity
@Table(name = "comment_media")
@Getter
@Setter
public class CommentMedia {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "BINARY(16)", updatable = false, nullable = false)
    private UUID commentMediaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;

    @Column(name = "type", nullable = false)
    private MediaType type;

    @Column(name = "object_key", nullable = false)
    private String objectKey;

    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;

    protected CommentMedia() {};

    public CommentMedia(Comment comment, MediaType type, String objectKey, Integer orderIndex) {
        this.comment = comment;
        this.type = type;
        this.objectKey = objectKey;
        this.orderIndex = orderIndex;
    }
}
