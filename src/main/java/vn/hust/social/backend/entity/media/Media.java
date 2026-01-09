package vn.hust.social.backend.entity.media;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import vn.hust.social.backend.entity.enums.media.MediaTargetType;
import vn.hust.social.backend.entity.enums.media.MediaType;

import java.util.UUID;

@Entity
@Table(name = "media",
        indexes = {
                @Index(name = "idx_media_target", columnList = "target_id, target_type")
        })
@Getter
@Setter
@EqualsAndHashCode(of = "mediaId")
public class Media {

    @Id
    @GeneratedValue
    @Column(name = "media_id", columnDefinition = "BINARY(16)", updatable = false, nullable = false)
    private UUID mediaId;

    @Column(name = "target_id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID targetId;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false, length = 20)
    private MediaTargetType targetType;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private MediaType type;

    @Column(name = "object_key", nullable = false)
    private String objectKey;

    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;

    protected Media() {}

    public Media(UUID targetId,
                 MediaTargetType targetType,
                 MediaType type,
                 String objectKey,
                 int orderIndex) {
        this.targetId = targetId;
        this.targetType = targetType;
        this.type = type;
        this.objectKey = objectKey;
        this.orderIndex = orderIndex;
    }
}
