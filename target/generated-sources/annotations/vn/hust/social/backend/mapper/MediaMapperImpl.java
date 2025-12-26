package vn.hust.social.backend.mapper;

import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import vn.hust.social.backend.dto.MediaDTO;
import vn.hust.social.backend.entity.enums.media.MediaTargetType;
import vn.hust.social.backend.entity.enums.media.MediaType;
import vn.hust.social.backend.entity.media.Media;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-27T00:57:13+0700",
    comments = "version: 1.6.0, compiler: Eclipse JDT (IDE) 3.44.0.v20251118-1623, environment: Java 21.0.9 (Eclipse Adoptium)"
)
@Component
public class MediaMapperImpl implements MediaMapper {

    @Override
    public MediaDTO toDTO(Media media) {
        if ( media == null ) {
            return null;
        }

        UUID mediaId = null;
        UUID targetId = null;
        MediaTargetType targetType = null;
        MediaType type = null;
        String objectKey = null;
        Integer orderIndex = null;

        mediaId = media.getMediaId();
        targetId = media.getTargetId();
        targetType = media.getTargetType();
        type = media.getType();
        objectKey = media.getObjectKey();
        orderIndex = media.getOrderIndex();

        MediaDTO mediaDTO = new MediaDTO( mediaId, targetId, targetType, type, objectKey, orderIndex );

        return mediaDTO;
    }
}
