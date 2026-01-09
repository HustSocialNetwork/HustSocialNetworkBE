package vn.hust.social.backend.mapper;

import org.mapstruct.Mapper;
import vn.hust.social.backend.dto.MediaDTO;
import vn.hust.social.backend.entity.media.Media;

@Mapper(componentModel = "spring")
public interface MediaMapper {
    MediaDTO toDTO(Media media);
}
