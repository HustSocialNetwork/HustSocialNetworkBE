package vn.hust.social.backend.repository.media;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.hust.social.backend.entity.enums.media.MediaTargetType;
import vn.hust.social.backend.entity.media.Media;

import java.util.List;
import java.util.UUID;

public interface MediaRepository extends JpaRepository<Media, UUID> {
    List<Media> findByTargetIdAndTargetType(UUID targetId, MediaTargetType targetType);

    void deleteByMediaId(UUID mediaId);
    
    void deleteByObjectKeyAndTargetType(String objectKey, MediaTargetType targetType);
}
